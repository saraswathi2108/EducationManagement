package com.project.student.education.service;

import com.project.student.education.DTO.CreateFeeRequest;
import com.project.student.education.DTO.CreatePaymentRequest;
import com.project.student.education.DTO.FeeSummaryDTO;
import com.project.student.education.DTO.StudentFeeDTO;
import com.project.student.education.entity.Admission;
import com.project.student.education.entity.IdGenerator;
import com.project.student.education.entity.Payment;
import com.project.student.education.entity.StudentFee;
import com.project.student.education.enums.FeeStatus;
import com.project.student.education.repository.AdmissionRepository;
import com.project.student.education.repository.PaymentRepository;
import com.project.student.education.repository.StudentFeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeeService {

    private final IdGenerator idGenerator;
    private final StudentFeeRepository feeRepo;
    private final PaymentRepository paymentRepo;
    private final AdmissionRepository admissionRepo;

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
        Admission admission = admissionRepo.findByStudent_StudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Admission not found"));

        double totalFee = admission.getTotalFee() == null ? 0.0 : admission.getTotalFee();

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
}
