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
        }

        return request;
    }

    public AttendanceViewDTO getAttendanceByStudent(String studentId, int year, int month) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        // Fetch attendance for month
        List<StudentAttendance> attendanceList =
                attendanceRepository.findByStudentIdAndDateBetween(studentId, start, end);

        // Map date → status
        Map<LocalDate, String> attendanceMap = attendanceList.stream()
                .collect(Collectors.toMap(
                        StudentAttendance::getDate,
                        a -> a.getStatus().trim().toUpperCase(),
                        (s1, s2) -> s1
                ));

        // Fetch holidays
        List<Holiday> holidayList = holidayRepository.findByDateBetween(start, end);
        Set<LocalDate> holidayDates = holidayList.stream()
                .map(Holiday::getDate)
                .collect(Collectors.toSet());

        int present = 0;
        int absent = 0;
        int holidayCount = 0;

        List<AttendanceViewDTO.Daily> dailyList = new ArrayList<>();

        // Loop through real calendar days
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {

            String status;

            // 1️⃣ Sunday → HOLIDAY
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                status = "HOLIDAY";
                holidayCount++;
            }

            // 2️⃣ DB holiday → HOLIDAY
            else if (holidayDates.contains(date)) {
                status = "HOLIDAY";
                holidayCount++;
            }

            // 3️⃣ Attendance exists → PRESENT / ABSENT
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

            // 4️⃣ No record → NOT_MARKED
            else {
                status = "NOT_MARKED";
            }

            dailyList.add(new AttendanceViewDTO.Daily(date, status));
        }

        // working days = present + absent
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
}