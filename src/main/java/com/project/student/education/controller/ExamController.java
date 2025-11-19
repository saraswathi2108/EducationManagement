package com.project.student.education.controller;


import com.project.student.education.DTO.*;
import com.project.student.education.service.ExamSchedulingService;
import com.project.student.education.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/exams")
@RequiredArgsConstructor
public class ExamController {


    private final ExamService examService;

    private final ExamSchedulingService schedulingService;



    @PostMapping("/create")
    public ResponseEntity<ExamMasterDTO>createExam(@RequestBody ExamMasterDTO examMasterDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examService.createExam(examMasterDTO));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ExamMasterDTO>> getAllExams() {
        return ResponseEntity.status(HttpStatus.OK).body(examService.getAllExams());
    }

    @GetMapping("/{examId}")
    public ResponseEntity<ExamMasterDTO> getExamById(@PathVariable String examId) {
        return  ResponseEntity.ok(examService.getExamById(examId));

    }
    @PutMapping("/{examId}/status")
    public ResponseEntity<ExamMasterDTO> updateStatus(
            @PathVariable String examId,
            @RequestBody UpdateExamStatusRequest request) {

        return ResponseEntity.ok(examService.updateExamStatus(examId, request));
    }
    @PostMapping("/schedule-one")
    public ResponseEntity<ExamRecordDTO> scheduleExam(
            @RequestBody ExamRecordDTO dto) {
        return ResponseEntity.ok(schedulingService.scheduleOne(dto));
    }
    @PostMapping("/schedule-bulk")
    public ResponseEntity<List<ExamRecordDTO>> scheduleBulk(@RequestBody BulkScheduleRequest req) {
        return ResponseEntity.ok(schedulingService.scheduleBulk(req));
    }


    @GetMapping("/{examId}/timetable/{classSectionId}")
    public ResponseEntity<List<TimetableDayDTO>> getTimetable(
            @PathVariable String examId,
            @PathVariable String classSectionId
    ) {
        List<TimetableDayDTO>examRecordDTOS=schedulingService.getTimetable(examId,classSectionId);
        return ResponseEntity.ok(examRecordDTOS);
    }
    @PutMapping("/enter-marks/{subjectId}")
    public ResponseEntity<SubjectMarksEntryRequest> enterMarks(@RequestBody SubjectMarksEntryRequest dto,@PathVariable String subjectId) {
        return ResponseEntity.ok(schedulingService.enterMarks(dto,subjectId));
    }

//    @GetMapping("result/{studentId}")
//    public ResponseEntity<Stu>
//




}
