package com.project.student.education.service;

import com.project.student.education.DTO.*;
import com.project.student.education.entity.*;
import com.project.student.education.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AdmissionRepository admissionRepository;

    @Autowired
    private ClassSectionRepository classSectionRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private TeacherRepository teacherRepository;


    @Autowired
    private TimetableRepository timetableRepository;
    @Autowired
    private ClassSubjectMappingRepository classSubjectMappingRepository;

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public StudentDTO getStudentById(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));
        return convertToDTO(student);
    }

    @Transactional
    public StudentDTO updateStudent(String studentId, StudentDTO dto) {

        Student existing = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

        existing.setFullName(dto.getFullName());
        existing.setDateOfBirth(dto.getDateOfBirth());
        existing.setGender(dto.getGender());
        existing.setBloodGroup(dto.getBloodGroup());
        existing.setNationality(dto.getNationality());
        existing.setReligion(dto.getReligion());
        existing.setCategory(dto.getCategory());
        existing.setAadhaarNumber(dto.getAadhaarNumber());
        existing.setAcademicYear(dto.getAcademicYear());
        existing.setJoiningDate(dto.getJoiningDate());
        existing.setRollNumber(dto.getRollNumber());
        existing.setActive(dto.getActive());
        existing.setAddress(dto.getAddress());
        existing.setCity(dto.getCity());
        existing.setState(dto.getState());
        existing.setPincode(dto.getPincode());
        existing.setContactNumber(dto.getContactNumber());
        existing.setEmail(dto.getEmail());
        existing.setFatherName(dto.getFatherName());
        existing.setFatherContact(dto.getFatherContact());
        existing.setMotherName(dto.getMotherName());
        existing.setMotherContact(dto.getMotherContact());
        existing.setGuardianName(dto.getGuardianName());
        existing.setGuardianContact(dto.getGuardianContact());
        existing.setEmergencyContactName(dto.getEmergencyContactName());
        existing.setEmergencyContactNumber(dto.getEmergencyContactNumber());
        existing.setProfileImageUrl(dto.getProfileImageUrl());

        if (dto.getClassSectionId() != null) {
            ClassSection section = classSectionRepository.findById(dto.getClassSectionId())
                    .orElseThrow(() -> new RuntimeException("Class section not found"));
            existing.setClassSection(section);
            existing.setGrade(section.getClassName());
            existing.setSection(section.getSection());
        }

        Student updated = studentRepository.save(existing);
        return convertToDTO(updated);
    }

    @Transactional
    public StudentDTO deleteStudent(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

        if (student.getAdmission() != null) {
            Admission admission = student.getAdmission();
            admission.setStudent(null);
            admissionRepository.save(admission);
        }

        studentRepository.delete(student);
        return convertToDTO(student);
    }

    @Transactional
    public StudentDTO transferStudent(String studentId, String targetClassSectionId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        ClassSection newSection = classSectionRepository.findById(targetClassSectionId)
                .orElseThrow(() -> new RuntimeException("Target class section not found"));

        student.setClassSection(newSection);
        student.setGrade(newSection.getClassName());
        student.setSection(newSection.getSection());
        student.setAcademicYear(newSection.getAcademicYear());
        studentRepository.save(student);

        return convertToDTO(student);
    }

    private StudentDTO convertToDTO(Student s) {

        StudentDTO dto = StudentDTO.builder()
                .studentId(s.getStudentId())
                .fullName(s.getFullName())
                .dateOfBirth(s.getDateOfBirth())
                .gender(s.getGender())
                .bloodGroup(s.getBloodGroup())
                .nationality(s.getNationality())
                .religion(s.getReligion())
                .category(s.getCategory())
                .aadhaarNumber(s.getAadhaarNumber())
                .academicYear(s.getAcademicYear())
                .joiningDate(s.getJoiningDate())
                .rollNumber(s.getRollNumber())
                .address(s.getAddress())
                .city(s.getCity())
                .state(s.getState())
                .pincode(s.getPincode())
                .contactNumber(s.getContactNumber())
                .email(s.getEmail())
                .fatherName(s.getFatherName())
                .fatherContact(s.getFatherContact())
                .motherName(s.getMotherName())
                .motherContact(s.getMotherContact())
                .guardianName(s.getGuardianName())
                .guardianContact(s.getGuardianContact())
                .emergencyContactName(s.getEmergencyContactName())
                .emergencyContactNumber(s.getEmergencyContactNumber())
                .profileImageUrl(s.getProfileImageUrl())
                .active(s.getActive())
                .build();

        if (s.getAdmission() != null)
            dto.setAdmissionNumber(s.getAdmission().getAdmissionNumber());

        if (s.getClassSection() != null) {
            dto.setClassSectionId(s.getClassSection().getClassSectionId());
            dto.setGrade(s.getClassSection().getClassName());
            dto.setSection(s.getClassSection().getSection());
        }

        return dto;
    }

    private SubjectDTO convertSubjectToDTO(Subject subject) {
        SubjectDTO dto = new SubjectDTO();
        dto.setSubjectId(subject.getSubjectId());
        dto.setSubjectName(subject.getSubjectName());
        return dto;
    }


    public Long getStudentCount() {
        return studentRepository.countStudents();
    }

    public Map<String, Double> getGenderPercentage() {
        List<Object[]> rows = studentRepository.getGenderPercentage();
        Map<String, Double> result = new HashMap<>();

        for (Object[] row : rows) {
            result.put((String) row[0], ((Number) row[1]).doubleValue());
        }
        return result;
    }

//
//    public StudentTimetableResponse getStudentTimetable(String studentId) {
//
//        Student student = studentRepository.findById(studentId)
//                .orElseThrow(() -> new RuntimeException("Student not found"));
//
//        ClassSection cs = student.getClassSection();
//
//        String today = LocalDate.now().getDayOfWeek().name().substring(0, 3);
//
//
//        List<Timetable> todayList =
//                timetableRepository.findByClassSection_ClassSectionIdAndDay(
//                        cs.getClassSectionId(),
//                        today
//                );
//
//        List<Timetable> fullWeek =
//                timetableRepository.findByClassSection_ClassSectionIdOrderByDayAscStartTimeAsc(
//                        cs.getClassSectionId()
//                );
//
//        StudentTimetableResponse response = new StudentTimetableResponse();
//        response.setStudentId(student.getStudentId());
//        response.setStudentName(student.getFullName());
//
//        response.setClassSection(
//                new ClassSectionMiniDTO(
//                        cs.getClassSectionId(),
//                        cs.getClassName(),
//                        cs.getSection(),
//                        cs.getAcademicYear()
//                )
//        );
//
//        if (cs.getClassTeacher() != null) {
//            Teacher t = cs.getClassTeacher();
//            response.setClassTeacher(
//                    new TeacherMiniDTO(t.getTeacherId(), t.getTeacherName())
//            );
//        }
//
//        // Subject grouping (subject → periods)
//        response.setSubjects(buildSubjectWise(fullWeek));
//
//        // Today's Timetable
//        response.setTodayTimetable(
//                todayList.stream().map(this::mapMini).toList()
//        );
//
//        // Weekly Timetable (grouped by days)
//        response.setFullWeekTimetable(buildWeekly(fullWeek));
//
//        return response;
//    }

    private TimetableMiniDTO mapMini(Timetable t) {
        return new TimetableMiniDTO(
                t.getDay(),
                t.getStartTime(),
                t.getEndTime(),
                t.getSubject().getSubjectName(),
                t.getTeacher().getTeacherName()
        );
    }

    private List<StudentSubjectTimetableDTO> buildSubjectWise(List<Timetable> list) {

        Map<String, StudentSubjectTimetableDTO> map = new HashMap<>();

        for (Timetable t : list) {

            map.putIfAbsent(
                    t.getSubject().getSubjectId(),
                    StudentSubjectTimetableDTO.builder()
                            .subjectId(t.getSubject().getSubjectId())
                            .subjectName(t.getSubject().getSubjectName())
                            .teacherId(t.getTeacher().getTeacherId())
                            .teacherName(t.getTeacher().getTeacherName())
                            .periods(new ArrayList<>())
                            .weeklyDays(new ArrayList<>())
                            .build()
            );

            StudentSubjectTimetableDTO dto = map.get(t.getSubject().getSubjectId());

            dto.getPeriods().add(mapMini(t));

            if (!dto.getWeeklyDays().contains(t.getDay())) {
                dto.getWeeklyDays().add(t.getDay());
            }
        }

        return new ArrayList<>(map.values());
    }

    private List<WeeklyTimetableDTO> buildWeekly(List<Timetable> list) {

        Map<String, WeeklyTimetableDTO> map = new LinkedHashMap<>();

        for (Timetable t : list) {

            map.putIfAbsent(
                    t.getDay(),
                    WeeklyTimetableDTO.builder()
                            .day(t.getDay())
                            .list(new ArrayList<>())
                            .build()
            );

            map.get(t.getDay()).getList().add(mapMini(t));
        }

        return new ArrayList<>(map.values());
    }

    public void createWeeklyTimetable(CreateTimetableRequest request) {

        ClassSection classSection = classSectionRepository.findById(request.getClassSectionId())
                .orElseThrow(() -> new RuntimeException("Invalid class section"));

        for (CreateTimetableRequest.PeriodRequest p : request.getPeriods()) {

            Subject subject = subjectRepository.findById(p.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Invalid subject"));

            Teacher teacher = teacherRepository.findById(p.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Invalid teacher"));

            boolean classHasConflict =
                    timetableRepository.existsByClassSection_ClassSectionIdAndDayAndStartTimeAndEndTime(
                            request.getClassSectionId(),
                            p.getDay(),
                            p.getStartTime(),
                            p.getEndTime()
                    );

            if (classHasConflict) {
                throw new RuntimeException(
                        "Class already has a period at " + p.getDay() + " " + p.getStartTime()
                );
            }

            boolean teacherBusy =
                    timetableRepository.existsByTeacher_TeacherIdAndDayAndStartTimeAndEndTime(
                            p.getTeacherId(),
                            p.getDay(),
                            p.getStartTime(),
                            p.getEndTime()
                    );

            if (teacherBusy) {
                throw new RuntimeException(
                        "Teacher " + teacher.getTeacherName() +
                                " is already assigned at " + p.getDay() + " " + p.getStartTime()
                );
            }

            Timetable t = Timetable.builder()
                    .id(idGenerator.generateId("T"))
                    .classSection(classSection)
                    .subject(subject)
                    .teacher(teacher)
                    .day(p.getDay())
                    .startTime(p.getStartTime())
                    .endTime(p.getEndTime())
                    .build();

            timetableRepository.save(t);
        }
    }

    public void updateWeeklyTimeTable(CreateTimetableRequest request) {

        // 1️⃣ Validate Class Section
        ClassSection classSection = classSectionRepository.findById(request.getClassSectionId())
                .orElseThrow(() -> new RuntimeException("Invalid class section"));

        // 2️⃣ Delete Existing Timetable for This Class
        List<Timetable> existing = timetableRepository
                .findByClassSection_ClassSectionId(request.getClassSectionId());

        timetableRepository.deleteAll(existing);

        for (CreateTimetableRequest.PeriodRequest p : request.getPeriods()) {

            Subject subject = subjectRepository.findById(p.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Invalid subject"));

            Teacher teacher = teacherRepository.findById(p.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Invalid teacher"));

            // 🔥 Check Teacher Conflict
            boolean teacherBusy =
                    timetableRepository.existsByTeacher_TeacherIdAndDayAndStartTimeAndEndTime(
                            p.getTeacherId(),
                            p.getDay(),
                            p.getStartTime(),
                            p.getEndTime()
                    );

            if (teacherBusy) {
                throw new RuntimeException(
                        "Teacher " + teacher.getTeacherName() +
                                " is already assigned at " + p.getDay() + " " + p.getStartTime()
                );
            }

            // 4️⃣ Save new timetable period
            Timetable t = Timetable.builder()
                    .id(idGenerator.generateId("T"))
                    .classSection(classSection)
                    .subject(subject)
                    .teacher(teacher)
                    .day(p.getDay())
                    .startTime(p.getStartTime())
                    .endTime(p.getEndTime())
                    .build();

            timetableRepository.save(t);
        }
    }

    public StudentWeeklyTimetableDTO getStudentWeeklyTimetableWithDates(String studentId, LocalDate weekReference) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        ClassSection cs = student.getClassSection();
        if (cs == null) throw new RuntimeException("Student has no class section assigned");

        List<Timetable> classTimetable = timetableRepository
                .findByClassSection_ClassSectionIdOrderByDayAscStartTimeAsc(cs.getClassSectionId());

        Map<String, LocalDate> weekDates = getWeekDates(weekReference);

        Map<String, List<Timetable>> grouped = classTimetable.stream()
                .collect(Collectors.groupingBy(Timetable::getDay, LinkedHashMap::new, Collectors.toList()));

        List<StudentWeeklyTimetableDTO.DayEntry> days = new ArrayList<>();

        for (String dayName : List.of(
                "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY",
                "FRIDAY", "SATURDAY", "SUNDAY"
        )) {

            LocalDate date = weekDates.get(dayName);

            List<Timetable> periods = grouped.getOrDefault(dayName, Collections.emptyList());

            List<StudentWeeklyTimetableDTO.Period> periodDTOs = periods.stream()
                    .sorted(Comparator.comparing(Timetable::getStartTime))
                    .map(t -> {
                        StudentWeeklyTimetableDTO.Period p = new StudentWeeklyTimetableDTO.Period();
                        p.setStartTime(toAmPm(t.getStartTime()));   // ⬅ AM/PM
                        p.setEndTime(toAmPm(t.getEndTime()));       // ⬅ AM/PM
                        p.setSubjectId(t.getSubject().getSubjectId());
                        p.setSubjectName(t.getSubject().getSubjectName());
                        p.setTeacherId(t.getTeacher().getTeacherId());
                        p.setTeacherName(t.getTeacher().getTeacherName());
                        return p;
                    }).collect(Collectors.toList());

            StudentWeeklyTimetableDTO.DayEntry de = new StudentWeeklyTimetableDTO.DayEntry();
            de.setDay(dayName);
            de.setDate(date != null ? date.toString() : null);
            de.setPeriods(periodDTOs);

            days.add(de);
        }

        return StudentWeeklyTimetableDTO.builder()
                .studentId(student.getStudentId())
                .studentName(student.getFullName())
                .classSectionId(cs.getClassSectionId())
                .classSection(new ClassSectionMiniDTO(
                        cs.getClassSectionId(), cs.getClassName(), cs.getSection(), cs.getAcademicYear()
                ))
                .classTeacher(
                        cs.getClassTeacher() != null ?
                                new TeacherMiniDTO(
                                        cs.getClassTeacher().getTeacherId(),
                                        cs.getClassTeacher().getTeacherName()
                                )
                                : null
                )
                .weeklyTimetable(days)
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
        if (time == null) return null;

        return java.time.LocalTime.parse(time)
                .format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a"));
    }
}