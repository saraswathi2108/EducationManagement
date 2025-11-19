package com.project.student.education.service;


import com.project.student.education.DTO.AttendanceEntryDTO;
import com.project.student.education.DTO.AttendanceRequest;
import com.project.student.education.entity.ClassSection;
import com.project.student.education.entity.Student;
import com.project.student.education.entity.StudentAttendance;
import com.project.student.education.entity.Teacher;
import com.project.student.education.repository.AttendanceRepository;
import com.project.student.education.repository.ClassSectionRepository;
import com.project.student.education.repository.StudentRepository;
import com.project.student.education.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final ClassSectionRepository classSectionRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;


        public AttendanceRequest markAttendance(AttendanceRequest request, String markedByTeacherId) {

            // 1️⃣ Validate class section
            ClassSection section = classSectionRepository.findById(request.getClassSectionId())
                    .orElseThrow(() -> new RuntimeException("Class section not found"));

            // 2️⃣ Validate teacher is class teacher
            if (section.getClassTeacher() == null ||
                    !section.getClassTeacher().getTeacherId().equals(markedByTeacherId)) {
                throw new RuntimeException("Only the class teacher can mark attendance for this class.");
            }

            // 3️⃣ Prevent marking attendance twice on the same day
            boolean alreadyExists = attendanceRepository
                    .existsByClassSectionIdAndDate(request.getClassSectionId(), request.getDate());

            if (alreadyExists) {
                throw new RuntimeException("Attendance already marked for this class on this date.");
            }

            // 4️⃣ Validate students & save attendance
            for (AttendanceEntryDTO entry : request.getEntries()) {

                // Fetch student
                Student student = studentRepository.findById(entry.getStudentId())
                        .orElseThrow(() -> new RuntimeException("Student not found: " + entry.getStudentId()));

                // Check if student belongs to this class
                if (!student.getClassSection().getClassSectionId().equals(request.getClassSectionId())) {
                    throw new RuntimeException("Student " + entry.getStudentId() +
                            " does not belong to class " + request.getClassSectionId());
                }

                // Save attendance
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

}