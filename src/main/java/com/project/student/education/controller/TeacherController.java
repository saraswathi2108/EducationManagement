package com.project.student.education.controller;

import com.project.student.education.DTO.ClassSectionMiniDTO;
import com.project.student.education.DTO.TeacherDTO;
import com.project.student.education.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/teacher")
@RequiredArgsConstructor
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @PostMapping("/add")
    public ResponseEntity<TeacherDTO> addTeacher(@RequestBody TeacherDTO dto) {
        return new ResponseEntity<>(teacherService.addTeacher(dto), HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<TeacherDTO>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    @GetMapping("/{teacherId}")
    public ResponseEntity<TeacherDTO> getTeacherById(@PathVariable String teacherId) {
        return ResponseEntity.ok(teacherService.getTeacherById(teacherId));
    }

    @PutMapping("/{teacherId}")
    public ResponseEntity<TeacherDTO> updateTeacher(
            @PathVariable String teacherId,
            @RequestBody TeacherDTO dto) {
        return ResponseEntity.ok(teacherService.updateTeacher(teacherId, dto));
    }

    @DeleteMapping("/{teacherId}")
    public ResponseEntity<String> deleteTeacher(@PathVariable String teacherId) {
        return ResponseEntity.ok(teacherService.deleteTeacher(teacherId));
    }

    @PostMapping("/assign/{teacherId}/{classSectionId}")
    public ResponseEntity<String> assignTeacherToClass(
            @PathVariable String teacherId,
            @PathVariable String classSectionId
    ) {
        return ResponseEntity.ok(teacherService.assignTeacher(teacherId, classSectionId));
    }

    @PutMapping("/assign/update/{classSectionId}/{teacherId}")
    public ResponseEntity<String> updateClassTeacher(
            @PathVariable String classSectionId,
            @PathVariable String teacherId) {
        return ResponseEntity.ok(teacherService.updateClassTeacher(classSectionId, teacherId));
    }

    @GetMapping("/assigned-classes/{teacherId}")
    public ResponseEntity<List<ClassSectionMiniDTO>> getClassesHandledByTeacher(
            @PathVariable String teacherId) {
        return ResponseEntity.ok(teacherService.getClassesHandledByTeacher(teacherId));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countTeachers() {
        return ResponseEntity.ok(teacherService.getTeacherCount());
    }



}
