package com.project.student.education.controller;


import com.project.student.education.DTO.CreateTimetableRequest;
import com.project.student.education.DTO.StudentTimetableResponse;
import com.project.student.education.DTO.StudentWeeklyTimetableDTO;
import com.project.student.education.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/student")
public class TimeTableController {


    private final StudentService studentService;

    public TimeTableController(StudentService studentService) {
        this.studentService = studentService;
    }

//    @GetMapping("/{studentId}/timetable")
//    public ResponseEntity<StudentTimetableResponse> getStudentTimetable(
//            @PathVariable String studentId
//    ) {
//        StudentTimetableResponse response =
//                studentService.getStudentTimetable(studentId);
//
//        return ResponseEntity.ok(response);
//    }
    @PostMapping("/create/timetable")
    public ResponseEntity<String> createTimetable(
            @RequestBody CreateTimetableRequest request
    ) {
        studentService.createWeeklyTimetable(request);
        return ResponseEntity.ok("Timetable created successfully!");
    }

    @PutMapping("/update/timetable")
    public ResponseEntity<String> updateTimetable(@RequestBody CreateTimetableRequest request)
    {
        studentService.updateWeeklyTimeTable(request);
        return ResponseEntity.ok("Timetable updated successfully!");
    }
    @GetMapping("/{studentId}/weekly-timetable")
    public ResponseEntity<StudentWeeklyTimetableDTO> getStudentWeeklyTimetable(
            @PathVariable String studentId,
            @RequestParam(required = false) String weekStart
    ) {
        LocalDate ref;
        if (weekStart == null || weekStart.isEmpty()) {
            ref = LocalDate.now();
        } else {
            ref = LocalDate.parse(weekStart);
        }

        StudentWeeklyTimetableDTO dto = studentService.getStudentWeeklyTimetableWithDates(studentId, ref);
        return ResponseEntity.ok(dto);
    }


}


