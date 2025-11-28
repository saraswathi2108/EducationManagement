package com.project.student.education.service;

import com.project.student.education.DTO.*;
import com.project.student.education.entity.*;
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

    private final NotificationService notificationService;


    @Transactional
    public ExamRecordDTO scheduleOne(ExamRecordDTO dto) {

        validateScheduleInput(dto);

        if (recordRepo.existsByExamIdAndClassSectionIdAndSubjectIdAndStudentId(
                dto.getExamId(), dto.getClassSectionId(), dto.getSubjectId(), dto.getStudentId())) {
            throw new RuntimeException("Exam already scheduled for this student & subject.");
        }

        ExamRecord record = mapper.map(dto, ExamRecord.class);
        record.setCreatedAt(LocalDateTime.now());
        record.setEnteredBy(getCurrentUser());

        ExamRecord saved = recordRepo.save(record);

        String message = "Your exam for subject " + dto.getSubjectId() +
                " is scheduled on " + dto.getExamDate() +
                " from " + dto.getStartTime() + " to " + dto.getEndTime() + ".";
        notificationService.sendNotification(
                dto.getStudentId(),
                "Exam Scheduled",
                message,
                "EXAM"
        );

        return mapper.map(saved, ExamRecordDTO.class);
    }


    @Transactional
    public List<ExamRecordDTO> scheduleBulk(BulkScheduleRequest req) {

        if (!examRepo.existsById(req.getExamId())) {
            throw new EntityNotFoundException("Exam not found: " + req.getExamId());
        }

        if (!subjectRepo.existsById(req.getSubjectId())) {
            throw new EntityNotFoundException("Subject not found: " + req.getSubjectId());
        }

        if (req.getClassSectionId() == null || req.getClassSectionId().isEmpty()) {
            throw new IllegalArgumentException("At least one classSectionId is required.");
        }

        List<ExamRecordDTO> output = new ArrayList<>();
        boolean inserted = false;

        for (String classId : req.getClassSectionId()) {

            if (!classRepo.existsById(classId)) {
                throw new EntityNotFoundException("Class not found: " + classId);
            }

            List<String> studentIds = studentRepo.findStudentIdsByClassSectionId(classId);

            if (studentIds.isEmpty()) continue;

            for (String studentId : studentIds) {

                boolean exists = recordRepo.existsByExamIdAndClassSectionIdAndSubjectIdAndStudentId(
                        req.getExamId(), classId, req.getSubjectId(), studentId
                );

                if (exists) continue;

                inserted = true;

                ExamRecord record = ExamRecord.builder()
                        .examId(req.getExamId())
                        .classSectionId(classId)
                        .subjectId(req.getSubjectId())
                        .studentId(studentId)
                        .examDate(req.getExamDate())
                        .startTime(req.getStartTime())
                        .endTime(req.getEndTime())
                        .createdAt(LocalDateTime.now())
                        .enteredBy(getCurrentUser())
                        .build();

                ExamRecord saved = recordRepo.save(record);
                output.add(mapper.map(saved, ExamRecordDTO.class));

                String message = "New exam scheduled for subject " + req.getSubjectId() +
                        " on " + req.getExamDate() +
                        " from " + req.getStartTime() + " to " + req.getEndTime() + ".";
                notificationService.sendNotification(
                        studentId,
                        "Exam Scheduled",
                        message,
                        "EXAM"
                );
            }
        }

        if (!inserted) {
            throw new IllegalStateException("Exam schedule already exists for all selected classes.");
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
                        .map(Subject::getSubjectName)
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


    @Transactional
    public List<ExamRecordDTO> scheduleComprehensive(ComprehensiveScheduleRequest req) {

        if (!examRepo.existsById(req.getExamId())) {
            throw new EntityNotFoundException("Exam not found: " + req.getExamId());
        }

        if (req.getClassSectionIds() == null || req.getClassSectionIds().isEmpty()) {
            throw new IllegalArgumentException("At least one classSectionId is required.");
        }

        if (req.getSchedules() == null || req.getSchedules().isEmpty()) {
            throw new IllegalArgumentException("At least one schedule is required.");
        }

        List<ExamRecordDTO> output = new ArrayList<>();
        boolean inserted = false;

        for (String classId : req.getClassSectionIds()) {

            if (!classRepo.existsById(classId)) {
                throw new EntityNotFoundException("Class not found: " + classId);
            }

            List<String> studentIds = studentRepo.findStudentIdsByClassSectionId(classId);
            if (studentIds.isEmpty()) continue;

            for (ComprehensiveScheduleRequest.ScheduleEntry entry : req.getSchedules()) {

                if (!subjectRepo.existsById(entry.getSubjectId())) {
                    throw new EntityNotFoundException("Subject not found: " + entry.getSubjectId());
                }

                boolean timeClashExists = recordRepo
                        .existsByExamIdAndClassSectionIdAndExamDateAndStartTimeAndEndTime(
                                req.getExamId(),
                                classId,
                                entry.getExamDate(),
                                entry.getStartTime(),
                                entry.getEndTime()
                        );

                if (timeClashExists) {
                    throw new IllegalStateException(
                            "A subject is already scheduled for class " + classId +
                                    " on " + entry.getExamDate() +
                                    " at " + entry.getStartTime() + " - " + entry.getEndTime()
                    );
                }

                for (String studentId : studentIds) {

                    boolean existsForStudent = recordRepo
                            .existsByExamIdAndClassSectionIdAndSubjectIdAndStudentId(
                                    req.getExamId(),
                                    classId,
                                    entry.getSubjectId(),
                                    studentId
                            );

                    if (existsForStudent) continue;

                    inserted = true;

                    ExamRecord record = ExamRecord.builder()
                            .examId(req.getExamId())
                            .classSectionId(classId)
                            .subjectId(entry.getSubjectId())
                            .studentId(studentId)
                            .examDate(entry.getExamDate())
                            .startTime(entry.getStartTime())
                            .endTime(entry.getEndTime())
                            .createdAt(LocalDateTime.now())
                            .enteredBy(getCurrentUser())
                            .build();

                    ExamRecord saved = recordRepo.save(record);
                    output.add(mapper.map(saved, ExamRecordDTO.class));

                    String message = "New exam scheduled for subject " + entry.getSubjectId() +
                            " on " + entry.getExamDate() +
                            " from " + entry.getStartTime() + " to " + entry.getEndTime() + ".";
                    notificationService.sendNotification(
                            studentId,
                            "Exam Timetable Updated",
                            message,
                            "EXAM"
                    );
                }
            }
        }

        if (!inserted) {
            throw new IllegalStateException("Schedule already exists or time clash found.");
        }

        return output;
    }


    public List<TimetableDayDTO> getTeacherClassExamTimetable(String examId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Teacher teacher = teacherRepo.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("Teacher not found for logged-in user"));

        String teacherId = teacher.getTeacherId();

        ClassSection classSection = classRepo.findByClassTeacher_TeacherId(teacherId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No class assigned to this teacher"));

        List<TimetableDayDTO> days = getTimetable(examId, classSection.getClassSectionId());

        for (TimetableDayDTO day : days) {

            day.setClassName(classSection.getClassName());
            day.setSectionName(classSection.getSection());

            if (day.getSubjects() != null) {
                day.getSubjects().forEach(sub -> {
                    sub.setClassName(classSection.getClassName());
                    sub.setSectionName(classSection.getSection());
                });
            }
        }

        return days;
    }


    public List<TimetableDayDTO> getTeacherSubjectExamTimetable(String examId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();

        Teacher teacher = teacherRepo.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("Teacher not found for logged-in user"));

        String teacherId = teacher.getTeacherId();
        List<ClassSubjectMapping> mappings =
                classSubjectRepo.findByTeacher_TeacherId(teacherId);

        if (mappings.isEmpty()) {
            throw new RuntimeException("No subjects assigned to this teacher");
        }

        Map<LocalDate, TimetableDayDTO> map = new LinkedHashMap<>();

        for (ClassSubjectMapping m : mappings) {

            String classSectionId = m.getClassSection().getClassSectionId();
            String subjectId = m.getSubject().getSubjectId();

            List<ExamRecord> subjectRecords =
                    recordRepo.findByExamIdAndClassSectionIdAndSubjectId(
                            examId, classSectionId, subjectId
                    );

            for (ExamRecord r : subjectRecords) {

                LocalDate examDate = r.getExamDate();
                if (examDate == null) continue;

                map.computeIfAbsent(examDate, d -> {
                    TimetableDayDTO dto = new TimetableDayDTO();
                    dto.setExamDate(d);
                    dto.setDayName(d.getDayOfWeek().name());
                    dto.setClassName(m.getClassSection().getClassName());
                    dto.setSectionName(m.getClassSection().getSection());
                    dto.setSubjects(new ArrayList<>());
                    return dto;
                });

                TimetableSubjectDTO subDto = new TimetableSubjectDTO();
                subDto.setSubjectId(r.getSubjectId());
                subDto.setSubjectName(r.getSubject().getSubjectName());
                subDto.setStartTime(r.getStartTime().toString());
                subDto.setEndTime(r.getEndTime().toString());
                subDto.setRoomNumber(r.getRoomNumber());
                if (r.getClassSection() != null) {
                    subDto.setClassName(r.getClassSection().getClassName());
                    subDto.setSectionName(r.getClassSection().getSection());
                }
                map.get(examDate).getSubjects().add(subDto);
            }
        }

        return new ArrayList<>(map.values());
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
}
