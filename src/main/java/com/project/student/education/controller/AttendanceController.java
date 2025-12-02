package com.project.student.education.controller;

import AIExpose.Agent.Annotations.AIExposeController;
import AIExpose.Agent.Annotations.AIExposeEpHttp;
import AIExpose.Agent.Annotations.Describe;
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
@AIExposeController
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }
    @AIExposeEpHttp(
            name = "Mark Class Attendance",
            description = "Marks attendance for a class on a specific date.",
            autoExecute = true,
            tags = {"Attendance", "Teacher", "Mark"},
            reqParams = @Describe(
                    name = "AttendanceRequest",
                    description = "ClassSectionId, date, and student attendance entries.",
                    dataType = "AttendanceRequest"
            ),
            returnDescription = "Returns attendance object after saving."
    )
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


    @AIExposeEpHttp(
            name = "Get Monthly Attendance",
            description = "Gets attendance for a specific month.",
            autoExecute = true,
            tags = {"Attendance", "Student", "Monthly"},
    pathParams = {
        @Describe(name = "studentId", dataType = "String"),
        @Describe(name = "year", dataType = "int"),
        @Describe(name = "month", dataType = "int")
    },
    returnDescription = "Returns attendance summary for the given month."
            )
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


    @AIExposeEpHttp(
            name = "Get Class Attendance On Date",
            description = "Returns attendance list for an entire class on a specific date.",
            autoExecute = true,
            tags = {"Attendance", "Admin", "Class"},
            pathParams = {
                    @Describe(name = "classSectionId", dataType = "String"),
                    @Describe(name = "date", dataType = "LocalDate")
            },
            returnDescription = "Returns list of students with their attendance status."
    )
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
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


    @AIExposeEpHttp(
            name = "Get Academic Year Attendance",
            description = "Fetches full academic attendance (June–April).",
            autoExecute = true,
            tags = {"Attendance", "Student", "AcademicYear"},
            pathParams = {
                    @Describe(name = "studentId", dataType = "String"),
                    @Describe(name = "year", dataType = "int")
            },
            returnDescription = "Returns attendance summary for the academic year."
    )
    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/{studentId}/year/{year}")
    public ResponseEntity<AttendanceViewDTO> getAttendanceAcademicYear(
            @PathVariable String studentId,
            @PathVariable int year) {

        return ResponseEntity.ok(
                attendanceService.getAttendanceForAcademicYear(studentId, year)
        );
    }



}
