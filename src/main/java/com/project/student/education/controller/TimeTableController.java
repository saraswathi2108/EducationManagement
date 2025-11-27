package com.project.student.education.controller;


import com.project.student.education.DTO.CreateTimetableRequest;
import com.project.student.education.DTO.StudentWeeklyTimetableDTO;
import com.project.student.education.DTO.TeacherWeeklyTimetableDTO;
import com.project.student.education.service.StudentService;
import com.project.student.education.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/student")
public class TimeTableController {

    private final StudentService studentService;
    private final TeacherService teacherService;

    public TimeTableController(StudentService studentService, TeacherService teacherService) {
        this.studentService = studentService;
        this.teacherService = teacherService;
    }


    // ADMIN ONLY
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create/timetable")
    public ResponseEntity<String> createTimetable(@RequestBody CreateTimetableRequest request) {
        studentService.createWeeklyTimetable(request);
        return ResponseEntity.ok("Timetable created successfully!");
    }


    // ADMIN ONLY
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/timetable")
    public ResponseEntity<String> updateTimetable(@RequestBody CreateTimetableRequest request) {
        studentService.updateWeeklyTimeTable(request);
        return ResponseEntity.ok("Timetable updated successfully!");
    }


    // ADMIN + TEACHER + STUDENT + PARENT
    // (Students/Parents must only view their OWN timetable – enforce in service layer)
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT','PARENT')")
    @GetMapping("/{studentId}/weekly-timetable")
    public ResponseEntity<StudentWeeklyTimetableDTO> getStudentWeeklyTimetable(
            @PathVariable String studentId,
            @RequestParam(required = false) String weekStart
    ) {
        LocalDate ref = (weekStart == null || weekStart.isEmpty())
                ? LocalDate.now()
                : LocalDate.parse(weekStart);

        return ResponseEntity.ok(studentService.getStudentWeeklyTimetableWithDates(studentId, ref));
    }


    // ADMIN + TEACHER
    // Teachers can access ONLY their own timetable → check in service layer
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/teacher/{teacherId}/weekly-timetable")
    public ResponseEntity<TeacherWeeklyTimetableDTO> getTeacherTimetable(
            @PathVariable String teacherId,
            @RequestParam(required = false) String weekStart
    ) {
        LocalDate ref = (weekStart == null || weekStart.isEmpty())
                ? LocalDate.now()
                : LocalDate.parse(weekStart);

        return ResponseEntity.ok(teacherService.getTeacherWeeklyTimetable(teacherId, ref));
    }


    // ADMIN + TEACHER
    // A class teacher can see only their class timetable → validate in service layer
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/teacher/{teacherId}/class-timetable")
    public ResponseEntity<TeacherWeeklyTimetableDTO> getClassTeacherTimetable(
            @PathVariable String teacherId,
            @RequestParam(required = false) String weekStart
    ) {
        LocalDate ref = (weekStart == null || weekStart.isEmpty())
                ? LocalDate.now()
                : LocalDate.parse(weekStart);

        return ResponseEntity.ok(teacherService.getClassTeacherTimetable(teacherId, ref));
    }

}
