package com.project.student.education.controller;


import com.project.student.education.DTO.*;
import com.project.student.education.service.ExamSchedulingService;
import com.project.student.education.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/exams")
@RequiredArgsConstructor
public class ExamController {


    private final ExamService examService;

    private final ExamSchedulingService schedulingService;


    @PreAuthorize("hasRole('ADMIN')")

    @PostMapping("/create")
    public ResponseEntity<ExamMasterDTO>createExam(@RequestBody ExamMasterDTO examMasterDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examService.createExam(examMasterDTO));
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    @GetMapping("/all")
    public ResponseEntity<List<ExamMasterDTO>> getAllExams() {
        return ResponseEntity.status(HttpStatus.OK).body(examService.getAllExams());
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")

    @GetMapping("/{examId}")
    public ResponseEntity<ExamMasterDTO> getExamById(@PathVariable String examId) {
        return  ResponseEntity.ok(examService.getExamById(examId));

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{examId}/status")
    public ResponseEntity<ExamMasterDTO> updateStatus(
            @PathVariable String examId,
            @RequestBody UpdateExamStatusRequest request) {

        return ResponseEntity.ok(examService.updateExamStatus(examId, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/schedule-one")
    public ResponseEntity<ExamRecordDTO> scheduleExam(
            @RequestBody ExamRecordDTO dto) {
        return ResponseEntity.ok(schedulingService.scheduleOne(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/schedule-bulk")
    public ResponseEntity<List<ExamRecordDTO>> scheduleBulk(@RequestBody BulkScheduleRequest req) {
        return ResponseEntity.ok(schedulingService.scheduleBulk(req));
    }


    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    @GetMapping("/{examId}/timetable/{classSectionId}")
    public ResponseEntity<List<TimetableDayDTO>> getTimetable(
            @PathVariable String examId,
            @PathVariable String classSectionId
    ) {
        List<TimetableDayDTO>examRecordDTOS=schedulingService.getTimetable(examId,classSectionId);
        return ResponseEntity.ok(examRecordDTOS);
    }
  //  @PutMapping("/enter-marks/{subjectId}")
//    public ResponseEntity<SubjectMarksEntryRequest> enterMarks(@RequestBody SubjectMarksEntryRequest dto,@PathVariable String subjectId) {
//        return ResponseEntity.ok(schedulingService.enterMarks(dto,subjectId));
//    }

//    @GetMapping("result/{studentId}")
//    public ResponseEntity<Stu>
//
//
@PreAuthorize("hasAnyRole('ADMIN','TEACHER')")

@PostMapping("schedule-comprehensive")
    public ResponseEntity<List<ExamRecordDTO>> scheduleComprehensive(@RequestBody ComprehensiveScheduleRequest req){
        List<ExamRecordDTO>examRecordDTOS=schedulingService.scheduleComprehensive(req);
        return ResponseEntity.ok(examRecordDTOS);
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")

    @GetMapping("/teacher/{examId}")
    public ResponseEntity<List<TimetableDayDTO>> getTeacherClassExamTimetable(
            @PathVariable String examId
    ) {
        return ResponseEntity.ok(
                schedulingService.getTeacherClassExamTimetable(examId)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")

    @GetMapping("/teacher/subject/{examId}")
    public ResponseEntity<List<TimetableDayDTO>> getTeacherSubjectExamTimetable(
            @PathVariable String examId
    ) {
        return ResponseEntity.ok(
                schedulingService.getTeacherSubjectExamTimetable(examId)
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")

    @PutMapping("/enter-marks/{subjectId}")
    public ResponseEntity<String> enterMarks(
            @PathVariable String subjectId,
            @RequestBody SubjectMarksEntryRequest request) {

        examService.enterMarks(subjectId, request);
        return ResponseEntity.ok("Marks saved successfully for subject " + subjectId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    @GetMapping("/records/{examId}/{classSectionId}/{subjectId}")
    public ResponseEntity<List<StudentExamRecordDTO>> getExamRecords(
            @PathVariable String examId,
            @PathVariable String classSectionId,
            @PathVariable String subjectId) {

        return ResponseEntity.ok(
                examService.getExamRecords(examId, classSectionId, subjectId)
        );
    }



    @PreAuthorize("hasRole('ADMIN')")

    @PutMapping("/publish/{examId}/{classSectionId}")
    public ResponseEntity<String> publishResult(
            @PathVariable String examId,
            @PathVariable String classSectionId,
            @RequestParam(defaultValue = "ADMIN") String adminName) {

        examService.publishResult(examId, classSectionId, adminName);
        return ResponseEntity.ok("Result published successfully.");
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    @GetMapping("/result/{examId}/{studentId}")
    public ResponseEntity<StudentExamResultDTO> getResult(
            @PathVariable String examId,
            @PathVariable String studentId,
            @RequestParam String classSectionId) {

        return ResponseEntity.ok(
                examService.getStudentResult(examId, studentId, classSectionId)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")

    @GetMapping("/class-result/{examId}/{classSectionId}")
    public ResponseEntity<List<StudentExamResultDTO>> getClassExamResults(
            @PathVariable String examId,
            @PathVariable String classSectionId) {

        return ResponseEntity.ok(examService.getClassExamResults(examId, classSectionId));
    }










}
