package com.project.student.education.controller;

import com.project.student.education.DTO.AttendanceRequest;
import com.project.student.education.DTO.AttendanceViewDTO;
import com.project.student.education.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/mark/{classSectionId}/{teacherId}")
    public ResponseEntity<?> markAttendance(
            @PathVariable String classSectionId,
            @PathVariable String teacherId,
            @RequestBody AttendanceRequest request
    ) {
        request.setClassSectionId(classSectionId);
        AttendanceRequest result = attendanceService.markAttendance(
                request,
                teacherId
        );
        return ResponseEntity.ok(Map.of(
                "message", "Attendance marked successfully",
                "data", result
        ));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/{studentId}/{year}/{month}")
    public ResponseEntity<AttendanceViewDTO> getAttendanceByStudent(
            @PathVariable String studentId,
            @PathVariable int year,
            @PathVariable int month) {

        return ResponseEntity.ok(
                attendanceService.getAttendanceByStudent(studentId, year, month)
        );
    }

    @PreAuthorize("hasRole('ADMIN')") // FIXED!

    @GetMapping("/class/{classSectionId}/date/{date}")
    public ResponseEntity<List<Map<String, Object>>> getClassAttendanceForDate(
            @PathVariable String classSectionId,
            @PathVariable String date
    ) {
        LocalDate parsedDate = LocalDate.parse(date);
        return ResponseEntity.ok(
                attendanceService.getClassAttendanceForDate(classSectionId, parsedDate)
        );
    }


}
