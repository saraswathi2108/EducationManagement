package com.project.student.education.service;

import com.project.student.education.DTO.*;
import com.project.student.education.entity.ExamMaster;
import com.project.student.education.entity.ExamRecord;
import com.project.student.education.entity.IdGenerator;
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

            if (!record.getSubjectId().equals(subjectId)) {
                throw new RuntimeException(
                        "Subject mismatch: expected " + subjectId +
                                " but found " + record.getSubjectId()
                );
            }

            ExamAttendanceStatus attendance =
                    ExamAttendanceStatus.valueOf(entry.getAttendanceStatus());
            record.setAttendanceStatus(attendance);

            switch (attendance) {
                case PRESENT -> {
                    record.setPaperObtained(entry.getPaperObtained());
                    record.setPaperTotal(entry.getPaperTotal());

                    record.setAssignmentObtained(entry.getAssignmentObtained());
                    record.setAssignmentTotal(entry.getAssignmentTotal());
                }

                case ABSENT, MALPRACTICE, NOT_ALLOWED, DNR -> {
                    record.setPaperObtained(0.0);
                    record.setAssignmentObtained(0.0);
                }
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
                            .attendanceStatus(r.getAttendanceStatus().name())
                            .status(finalTotal >= r.getPassMarks() ? "PASS" : "FAIL")
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

}