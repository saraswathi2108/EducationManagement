package com.project.student.education.service;

import com.project.student.education.DTO.*;
import com.project.student.education.entity.ExamMaster;
import com.project.student.education.entity.ExamRecord;
import com.project.student.education.entity.IdGenerator;
import com.project.student.education.entity.Student;
import com.project.student.education.enums.ExamAttendanceStatus;
import com.project.student.education.enums.ExamResultStatus;
import com.project.student.education.enums.ExamStatus;
import com.project.student.education.repository.ClassSectionRepository;
import com.project.student.education.repository.ExamMasterRepository;
import com.project.student.education.repository.ExamRecordRepository;
import com.project.student.education.repository.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamService {

    private final IdGenerator idGenerator;
    private final ModelMapper modelMapper;
    private final ExamMasterRepository examRepo;
    private final ExamRecordRepository examRecordRepo;

    private final ClassSectionRepository classSectionRepo;
    private final SubjectRepository subjectRepo;

    public ExamMasterDTO createExam(ExamMasterDTO examMasterDTO) {
        if (examRepo.existsByExamNameAndAcademicYear((examMasterDTO.getExamName()), examMasterDTO.getAcademicYear())) {
            throw new RuntimeException("Exam already exists for this academic year");
        }
        String examId = idGenerator.generateId("EXM");
        String username = getCurrentUser();

        ExamMaster exam = modelMapper.map(examMasterDTO, ExamMaster.class);
        exam.setExamId(examId);
        exam.setStatus(ExamStatus.CREATED);
        exam.setCreatedBy(username);

        return modelMapper.map(examRepo.save(exam), ExamMasterDTO.class);
    }

    public List<ExamMasterDTO> getAllExams() {
        return examRepo.findAll()
                .stream()
                .map(exam -> modelMapper.map(exam, ExamMasterDTO.class))
                .collect(Collectors.toList());
    }

    public ExamMasterDTO getExamById(String examId) {
        ExamMaster exam = examRepo.findById(examId)
                .orElseThrow(() -> new EntityNotFoundException("Exam with id " + examId + " not found"));
        return modelMapper.map(exam, ExamMasterDTO.class);
    }

    public ExamMasterDTO updateExamStatus(String examId, UpdateExamStatusRequest request) {
        ExamMaster exam = examRepo.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        exam.setStatus(request.getStatus());
        exam.setUpdatedBy(getCurrentUser());
        exam.setUpdatedAt(LocalDateTime.now());

        return modelMapper.map(examRepo.save(exam), ExamMasterDTO.class);
    }


    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "SYSTEM";
    }


    @Transactional
    public void enterMarks(String subjectId, SubjectMarksEntryRequest request) {

        for (MarksEntryRequest entry : request.getEntries()) {

            ExamRecord record = examRecordRepo
                    .findByExamIdAndStudentIdAndSubjectId(
                            request.getExamId(),
                            entry.getStudentId(),
                            subjectId
                    )
                    .orElseThrow(() -> new RuntimeException(
                            "Record not found for student: " + entry.getStudentId()
                    ));

            // Auto Detect Present / Absent
            boolean hasMarks =
                    (entry.getPaperObtained() != null && entry.getPaperObtained() > 0) ||
                            (entry.getAssignmentObtained() != null && entry.getAssignmentObtained() > 0);

            ExamAttendanceStatus attendance =
                    hasMarks ? ExamAttendanceStatus.PRESENT : ExamAttendanceStatus.ABSENT;

            record.setAttendanceStatus(attendance);

            if (attendance == ExamAttendanceStatus.PRESENT) {
                record.setPaperObtained(entry.getPaperObtained());
                record.setPaperTotal(entry.getPaperTotal());
                record.setAssignmentObtained(entry.getAssignmentObtained());
                record.setAssignmentTotal(entry.getAssignmentTotal());
            } else {
                record.setPaperObtained(0.0);
                record.setAssignmentObtained(0.0);
            }

            record.setRemarks(entry.getRemarks());
            record.setResultStatus(ExamResultStatus.ENTERED);
            record.setUpdatedAt(LocalDateTime.now());

            examRecordRepo.save(record);
        }
    }





    @Transactional
    public void publishResult(String examId, String classSectionId, String adminName) {

        List<ExamRecord> records =
                examRecordRepo.findByExamIdAndClassSectionId(examId, classSectionId);

        if (records.isEmpty())
            throw new RuntimeException("No records found for publishing");


        boolean invalid = records.stream().anyMatch(r ->
                r.getAttendanceStatus() == null ||
                        (r.getAttendanceStatus() == ExamAttendanceStatus.PRESENT &&
                                r.getPaperObtained() == null)
        );

        if (invalid)
            throw new RuntimeException("Marks or attendance missing! Cannot publish.");


        for (ExamRecord r : records) {
            r.setResultStatus(ExamResultStatus.PUBLISHED);
            r.setPublishedBy(adminName);
            r.setUpdatedAt(LocalDateTime.now());
        }

        examRecordRepo.saveAll(records);
    }
    public StudentExamResultDTO getStudentResult(String examId, String studentId, String classSectionId) {

        List<ExamRecord> records =
                examRecordRepo.findByExamIdAndStudentId(examId, studentId);

        if (records.isEmpty())
            throw new RuntimeException("No records found for student.");

        boolean published = records.stream()
                .allMatch(r -> r.getResultStatus() == ExamResultStatus.PUBLISHED);

        if (!published)
            throw new RuntimeException("Result not yet published by admin.");

        double totalObt = 0;
        double totalMax = 0;

        List<SubjectResultDTO> subjects = new ArrayList<>();

        for (ExamRecord r : records) {

            double paper = r.getPaperObtained();
            double paperMax = r.getPaperTotal();

            double assign = r.getAssignmentObtained();
            double assignMax = r.getAssignmentTotal();

            double finalTotal = paper + assign;
            double finalMax = paperMax + assignMax;

            totalObt += finalTotal;
            totalMax += finalMax;

            // ⭐ SAFE PASS MARKS CHECK
            Double passMarks = r.getPassMarks();
            String status;

            if (passMarks == null) {
                status = "NA"; // No pass criteria set
            } else {
                status = finalTotal >= passMarks ? "PASS" : "FAIL";
            }

            subjects.add(
                    SubjectResultDTO.builder()
                            .subjectId(r.getSubjectId())
                            .subjectName(r.getSubject().getSubjectName())
                            .paperObtained(paper)
                            .paperTotal(paperMax)
                            .assignmentObtained(assign)
                            .assignmentTotal(assignMax)
                            .subjectTotalObtained(finalTotal)
                            .subjectTotalMax(finalMax)
                            .attendanceStatus(r.getAttendanceStatus())
                            .status(status)      // ⭐ Updated here
                            .build()
            );
        }

        Integer rank = examRecordRepo.calculateRank(examId, classSectionId, studentId);

        return StudentExamResultDTO.builder()
                .examId(examId)
                .examName(examRepo.findById(examId).get().getExamName())
                .studentId(studentId)
                .studentName(records.get(0).getStudent().getFullName())
                .className(records.get(0).getClassSection().getClassName())
                .section(records.get(0).getClassSection().getSection())
                .totalMarksObtained(totalObt)
                .totalMarksMax(totalMax)
                .percentage((totalObt / totalMax) * 100)
                .rank(rank)
                .subjects(subjects)
                .build();
    }

    public List<StudentExamRecordDTO> getExamRecords(String examId, String classSectionId, String subjectId) {

        List<ExamRecord> records =
                examRecordRepo.findByExamIdAndClassSectionIdAndSubjectId(
                        examId, classSectionId, subjectId
                );

        return records.stream().map(r ->
                StudentExamRecordDTO.builder()
                        .recordId(r.getRecordId())
                        .studentId(r.getStudentId())
                        .fullName(r.getStudent().getFullName())
                        .rollNumber(r.getStudent().getRollNumber())
                        .build()
        ).toList();
    }

    public List<StudentExamResultDTO> getClassExamResults(String examId, String classSectionId) {

        // 1. Fetch all records for this Exam & Class
        List<ExamRecord> records = examRecordRepo.findByExamIdAndClassSectionId(examId, classSectionId);

        if (records.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. Group Records by Student ID
        Map<String, List<ExamRecord>> studentRecordsMap = records.stream()
                .collect(Collectors.groupingBy(ExamRecord::getStudentId));

        List<StudentExamResultDTO> classResults = new ArrayList<>();

        // 3. Process Each Student
        for (Map.Entry<String, List<ExamRecord>> entry : studentRecordsMap.entrySet()) {
            String studentId = entry.getKey();
            List<ExamRecord> myRecords = entry.getValue();

            double totalObtained = 0;
            double totalMax = 0;
            List<SubjectResultDTO> subjects = new ArrayList<>();

            Student student = myRecords.get(0).getStudent();

            for (ExamRecord r : myRecords) {
                double pObt = r.getPaperObtained() != null ? r.getPaperObtained() : 0;
                double pTot = r.getPaperTotal() != null ? r.getPaperTotal() : 0;
                double aObt = r.getAssignmentObtained() != null ? r.getAssignmentObtained() : 0;
                double aTot = r.getAssignmentTotal() != null ? r.getAssignmentTotal() : 0;

                double subTotal = pObt + aObt;
                double subMax = pTot + aTot;

                totalObtained += subTotal;
                totalMax += subMax;
                String status = "FAIL";
                if (r.getPassMarks() != null && subTotal >= r.getPassMarks()) {
                    status = "PASS";
                } else if (r.getPassMarks() == null) {
                    status = (subTotal / subMax >= 0.35) ? "PASS" : "FAIL";
                }

                subjects.add(SubjectResultDTO.builder()
                        .subjectId(r.getSubjectId())
                        .subjectName(r.getSubject().getSubjectName())
                        .paperObtained(pObt)
                        .paperTotal(pTot)
                        .assignmentObtained(aObt)
                        .assignmentTotal(aTot)
                        .subjectTotalObtained(subTotal)
                        .subjectTotalMax(subMax)
                        .attendanceStatus(r.getAttendanceStatus())
                        .status(status)
                        .build());
            }

            double percentage = totalMax > 0 ? (totalObtained / totalMax) * 100 : 0;

            classResults.add(StudentExamResultDTO.builder()
                    .studentId(studentId)
                    .studentName(student.getFullName())
                    .rollNumber(student.getRollNumber()) // Make sure to add this field to DTO if needed
                    .totalMarksObtained(totalObtained)
                    .totalMarksMax(totalMax)
                    .percentage(percentage)
                    .subjects(subjects)
                    .build());
        }

        classResults.sort((a, b) -> Double.compare(b.getTotalMarksObtained(), a.getTotalMarksObtained()));

        int rank = 1;
        for (StudentExamResultDTO dto : classResults) {
            dto.setRank(rank++);
        }

        return classResults;
    }
}