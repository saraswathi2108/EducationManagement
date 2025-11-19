package com.project.student.education.controller;


import com.project.student.education.DTO.AssignSubjectTeacherDTO;
import com.project.student.education.DTO.ClassSubjectAssignRequest;
import com.project.student.education.DTO.ClassSubjectMappingDTO;
import com.project.student.education.DTO.SubjectDTO;
import com.project.student.education.entity.Subject;
import com.project.student.education.service.StudentService;
import com.project.student.education.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/student/subject")
public class SubjectController {


    @Autowired
    private SubjectService subjectService;

    @Autowired
    private StudentService studentService;

    @PostMapping("/createSubject")
    public ResponseEntity<SubjectDTO> createSubject(@RequestBody SubjectDTO subject){

    SubjectDTO subjectDTO=subjectService.createSubject(subject);
    return new ResponseEntity<>(subjectDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{subjectId}")
    public ResponseEntity<SubjectDTO> updateSubject(
            @PathVariable String subjectId,
            @RequestBody SubjectDTO subjectDTO) {
        SubjectDTO updatedSubject = subjectService.updateSubject(subjectId, subjectDTO);
        return ResponseEntity.ok(updatedSubject);
    }

    @GetMapping("/allSubjects")
    public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
        List<SubjectDTO> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/{subjectId}")
    public ResponseEntity<SubjectDTO> getSubject(@PathVariable String subjectId) {
        SubjectDTO subjectDTO=subjectService.getSubjectById(subjectId);
        return  ResponseEntity.ok(subjectDTO);
    }




    @DeleteMapping("/{subjectId}")
    public ResponseEntity<Void> deleteSubject(@PathVariable String subjectId) {
        subjectService.deleteSubject(subjectId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/assign")
    public ResponseEntity<List<ClassSubjectMappingDTO>> assignSubjectsToClass(
            @RequestBody ClassSubjectAssignRequest request) {

        return ResponseEntity.ok(subjectService.assignSubjects(request));
    }

    @PutMapping("/assign")
    public ResponseEntity<List<ClassSubjectMappingDTO>> updateAssignSubjectsToClass(
            @RequestBody ClassSubjectAssignRequest request) {

        return ResponseEntity.ok(subjectService.updateSubjectsAndTeachers(request));
    }

    @GetMapping("/assign/{classSectionId}")
    public ResponseEntity<List<ClassSubjectMappingDTO>> getAssignedSubjects(
            @PathVariable String classSectionId) {

        return ResponseEntity.ok(
                subjectService.getAssignedSubjects(classSectionId)
        );
    }

    @PostMapping("/assignSubjectTeacher")
    public ResponseEntity<?> assignTeacher(@RequestBody AssignSubjectTeacherDTO dto) {
        return ResponseEntity.ok(Map.of("message", subjectService.assignTeacherToSubject(dto)));
    }

    @GetMapping("/{classSectionId}")
    public ResponseEntity<?> getSubjectTeacherMapping(@PathVariable String classSectionId) {
        return ResponseEntity.ok(subjectService.getMappingForClass(classSectionId));
    }




}
