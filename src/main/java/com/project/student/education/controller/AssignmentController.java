package com.project.student.education.controller;

import com.project.student.education.DTO.AssignmentDTO;
import com.project.student.education.service.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/student")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;


    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping(
            value = "/assignment/{teacherId}/{subjectId}/{classSectionId}",
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }
    )    public ResponseEntity<AssignmentDTO> createAssignment(
            @PathVariable String teacherId,
            @PathVariable String subjectId,
            @PathVariable String classSectionId,
            @RequestPart("data") AssignmentDTO assignmentDTO,
            @RequestPart(value = "file", required = false) MultipartFile attachedFiles
    ) throws IOException {

        AssignmentDTO response = assignmentService.createAssignment(
                teacherId, subjectId, classSectionId, assignmentDTO, attachedFiles
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping(
            value = "/assignment/{subjectId}/{assignmentId}",
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }
    )
    public ResponseEntity<AssignmentDTO> updateAssignment(
            @PathVariable String subjectId,
            @PathVariable String assignmentId,
            @RequestPart("data") AssignmentDTO assignmentDTO,
            @RequestPart(value = "file", required = false) MultipartFile attachedFiles
    ) throws IOException {

        AssignmentDTO updated = assignmentService.updateAssignment(
                subjectId,
                assignmentId,
                assignmentDTO,
                attachedFiles
        );

        return ResponseEntity.ok(updated);
    }


    @PreAuthorize("hasAnyRole('STUDENT','TEACHER')")
    @GetMapping("/assignment/{subjectId}/{assignmentId}")
    public ResponseEntity<AssignmentDTO> getAssignment(@PathVariable String subjectId, @PathVariable String assignmentId) {
        AssignmentDTO assignmentDTO = assignmentService.getAssignment(subjectId, assignmentId);
        return new ResponseEntity<>(assignmentDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/assignment/{subjectId}/{assignmentId}")
    public ResponseEntity<AssignmentDTO> deleteAssignment(@PathVariable String subjectId, @PathVariable String assignmentId) {
        AssignmentDTO assignmentDTO = assignmentService.deleteAssignment(subjectId, assignmentId);
        return new ResponseEntity<>(assignmentDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('STUDENT','TEACHER')")

    @GetMapping("/assignments/teacher/{teacherId}")
    public ResponseEntity<List<AssignmentDTO>> getAllAssignments(@PathVariable String teacherId) {
        List<AssignmentDTO> assignmentDTO = assignmentService.getAllByTeacher(teacherId);
        return new ResponseEntity<>(assignmentDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('STUDENT','TEACHER')")
    @GetMapping("/assignments/subject/{subjectId}")
    public ResponseEntity<List<AssignmentDTO>> getAllAssignmentsBySubject(@PathVariable String subjectId) {
        List<AssignmentDTO> assignmentDTOS = assignmentService.getAllAssignmentsBySubject(subjectId);
        return new ResponseEntity<>(assignmentDTOS, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('STUDENT','TEACHER')")
    @GetMapping("/assignments/class/{classSectionId}")
    public ResponseEntity<List<AssignmentDTO>> getAllAssignmentsByClass(@PathVariable String classSectionId) {
        List<AssignmentDTO> assignmentDTOS = assignmentService.getAllAssignmentsByClass(classSectionId);
        return new ResponseEntity<>(assignmentDTOS, HttpStatus.OK);
    }
}
