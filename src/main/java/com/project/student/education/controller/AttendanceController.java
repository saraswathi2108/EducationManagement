package com.project.student.education.controller;

import com.project.student.education.DTO.AttendanceRequest;
import com.project.student.education.DTO.AttendanceViewDTO;
import com.project.student.education.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

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
    @GetMapping("/{studentId}/{year}/{month}")
    public ResponseEntity<AttendanceViewDTO> getAttendanceByStudent(
            @PathVariable String studentId,
            @PathVariable int year,
            @PathVariable int month) {

        return ResponseEntity.ok(
                attendanceService.getAttendanceByStudent(studentId, year, month)
        );
    }

}
