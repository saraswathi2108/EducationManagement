package com.project.student.education.controller;

import com.project.student.education.DTO.StudentDTO;
import com.project.student.education.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/allStudents")
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        List<StudentDTO>studentDTOS=studentService.getAllStudents();
        return ResponseEntity.ok(studentDTOS);

    }
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentDTO> getStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(studentService.getStudentById(studentId));
    }

    @PutMapping(value = "/{studentId}", consumes = "multipart/form-data")
    public ResponseEntity<StudentDTO> updateStudent(
            @PathVariable String studentId,
            @RequestPart("data") StudentDTO dto,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) throws IOException {
        StudentDTO updated = studentService.updateStudent(studentId, dto, photo);
        return ResponseEntity.ok(updated);
    }



    @DeleteMapping("/{studentId}")
    public ResponseEntity<StudentDTO>deleteStudent(@PathVariable String studentId) {
        StudentDTO studentDTO1=studentService.deleteStudent(studentId);
        return ResponseEntity.ok(studentDTO1);
    }

    @PutMapping("/{studentId}/transfer")
    public ResponseEntity<StudentDTO> transferStudent(
            @PathVariable String studentId,
            @RequestParam String targetClassSectionId) {

        StudentDTO dto = studentService.transferStudent(studentId, targetClassSectionId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countStudents() {
        return ResponseEntity.ok(studentService.getStudentCount());
    }

    @GetMapping("/dashboard/gender-percentage")
    public ResponseEntity<Map<String, Double>> getGenderPercentage() {
        return ResponseEntity.ok(studentService.getGenderPercentage());
    }

}
