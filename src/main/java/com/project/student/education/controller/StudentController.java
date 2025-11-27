package com.project.student.education.controller;

import com.project.student.education.DTO.StudentDTO;
import com.project.student.education.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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


    // ADMIN + TEACHER
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/allStudents")
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }


    // ADMIN + TEACHER + STUDENT + PARENT
    // (Students/Parents can call this, but **service layer should check** if they are accessing only their own profile)
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT','PARENT')")
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentDTO> getStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(studentService.getStudentById(studentId));
    }


    // ADMIN ONLY
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{studentId}", consumes = "multipart/form-data")
    public ResponseEntity<StudentDTO> updateStudent(
            @PathVariable String studentId,
            @RequestPart("data") StudentDTO dto,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) throws IOException {
        return ResponseEntity.ok(studentService.updateStudent(studentId, dto, photo));
    }


    // ADMIN ONLY
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{studentId}")
    public ResponseEntity<StudentDTO> deleteStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(studentService.deleteStudent(studentId));
    }


    // ADMIN ONLY
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{studentId}/transfer")
    public ResponseEntity<StudentDTO> transferStudent(
            @PathVariable String studentId,
            @RequestParam String targetClassSectionId) {

        return ResponseEntity.ok(studentService.transferStudent(studentId, targetClassSectionId));
    }


    // ADMIN + TEACHER
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/count")
    public ResponseEntity<Long> countStudents() {
        return ResponseEntity.ok(studentService.getStudentCount());
    }


    // ADMIN + TEACHER
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/dashboard/gender-percentage")
    public ResponseEntity<Map<String, Double>> getGenderPercentage() {
        return ResponseEntity.ok(studentService.getGenderPercentage());
    }

}
