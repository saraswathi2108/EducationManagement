package com.project.student.education.service;

import com.project.student.education.DTO.*;
import com.project.student.education.entity.*;
import com.project.student.education.enums.FeeStatus;
import com.project.student.education.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeeService {

    private final IdGenerator idGenerator;
    private final StudentFeeRepository feeRepo;
    private final PaymentRepository paymentRepo;
    private final AdmissionRepository admissionRepo;
    private final StudentRepository studentRepository;

    private final ClassSectionRepository classSectionRepository;

    public StudentFee createFee(CreateFeeRequest req) {
        StudentFee fee = StudentFee.builder()
                .feeId(idGenerator.generateId("FEE"))
                .studentId(req.getStudentId())
                .feeName(req.getFeeName())
                .amount(req.getAmount())
                .amountPaid(0.0)
                .dueDate(req.getDueDate())
                .status(FeeStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        return feeRepo.save(fee);
    }

    public List<StudentFee> bulkCreate(List<CreateFeeRequest> list) {
        List<StudentFee> fees = list.stream()
                .map(r -> StudentFee.builder()
                        .feeId(idGenerator.generateId("FEE"))
                        .studentId(r.getStudentId())
                        .feeName(r.getFeeName())
                        .amount(r.getAmount())
                        .amountPaid(0.0)
                        .dueDate(r.getDueDate())
                        .status(FeeStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .build()
                )
                .collect(Collectors.toList());

        return feeRepo.saveAll(fees);
    }

    public List<StudentFeeDTO> getAllFees(String studentId) {
        return feeRepo.findByStudentId(studentId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private StudentFeeDTO toDto(StudentFee fee) {
        StudentFeeDTO dto = new StudentFeeDTO();
        dto.setFeeId(fee.getFeeId());
        dto.setFeeName(fee.getFeeName());
        dto.setAmount(fee.getAmount());
        dto.setAmountPaid(fee.getAmountPaid());
        dto.setDueDate(fee.getDueDate());
        dto.setStatus(fee.getStatus().name());
        return dto;
    }

    public List<Payment> getAllPayments() {
        return paymentRepo.findAll();
    }

    public FeeSummaryDTO getSummary(String studentId) {

        // Get student always from Student table
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        double totalFee = student.getTotalFee() != null ? student.getTotalFee() : 0.0;

        // Calculate paid amount
        double paid = feeRepo.findByStudentId(studentId).stream()
                .mapToDouble(f -> f.getAmountPaid() == null ? 0.0 : f.getAmountPaid())
                .sum();

        return new FeeSummaryDTO(totalFee, paid, totalFee - paid);
    }



    public List<StudentFeeDTO> getPendingFees(String studentId) {
        List<FeeStatus> statuses = List.of(FeeStatus.PENDING, FeeStatus.PARTIAL, FeeStatus.OVERDUE);

        return feeRepo.findByStudentIdAndStatusIn(studentId, statuses)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Payment pay(CreatePaymentRequest req) {
        StudentFee fee = feeRepo.findById(req.getFeeId())
                .orElseThrow(() -> new RuntimeException("Fee not found"));

        Payment payment = Payment.builder()
                .paymentId(idGenerator.generateId("PAY"))
                .feeId(fee.getFeeId())
                .studentId(fee.getStudentId())
                .amount(req.getAmount())
                .paymentDate(LocalDateTime.now())
                .method(req.getMethod())
                .transactionRef("TXN-" + UUID.randomUUID())
                .build();

        paymentRepo.save(payment);

        double newPaid = fee.getAmountPaid() + req.getAmount();
        fee.setAmountPaid(newPaid);

        if (newPaid >= fee.getAmount()) fee.setStatus(FeeStatus.PAID);
        else fee.setStatus(FeeStatus.PARTIAL);

        feeRepo.save(fee);

        return payment;
    }

    public List<Payment> getPaymentHistory(String studentId) {
        return paymentRepo.findByStudentId(studentId);
    }

    public FeeDashboardResponse getFeeDashboard(String studentId) {

        FeeSummaryDTO summary = getSummary(studentId);
        List<StudentFeeDTO> pending = getPendingFees(studentId);
        List<StudentFeeDTO> all = getAllFees(studentId);
        List<Payment> history = getPaymentHistory(studentId);

        return FeeDashboardResponse.builder()
                .summary(summary)
                .pendingFees(pending)
                .allFees(all)
                .paymentHistory(history)
                .build();
    }


    public ClassFeeResponse createFeeForClass(ClassFeeRequest req) {

        List<Student> students =
                studentRepository.findByClassSection_ClassSectionId(req.getClassSectionId());

        if (students.isEmpty()) {
            throw new RuntimeException("No students found for class " + req.getClassSectionId());
        }

        for (Student s : students) {

            StudentFee fee = StudentFee.builder()
                    .feeId("FEE-" + UUID.randomUUID())
                    .studentId(s.getStudentId())
                    .feeName(req.getFeeName())
                    .amount(req.getAmount())
                    .amountPaid(0.0)
                    .dueDate(req.getDueDate())
                    .status(FeeStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .build();

            feeRepo.save(fee);
        }

        return ClassFeeResponse.builder()
                .classSectionId(req.getClassSectionId())
                .feeName(req.getFeeName())
                .amount(req.getAmount())
                .totalStudents(students.size())
                .status("SUCCESS")
                .build();
    }
    public List<ClassFeeStatsDTO> getAllClassesFeeStats() {
        List<ClassSection> allSections = classSectionRepository.findAll();
        return allSections.stream().map(sec -> {
            Double totalExpected = studentRepository.getTotalFeeByClass(sec.getClassSectionId());
            Double totalPaid = feeRepo.getTotalPaidByClass(sec.getClassSectionId());
            if(totalExpected == null) totalExpected = 0.0;
            if(totalPaid == null) totalPaid = 0.0;
            return ClassFeeStatsDTO.builder()
                    .classSectionId(sec.getClassSectionId())
                    .className(sec.getClassName())
                    .section(sec.getSection())
                    .totalExpectedFee(totalExpected)
                    .totalCollectedFee(totalPaid)
                    .totalPendingFee(totalExpected - totalPaid)
                    .build();
        }).collect(Collectors.toList());
    }

    // 2. Get Students List with Fee Status for a Specific Class
    public List<StudentFeeStatusDTO> getClassStudentFeeStatus(String classSectionId) {
        List<Student> students = studentRepository.findByClassSection_ClassSectionId(classSectionId);
        return students.stream().map(s -> {
            Double totalFee = s.getTotalFee() != null ? s.getTotalFee() : 0.0;
            // Calculate total paid by this student from feeRepo
            Double paid = feeRepo.findByStudentId(s.getStudentId()).stream()
                    .mapToDouble(f -> f.getAmountPaid() == null ? 0.0 : f.getAmountPaid())
                    .sum();
            Double balance = totalFee - paid;
            String status = (balance <= 0 && totalFee > 0) ? "PAID" : (paid > 0 ? "PARTIAL" : "PENDING");
            return StudentFeeStatusDTO.builder()
                    .studentId(s.getStudentId())
                    .studentName(s.getFullName())
                    .rollNumber(s.getRollNumber())
                    .totalFee(totalFee)
                    .paidAmount(paid)
                    .balanceAmount(balance)
                    .status(status)
                    .build();
        }).collect(Collectors.toList());
    }




}