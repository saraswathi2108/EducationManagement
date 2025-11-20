package com.project.student.education.service;

import com.project.student.education.DTO.*;
import com.project.student.education.entity.ExamRecord;
import com.project.student.education.enums.RecordStatus;
import com.project.student.education.repository.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamSchedulingService {

    private final ModelMapper mapper;
    private final ExamMasterRepository examRepo;
    private final ClassSectionRepository classRepo;
    private final SubjectRepository subjectRepo;
    private final StudentRepository studentRepo;
    private final ExamRecordRepository recordRepo;
    private final ClassSubjectMappingRepository classSubjectRepo;
    private final TeacherRepository teacherRepo;

    @Transactional
    public ExamRecordDTO scheduleOne(ExamRecordDTO dto) {

        validateScheduleInput(dto);

        if (recordRepo.existsByExamIdAndClassSectionIdAndSubjectIdAndStudentId(
                dto.getExamId(), dto.getClassSectionId(), dto.getSubjectId(), dto.getStudentId())) {
            throw new RuntimeException("Exam already scheduled for this student & subject.");
        }

        ExamRecord record = mapper.map(dto, ExamRecord.class);
        record.setStatus(RecordStatus.SCHEDULED.name());
        record.setCreatedAt(LocalDateTime.now());
        record.setEnteredBy(getCurrentUser());

        ExamRecord saved = recordRepo.save(record);
        return mapper.map(saved, ExamRecordDTO.class);
    }


    @Transactional
    public List<ExamRecordDTO> scheduleBulk(BulkScheduleRequest req) {

        if (!examRepo.existsById(req.getExamId()))
            throw new EntityNotFoundException("Exam not found: " + req.getExamId());

        if (!classRepo.existsById(req.getClassSectionId()))
            throw new EntityNotFoundException("Class not found: " + req.getClassSectionId());

        if (!subjectRepo.existsById(req.getSubjectId()))
            throw new EntityNotFoundException("Subject not found: " + req.getSubjectId());

        List<String> studentIds = req.getStudentIds();
        if (studentIds == null || studentIds.isEmpty()) {
            studentIds = studentRepo.findStudentIdsByClassSectionId(req.getClassSectionId());
        }

        if (studentIds == null || studentIds.isEmpty()) {
            throw new EntityNotFoundException(
                    "No students found for class section: " + req.getClassSectionId()
            );
        }


        List<ExamRecordDTO> output = new ArrayList<>();
        boolean atLeastOneInserted = false;

        for (String studentId : studentIds) {

            boolean alreadyExists = recordRepo.existsByExamIdAndClassSectionIdAndSubjectIdAndStudentId(
                    req.getExamId(), req.getClassSectionId(), req.getSubjectId(), studentId
            );

            if (alreadyExists) {
                continue;
            }

            atLeastOneInserted = true;

            ExamRecord record = ExamRecord.builder()
                    .examId(req.getExamId())
                    .classSectionId(req.getClassSectionId())
                    .subjectId(req.getSubjectId())
                    .studentId(studentId)
                    .examDate(req.getExamDate())
                    .startTime(req.getStartTime())
                    .endTime(req.getEndTime())
                    .roomNumber(req.getRoomNumber())
                    .invigilatorId(req.getInvigilatorId())
                    .maxMarks(req.getMaxMarks())
                    .passMarks(req.getPassMarks())
                    .status(RecordStatus.SCHEDULED.name())
                    .createdAt(LocalDateTime.now())
                    .enteredBy(getCurrentUser())
                    .build();

            ExamRecord saved = recordRepo.save(record);
            output.add(mapper.map(saved, ExamRecordDTO.class));
        }

        if (!atLeastOneInserted) {
            throw new IllegalStateException(
                    "Exam schedule already exists for ALL students of this class for subject: "
                            + req.getSubjectId()
            );
        }

        return output;
    }



    @Transactional(readOnly = true)
    public List<TimetableDayDTO> getTimetable(String examId, String classSectionId) {

        List<ExamRecord> records = recordRepo.findByExamIdAndClassSectionId(examId, classSectionId);
        Map<LocalDate, TimetableDayDTO> grouped = new LinkedHashMap<>();

        for (ExamRecord r : records) {
            LocalDate date = r.getExamDate();
            if (date == null) {
                continue;
            }

            grouped.computeIfAbsent(date, d -> {
                TimetableDayDTO dto = new TimetableDayDTO();
                dto.setExamDate(d);
                dto.setDayName(d.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, Locale.ENGLISH));
                dto.setSubjects(new ArrayList<>());
                return dto;
            });

            TimetableSubjectDTO subjectDTO = new TimetableSubjectDTO();
            subjectDTO.setSubjectId(r.getSubjectId());

            String subjectName = null;
            if (r.getSubject() != null) {
                subjectName = r.getSubject().getSubjectName();
            } else if (r.getSubjectId() != null && subjectRepo != null) {
                subjectName = subjectRepo.findById(r.getSubjectId())
                        .map(s -> s.getSubjectName())
                        .orElse(null);
            }
            subjectDTO.setSubjectName(subjectName);


            subjectDTO.setStartTime(r.getStartTime() != null ? r.getStartTime().toString() : null);
            subjectDTO.setEndTime(r.getEndTime() != null ? r.getEndTime().toString() : null);
            subjectDTO.setRoomNumber(r.getRoomNumber());

            grouped.get(date).getSubjects().add(subjectDTO);
        }

        return new ArrayList<>(grouped.values());
    }



    private void validateScheduleInput(ExamRecordDTO dto) {
        if (!examRepo.existsById(dto.getExamId()))
            throw new EntityNotFoundException("Exam not found");

        if (!classRepo.existsById(dto.getClassSectionId()))
            throw new EntityNotFoundException("Class not found");

        if (!subjectRepo.existsById(dto.getSubjectId()))
            throw new EntityNotFoundException("Subject not found");

        if (!studentRepo.existsById(dto.getStudentId()))
            throw new EntityNotFoundException("Student not found");
    }

    private String getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "SYSTEM";
    }

    public SubjectMarksEntryRequest enterMarks(SubjectMarksEntryRequest request, String subjectId) {

        subjectRepo.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("Subject not found: " + subjectId));

                classSubjectRepo
                        .findByClassSection_ClassSectionIdAndSubject_SubjectId(request.getClassSectionId(), subjectId)
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Subject " + subjectId + " does NOT belong to class section " + request.getClassSectionId()
                        ));

        request.setSubjectId(subjectId);

        for (SubjectWiseMarksEntryDTO entry : request.getEntries()) {

            ExamRecord record = recordRepo.findByExamIdAndStudentIdAndSubjectId(
                    request.getExamId(),
                    entry.getStudentId(),
                    subjectId
            ).orElseThrow(() -> new EntityNotFoundException(
                    "Exam record not found for student " + entry.getStudentId()
            ));

            record.setMarksObtained(entry.getMarksObtained());
            record.setAttendanceStatus(entry.getAttendanceStatus());
            record.setRemarks(entry.getRemarks());
            record.setStatus(RecordStatus.MARKS_ENTERED.name());
            record.setUpdatedAt(LocalDateTime.now());
            record.setEnteredBy(getCurrentUser());

            recordRepo.save(record);
        }

        return request;
    }
}
