package com.project.student.education.controller;

import com.project.student.education.DTO.ClassSectionDTO;
import com.project.student.education.DTO.StudentDTO;
import com.project.student.education.service.ClassSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class ClassSectionController {

    private final ClassSectionService classSectionService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/class-sections")
    public ResponseEntity<ClassSectionDTO> createClassSection(@RequestBody ClassSectionDTO dto) {
        return ResponseEntity.ok(classSectionService.createClassSection(dto));
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    @GetMapping("/class-sections")
    public ResponseEntity<List<ClassSectionDTO>> getAllClassSections() {
        return ResponseEntity.ok(classSectionService.getAllClassSections());
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")

    @GetMapping("/class-sections/search")
    public ResponseEntity<ClassSectionDTO> getClassSection(
            @RequestParam String className,
            @RequestParam String section,
            @RequestParam String academicYear) {
        return ResponseEntity.ok(
                classSectionService.getClassSection(className, section, academicYear)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")
    @GetMapping("/class/{classSectionId}/students")
    public ResponseEntity<List<StudentDTO>> getStudentsByClassSection(@PathVariable String classSectionId) {
        List<StudentDTO> students =classSectionService.getStudentsByClassSection(classSectionId);
        return ResponseEntity.ok(students);
    }

    @PreAuthorize("hasRole('ADMIN')")

    @PutMapping("/class-sections/{classSectionId}")
    public ResponseEntity<ClassSectionDTO> updateClassSection(
            @PathVariable String classSectionId,
            @RequestBody ClassSectionDTO dto) {

        ClassSectionDTO updated = classSectionService.updateClassSection(classSectionId, dto);
        return ResponseEntity.ok(updated);
    }


    @PreAuthorize("hasRole('ADMIN')")

    @PutMapping("/class-sections/{classSectionId}/assign-teacher")
    public ResponseEntity<ClassSectionDTO> assignTeacher(
            @PathVariable String classSectionId,
            @RequestParam String teacherId,
            @RequestParam String teacherName) {

        ClassSectionDTO dto = classSectionService.assignTeacher(classSectionId, teacherId, teacherName);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")

    @PutMapping("/class-sections/{classSectionId}/assign-student")
    public ResponseEntity<ClassSectionDTO> assignStudent(
            @PathVariable String classSectionId,
            @RequestParam String studentId) {

        ClassSectionDTO updatedSection = classSectionService.assignStudentToClassSection(classSectionId, studentId);

        return ResponseEntity.ok(updatedSection);
    }

    @PreAuthorize("hasRole('ADMIN')")

    @DeleteMapping("/class-section/{classSectionId}")
    public ResponseEntity<ClassSectionDTO> deleteClassSection(@PathVariable String classSectionId) {
        ClassSectionDTO classSectionDTO=classSectionService.deleteClassSection(classSectionId);
        return ResponseEntity.ok(classSectionDTO);
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")

    @GetMapping("/students/unassigned/{grade}")
    public ResponseEntity<List<StudentDTO>> getUnassignedStudents(@PathVariable String grade) {
        return ResponseEntity.ok(classSectionService.getUnassignedStudentsByGrade(grade));
    }

}
