package com.project.student.education.service;


import com.project.student.education.DTO.AttendanceRequest;
import com.project.student.education.DTO.AttendanceViewDTO;
import com.project.student.education.entity.ClassSection;
import com.project.student.education.entity.Holiday;
import com.project.student.education.entity.Student;
import com.project.student.education.entity.StudentAttendance;
import com.project.student.education.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final ClassSectionRepository classSectionRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final HolidayRepository holidayRepository;

    private final NotificationService notificationService;

    public AttendanceRequest markAttendance(AttendanceRequest request, String markedByTeacherId) {

        ClassSection section = classSectionRepository.findById(request.getClassSectionId())
                .orElseThrow(() -> new RuntimeException("Class section not found"));

        if (section.getClassTeacher() == null ||
                !section.getClassTeacher().getTeacherId().equals(markedByTeacherId)) {
            throw new RuntimeException("Only the class teacher can mark attendance for this class.");
        }

        boolean alreadyExists = attendanceRepository
                .existsByClassSectionIdAndDate(request.getClassSectionId(), request.getDate());

        if (alreadyExists) {
            throw new RuntimeException("Attendance already marked for this class on this date.");
        }

        for (AttendanceRequest.Entry entry : request.getEntries()) {

            Student student = studentRepository.findById(entry.getStudentId())
                    .orElseThrow(() -> new RuntimeException("Student not found: " + entry.getStudentId()));

            if (!student.getClassSection().getClassSectionId().equals(request.getClassSectionId())) {
                throw new RuntimeException("Student " + entry.getStudentId() +
                        " does not belong to class " + request.getClassSectionId());
            }

            StudentAttendance a = StudentAttendance.builder()
                    .studentId(entry.getStudentId())
                    .classSectionId(request.getClassSectionId())
                    .date(request.getDate())
                    .status(entry.getStatus())
                    .markedBy(markedByTeacherId)
                    .markedAt(LocalDateTime.now())
                    .build();

            attendanceRepository.save(a);
            if (entry.getStatus().equalsIgnoreCase("A") ||
                    entry.getStatus().equalsIgnoreCase("ABSENT")) {

                notificationService.sendNotification(
                        entry.getStudentId(),
                        "Absent Today",
                        "You have been marked ABSENT today (" + request.getDate() + ").",
                        "ATTENDANCE"
                );
            }
        }

        return request;
    }

    public AttendanceViewDTO getAttendanceByStudent(String studentId, int year, int month) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<StudentAttendance> attendanceList =
                attendanceRepository.findByStudentIdAndDateBetween(studentId, start, end);

        Map<LocalDate, String> attendanceMap = attendanceList.stream()
                .collect(Collectors.toMap(
                        StudentAttendance::getDate,
                        a -> a.getStatus().trim().toUpperCase(),
                        (s1, s2) -> s1
                ));

        List<Holiday> holidayList = holidayRepository.findByDateBetween(start, end);
        Set<LocalDate> holidayDates = holidayList.stream()
                .map(Holiday::getDate)
                .collect(Collectors.toSet());

        int present = 0;
        int absent = 0;
        int holidayCount = 0;

        List<AttendanceViewDTO.Daily> dailyList = new ArrayList<>();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {

            String status;

            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                status = "HOLIDAY";
                holidayCount++;
            }
            else if (holidayDates.contains(date)) {
                status = "HOLIDAY";
                holidayCount++;
            }
            else if (attendanceMap.containsKey(date)) {
                String val = attendanceMap.get(date);

                if (val.equals("P") || val.equals("PRESENT") || val.equals("PR")) {
                    present++;
                    status = "PRESENT";
                } else if (val.equals("A") || val.equals("AB") || val.equals("ABSENT")) {
                    absent++;
                    status = "ABSENT";
                } else {
                    status = "NOT_MARKED";
                }
            }
            else {
                status = "NOT_MARKED";
            }

            dailyList.add(new AttendanceViewDTO.Daily(date, status));
        }
        double workingDays = present + absent;
        double percentage = workingDays == 0 ? 0 : (present * 100.0) / workingDays;

        return AttendanceViewDTO.builder()
                .studentId(studentId)
                .present(present)
                .absent(absent)
                .holidays(holidayCount)
                .percentage(percentage)
                .dailyRecords(dailyList)
                .build();
    }

    public List<Map<String, Object>> getClassAttendanceForDate(String classSectionId, LocalDate date) {
        if (!classSectionRepository.existsById(classSectionId)) {
            throw new RuntimeException("Class section not found: " + classSectionId);
        }
        List<Student> students = studentRepository.findByClassSection_ClassSectionId(classSectionId);

        if (students.isEmpty()) {
            throw new RuntimeException("No students found for class " + classSectionId);
        }
        List<StudentAttendance> attList =
                attendanceRepository.findByClassSectionIdAndDate(classSectionId, date);

        Map<String, String> attendanceMap = attList.stream()
                .collect(Collectors.toMap(
                        StudentAttendance::getStudentId,
                        a -> a.getStatus().toUpperCase()
                ));
        boolean isSunday = date.getDayOfWeek() == DayOfWeek.SUNDAY;
        boolean isHoliday = holidayRepository.existsByDate(date);

        List<Map<String, Object>> response = new ArrayList<>();

        for (Student s : students) {

            String status;

            if (isSunday || isHoliday) {
                status = "HOLIDAY";
            } else if (attendanceMap.containsKey(s.getStudentId())) {
                status = attendanceMap.get(s.getStudentId());
            } else {
                status = "NOT_MARKED";
            }

            response.add(Map.of(
                    "studentId", s.getStudentId(),
                    "name", s.getFullName(),
                    "status", status
            ));
        }

        return response;
    }


}