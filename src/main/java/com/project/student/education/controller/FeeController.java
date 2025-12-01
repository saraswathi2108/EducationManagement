package com.project.student.education.controller;


import AIExpose.Agent.Annotations.AIExposeController;
import AIExpose.Agent.Annotations.AIExposeEpHttp;
import AIExpose.Agent.Annotations.Describe;
import com.project.student.education.DTO.*;
import com.project.student.education.entity.Payment;
import com.project.student.education.entity.StudentFee;
import com.project.student.education.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/student/fee")
@AIExposeController
public class FeeController {

    @Autowired
    private FeeService feeService;



    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/create")
    public ResponseEntity<StudentFee> createFee(@RequestBody CreateFeeRequest req) {
        return ResponseEntity.ok(feeService.createFee(req));
    }

    @AIExposeEpHttp(
            name = "Bulk Create Student Fees",
            description = "Creates fee entries for multiple students in one request using a list of CreateFeeRequest DTOs.",
            autoExecute = true,
            tags = {"Fees", "Bulk", "Admin", "Create"},
            reqParams = @Describe(
                    name = "reqs",
                    description = "List of CreateFeeRequest objects representing multiple student fees.",
                    dataType = "List<CreateFeeRequest>",
                    example = "See CreateFeeRequest DTO for detailed structure."
            ),
            returnDescription = "Returns a list of StudentFee objects created for the students."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/bulk-create")
    public ResponseEntity<List<StudentFee>> bulk(@RequestBody List<CreateFeeRequest> reqs) {
        return ResponseEntity.ok(feeService.bulkCreate(reqs));
    }


    @AIExposeEpHttp(
            name = "Get All Fees for Student",
            description = "Fetches all fee records associated with a specific student using their studentId.",
            autoExecute = true,
            tags = {"Fees", "Admin", "Get", "Student"},
            pathParams = @Describe(
                    name = "studentId",
                    description = "Unique ID of the student whose fee records need to be retrieved.",
                    dataType = "String",
                    example = "STU2025003"
            ),
            returnDescription = "Returns a list of StudentFeeDTO objects containing fee details for the student."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/student/{studentId}")
    public ResponseEntity<List<StudentFeeDTO>> allForStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(feeService.getAllFees(studentId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/payments")
    public ResponseEntity<List<Payment>> allPayments() {
        return ResponseEntity.ok(feeService.getAllPayments());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/class-fee")
    public ResponseEntity<ClassFeeResponse> createFeeForClass(@RequestBody ClassFeeRequest req) {
        return ResponseEntity.ok(feeService.createFeeForClass(req));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/dashboard/stats")
    public ResponseEntity<List<ClassFeeStatsDTO>> getAdminFeeStats(@RequestHeader String Authorization) {
        return ResponseEntity.ok(feeService.getAllClassesFeeStats());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/class-status/{classSectionId}")
    public ResponseEntity<List<StudentFeeStatusDTO>> getClassFeeStatus(@PathVariable String classSectionId) {
        return ResponseEntity.ok(feeService.getClassStudentFeeStatus(classSectionId));
    }


    @PreAuthorize("hasAnyRole('STUDENT','PARENT','ADMIN')")
    @GetMapping("/student/summary/{studentId}")
    public ResponseEntity<FeeSummaryDTO> summary(@PathVariable String studentId) {
        return ResponseEntity.ok(feeService.getSummary(studentId));
    }

    @PreAuthorize("hasAnyRole('STUDENT','PARENT','ADMIN')")
    @GetMapping("/student/pending/{studentId}")
    public ResponseEntity<List<StudentFeeDTO>> pending(@PathVariable String studentId) {
        return ResponseEntity.ok(feeService.getPendingFees(studentId));
    }

    @PreAuthorize("hasAnyRole('STUDENT','PARENT','ADMIN')")
    @GetMapping("/student/all/{studentId}")
    public ResponseEntity<List<StudentFeeDTO>> all(@PathVariable String studentId) {
        return ResponseEntity.ok(feeService.getAllFees(studentId));
    }

    @PreAuthorize("hasAnyRole('STUDENT','PARENT','ADMIN')")
    @PostMapping("/student/pay")
    public ResponseEntity<Payment> pay(@RequestBody CreatePaymentRequest req) {
        return ResponseEntity.ok(feeService.pay(req));
    }

    @PreAuthorize("hasAnyRole('STUDENT','PARENT','ADMIN')")
    @GetMapping("/student/payments/{studentId}")
    public ResponseEntity<List<Payment>> history(@PathVariable String studentId) {
        return ResponseEntity.ok(feeService.getPaymentHistory(studentId));
    }

    @PreAuthorize("hasAnyRole('STUDENT','PARENT','ADMIN')")
    @GetMapping("/student/dashboard/{studentId}")
    public ResponseEntity<FeeDashboardResponse> dashboard(@PathVariable String studentId) {
        return ResponseEntity.ok(feeService.getFeeDashboard(studentId));
    }
}
