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
import org.springframework.security.access.prepost.PreAuthorize;
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


    // ADMIN ONLY
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createSubject")
    public ResponseEntity<SubjectDTO> createSubject(@RequestBody SubjectDTO subject){
        SubjectDTO subjectDTO = subjectService.createSubject(subject);
        return new ResponseEntity<>(subjectDTO, HttpStatus.CREATED);
    }


    // ADMIN ONLY
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{subjectId}")
    public ResponseEntity<SubjectDTO> updateSubject(
            @PathVariable String subjectId,
            @RequestBody SubjectDTO subjectDTO) {
        return ResponseEntity.ok(subjectService.updateSubject(subjectId, subjectDTO));
    }


    // ADMIN + TEACHER + STUDENT + PARENT
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT','PARENT')")
    @GetMapping("/allSubjects")
    public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }


    // ADMIN + TEACHER + STUDENT + PARENT
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT','PARENT')")
    @GetMapping("/{subjectId}")
    public ResponseEntity<SubjectDTO> getSubject(@PathVariable String subjectId) {
        return ResponseEntity.ok(subjectService.getSubjectById(subjectId));
    }


    // ADMIN ONLY
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{subjectId}")
    public ResponseEntity<Void> deleteSubject(@PathVariable String subjectId) {
        subjectService.deleteSubject(subjectId);
        return ResponseEntity.noContent().build();
    }


    // ADMIN ONLY - Assign subjects to a class
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign")
    public ResponseEntity<List<ClassSubjectMappingDTO>> assignSubjectsToClass(
            @RequestBody ClassSubjectAssignRequest request) {

        return ResponseEntity.ok(subjectService.assignSubjects(request));
    }


    // ADMIN ONLY - Update subject + teacher mapping
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/assign")
    public ResponseEntity<List<ClassSubjectMappingDTO>> updateAssignSubjectsToClass(
            @RequestBody ClassSubjectAssignRequest request) {

        return ResponseEntity.ok(subjectService.updateSubjectsAndTeachers(request));
    }


    // ADMIN + TEACHER
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/assign/{classSectionId}")
    public ResponseEntity<List<ClassSubjectMappingDTO>> getAssignedSubjects(
            @PathVariable String classSectionId) {

        return ResponseEntity.ok(subjectService.getAssignedSubjects(classSectionId));
    }


    // ADMIN ONLY
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assignSubjectTeacher")
    public ResponseEntity<?> assignTeacher(@RequestBody AssignSubjectTeacherDTO dto) {
        return ResponseEntity.ok(Map.of("message", subjectService.assignTeacherToSubject(dto)));
    }


    // ADMIN + TEACHER
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/{classSectionId}/teachers")
    public ResponseEntity<?> getSubjectTeacherMapping(@PathVariable String classSectionId) {
        return ResponseEntity.ok(subjectService.getMappingForClass(classSectionId));
    }

}
