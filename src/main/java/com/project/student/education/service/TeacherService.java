package com.project.student.education.service;

import com.project.student.education.DTO.ClassSectionMiniDTO;
import com.project.student.education.DTO.TeacherDTO;
import com.project.student.education.DTO.TeacherWeeklyTimetableDTO;
import com.project.student.education.entity.*;
import com.project.student.education.enums.Role;
import com.project.student.education.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final IdGenerator idGenerator;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final ClassSectionRepository classSectionRepository;
    private final TimetableRepository timetableRepository;
    private final SubjectRepository subjectRepository;
    private final JavaMailSender mailSender;

    private final ClassSubjectMappingRepository classSubjectMappingRepository;

    public TeacherDTO addTeacher(TeacherDTO dto) {
        if (teacherRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String teacherId = idGenerator.generateId("TCH");
        String rawPassword = generateDefaultPassword(teacherId);

        User user = User.builder()
                .username(teacherId)
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.ROLE_TEACHER)
                .email(dto.getEmail())
                .build();

        userRepository.save(user);

        Teacher teacher = Teacher.builder()
                .teacherId(teacherId)
                .teacherName(dto.getTeacherName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .qualification(dto.getQualification())
                .gender(dto.getGender())
                .experience(dto.getExperience())
                .address(dto.getAddress())
                .user(user)
                .build();

        if (dto.getSubjectIds() != null && !dto.getSubjectIds().isEmpty()) {
            teacher.setSubjectIds(dto.getSubjectIds());
        }

        teacherRepository.save(teacher);

        TeacherDTO response = modelMapper.map(teacher, TeacherDTO.class);
        response.setPassword(rawPassword);
        response.setSubjectIds(teacher.getSubjectIds());
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            sendTeacherWelcomeEmail(dto.getEmail(), teacherId, rawPassword);
        }

        return response;
    }

    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAll()
                .stream()
                .map(t -> modelMapper.map(t, TeacherDTO.class))
                .collect(Collectors.toList());
    }

    public TeacherDTO getTeacherById(String teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        return modelMapper.map(teacher, TeacherDTO.class);
    }

    public TeacherDTO updateTeacher(String teacherId, TeacherDTO dto) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        teacher.setTeacherName(dto.getTeacherName());
        teacher.setEmail(dto.getEmail());
        teacher.setPhone(dto.getPhone());
        teacher.setQualification(dto.getQualification());
        teacher.setGender(dto.getGender());
        teacher.setExperience(dto.getExperience());
        teacher.setAddress(dto.getAddress());

        if (dto.getSubjectIds() != null) {
            teacher.setSubjectIds(dto.getSubjectIds());
        }

        teacherRepository.save(teacher);

        TeacherDTO response = modelMapper.map(teacher, TeacherDTO.class);
        response.setSubjectIds(teacher.getSubjectIds());

        return response;
    }

    public String deleteTeacher(String teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        teacherRepository.delete(teacher);
        userRepository.delete(teacher.getUser());

        return "Teacher deleted successfully";
    }

    private String generateDefaultPassword(String teacherId) {
        return "Tch@" + teacherId.substring(teacherId.length() - 4);
    }

    public String assignTeacher(String teacherId, String classSectionId) {
        ClassSection classSection = classSectionRepository.findById(classSectionId)
                .orElseThrow(() -> new RuntimeException("Class section not found"));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        classSection.setClassTeacher(teacher);
        classSectionRepository.save(classSection);
        return "Teacher  " + teacherId + "  assigned to class  " + classSectionId + "  successfully";
    }

    public String updateClassTeacher(String classSectionId, String teacherId) {
        ClassSection section = classSectionRepository.findById(classSectionId)
                .orElseThrow(() -> new RuntimeException("Class section not found"));

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        section.setClassTeacher(teacher);
        classSectionRepository.save(section);

        return "Class teacher updated for class section " + classSectionId +
                " to teacher " + teacherId;
    }

    // 🔥 UPDATED LOGIC: Get Classes for BOTH Class Teacher & Subject Teacher
    public List<ClassSectionMiniDTO> getClassesHandledByTeacher(String teacherId) {

        if (!teacherRepository.existsById(teacherId)) {
            throw new RuntimeException("Teacher not found: " + teacherId);
        }

        // 1. Classes where they are the MAIN CLASS TEACHER
        List<ClassSection> asClassTeacher = classSectionRepository
                .findByClassTeacher_TeacherId(teacherId);

        // 2. Classes where they are assigned as a SUBJECT TEACHER
        // (Requires ClassSubjectMappingRepository)
        List<ClassSubjectMapping> subjectMappings = classSubjectMappingRepository
                .findByTeacher_TeacherId(teacherId);

        List<ClassSection> asSubjectTeacher = subjectMappings.stream()
                .map(ClassSubjectMapping::getClassSection)
                .toList();

        Set<ClassSection> uniqueClasses = new HashSet<>(asClassTeacher);
        uniqueClasses.addAll(asSubjectTeacher);

        // 4. Convert to DTO
        return uniqueClasses.stream()
                .sorted(Comparator.comparing(ClassSection::getClassName)) // Optional: Sort
                .map(sec -> ClassSectionMiniDTO.builder()
                        .classSectionId(sec.getClassSectionId())
                        .className(sec.getClassName())
                        .sectionName(sec.getSection())
                        .academicYear(sec.getAcademicYear())
                        .build()
                ).toList();
    }

    public Long getTeacherCount() {
        return teacherRepository.countTeachers();
    }

    public TeacherWeeklyTimetableDTO getTeacherWeeklyTimetable(String teacherId, LocalDate weekReference) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        List<Timetable> list = timetableRepository
                .findByTeacher_TeacherIdOrderByDayAscStartTimeAsc(teacherId);

        Map<String, LocalDate> weekDates = getWeekDates(weekReference);

        Map<String, List<Timetable>> grouped = list.stream()
                .collect(Collectors.groupingBy(Timetable::getDay, LinkedHashMap::new, Collectors.toList()));

        List<TeacherWeeklyTimetableDTO.DayEntry> weekly = new ArrayList<>();

        for (String dayName : List.of(
                "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY",
                "FRIDAY", "SATURDAY"
        )) {
            LocalDate date = weekDates.get(dayName);
            List<Timetable> periods = grouped.getOrDefault(dayName, Collections.emptyList());

            List<TeacherWeeklyTimetableDTO.Period> periodDTOs = periods.stream()
                    .sorted(Comparator.comparing(Timetable::getStartTime))
                    .map(t -> TeacherWeeklyTimetableDTO.Period.builder()
                            .date(date.toString())
                            .classSectionId(t.getClassSection().getClassSectionId())
                            .className(t.getClassSection().getClassName())
                            .section(t.getClassSection().getSection())
                            .subjectId(t.getSubject().getSubjectId())
                            .subjectName(t.getSubject().getSubjectName())
                            .startTime(toAmPm(t.getStartTime()))
                            .endTime(toAmPm(t.getEndTime()))
                            .build()
                    ).toList();

            weekly.add(
                    TeacherWeeklyTimetableDTO.DayEntry.builder()
                            .day(dayName)
                            .date(date.toString())
                            .periods(periodDTOs)
                            .build()
            );
        }

        return TeacherWeeklyTimetableDTO.builder()
                .teacherId(teacher.getTeacherId())
                .teacherName(teacher.getTeacherName())
                .weeklyTimetable(weekly)
                .build();
    }

    private Map<String, LocalDate> getWeekDates(LocalDate referenceDate) {
        LocalDate monday = referenceDate.with(java.time.DayOfWeek.MONDAY);
        Map<String, LocalDate> map = new LinkedHashMap<>();
        map.put("MONDAY", monday);
        map.put("TUESDAY", monday.plusDays(1));
        map.put("WEDNESDAY", monday.plusDays(2));
        map.put("THURSDAY", monday.plusDays(3));
        map.put("FRIDAY", monday.plusDays(4));
        map.put("SATURDAY", monday.plusDays(5));
        map.put("SUNDAY", monday.plusDays(6));
        return map;
    }

    private String toAmPm(String time) {
        return LocalTime.parse(time)
                .format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    public TeacherWeeklyTimetableDTO getClassTeacherTimetable(String teacherId, LocalDate weekStart) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        List<ClassSection> sections = classSectionRepository.findByClassTeacher_TeacherId(teacherId);

        if (sections == null || sections.isEmpty()) {
            throw new RuntimeException("This teacher is not a class teacher");
        }

        ClassSection section = sections.get(0);

        List<Timetable> timetable = timetableRepository
                .findByClassSection_ClassSectionIdOrderByDayAscStartTimeAsc(section.getClassSectionId());

        Map<String, LocalDate> weekDates = getWeekDates(weekStart);
        List<TeacherWeeklyTimetableDTO.DayEntry> weekly = new ArrayList<>();

        for (String day : List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY")) {
            LocalDate date = weekDates.get(day);
            List<Timetable> periods = timetable.stream()
                    .filter(t -> t.getDay().equals(day))
                    .toList();

            List<TeacherWeeklyTimetableDTO.Period> periodList = periods.stream()
                    .map(t -> TeacherWeeklyTimetableDTO.Period.builder()
                            .date(date.toString())
                            .classSectionId(section.getClassSectionId())
                            .className(section.getClassName())
                            .section(section.getSection())
                            .subjectId(t.getSubject().getSubjectId())
                            .subjectName(t.getSubject().getSubjectName())
                            .startTime(toAmPm(t.getStartTime()))
                            .endTime(toAmPm(t.getEndTime()))
                            .build()
                    ).toList();

            weekly.add(
                    TeacherWeeklyTimetableDTO.DayEntry.builder()
                            .day(day)
                            .date(date.toString())
                            .periods(periodList)
                            .build()
            );
        }

        return TeacherWeeklyTimetableDTO.builder()
                .teacherId(teacherId)
                .teacherName(teacher.getTeacherName())
                .weeklyTimetable(weekly)
                .build();
    }

    private void sendTeacherWelcomeEmail(String email, String teacherId, String rawPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Teacher Account Created - Login Credentials");
        message.setText(
                "Dear Teacher,\n\n"
                        + "Your teacher account has been created successfully.\n\n"
                        + "Here are your login credentials:\n"
                        + "Teacher ID: " + teacherId + "\n"
                        + "Password: " + rawPassword + "\n\n"
                        + "Please log in and change your password immediately.\n\n"
                        + "Regards,\n"
                        + "School Administration"
        );

        mailSender.send(message);
    }

}