package com.project.student.education.controller;

import AIExpose.Agent.Annotations.AIExposeController;
import AIExpose.Agent.Annotations.AIExposeEpHttp;
import AIExpose.Agent.Annotations.Describe;
import com.project.student.education.DTO.AdmissionDTO;
import com.project.student.education.DTO.ApproveAdmissionRequest;
import com.project.student.education.DTO.StudentDTO;
import com.project.student.education.entity.Admission;
import com.project.student.education.service.AdmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@AIExposeController
public class AdmissionController {

    @Autowired
    private  AdmissionService admissionService;



    @AIExposeEpHttp(
            name = "Submit Admission Application",
            description = "Creates a new admission entry with applicant information and optional photo upload.",
            autoExecute = true,
            tags = {"Admission", "Student", "Create"},
            reqParams = @Describe(
                    name = "Admission + Photo",
                    description = "Admission form data along with optional student photo file.",
                    dataType = "multipart/form-data"
            ),
            returnDescription = "Returns the saved admission details."
    )
    @PostMapping(value = "/admission", consumes = "multipart/form-data")
    public ResponseEntity<AdmissionDTO> submitAdmission(
            @RequestPart("data") Admission admission,
            @RequestPart(value = "photo", required = false) MultipartFile photoUrl
    ) {
        AdmissionDTO admissionDTO = admissionService.submitAdmission(admission, photoUrl);
        return new ResponseEntity<>(admissionDTO, HttpStatus.OK);
    }

    @AIExposeEpHttp(
            name = "Approve Admission",
            description = "Approves a submitted admission and creates a student profile with login credentials.",
            autoExecute = true,
            tags = {"Admission", "Admin", "Approve"},
            pathParams = @Describe(
                    name = "admissionNumber",
                    description = "The admission number to approve.",
                    dataType = "String",
                    example = "ADM2025004"
            ),
            reqParams = @Describe(
                    name = "ApproveAdmissionRequest",
                    description = "Contains approvedBy and academicYear fields.",
                    dataType = "ApproveAdmissionRequest"
            ),
            returnDescription = "Returns the created StudentDTO with generated student credentials."
    )
    //@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/approve/{admissionNumber}")
    public ResponseEntity<StudentDTO> approveAdmission(
            @PathVariable String admissionNumber,
            @RequestBody ApproveAdmissionRequest request
    ) {
        StudentDTO studentDTO = admissionService.approveAdmission(
                admissionNumber,
                request.getApprovedBy(),
                request.getAcademicYear()
        );
        return ResponseEntity.ok(studentDTO);
    }

    @AIExposeEpHttp(
            name = "Get All Admissions",
            description = "Fetches all submitted admission applications.",
            autoExecute = true,
            tags = {"Admission", "Admin", "Get"},
            returnDescription = "Returns a list of all admission applications."
    )
   // @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    @GetMapping("/admissions")
    public ResponseEntity<List<AdmissionDTO>> getAllAdmissions() {
        return ResponseEntity.ok(admissionService.getAllAdmissions());
    }
   // @PreAuthorize("hasRole('ROLE_ADMIN')")


    @AIExposeEpHttp(
            name = "Reject Admission",
            description = "Rejects an admission with an optional reason.",
            autoExecute = true,
            tags = {"Admission", "Admin", "Reject"},
            pathParams = @Describe(
                    name = "admissionNumber",
                    description = "Admission number to reject.",
                    dataType = "String"
            ),
            reqParams = @Describe(
                    name = "reason",
                    description = "Optional rejection reason.",
                    dataType = "String"
            ),
            returnDescription = "Returns updated AdmissionDTO with rejection details."
    )
    @PostMapping("/reject/{admissionNumber}")
    public ResponseEntity<AdmissionDTO> rejectAdmission(
            @PathVariable String admissionNumber,
            @RequestParam(required = false) String reason
    ) {
        AdmissionDTO admissionDTO = admissionService.rejectAdmission(admissionNumber, reason);
        return ResponseEntity.ok(admissionDTO);
    }

   // @PreAuthorize("hasRole('ROLE_ADMIN')")

    @DeleteMapping("/admission/{admissionNumber}")
    public ResponseEntity<AdmissionDTO> deleteAdmission(@PathVariable String admissionNumber) {
        AdmissionDTO admissionDTO=admissionService.deleteAdmission(admissionNumber);
        return ResponseEntity.ok(admissionDTO);
    }


    @AIExposeEpHttp(
            name = "Get Pending Admissions",
            description = "Fetches all admissions with pending status.",
            autoExecute = true,
            tags = {"Admission", "Admin", "Pending"},
            returnDescription = "Returns list of pending admission applications."
    )
    //PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admissions/pending")
    public ResponseEntity<List<AdmissionDTO>> getPendingAdmissions() {
        return ResponseEntity.ok(admissionService.getAdmissionsByStatus("PENDING"));
    }





}
