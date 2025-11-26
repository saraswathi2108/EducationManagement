package com.project.student.education.controller;


import com.project.student.education.DTO.*;
import com.project.student.education.entity.Payment;
import com.project.student.education.entity.StudentFee;
import com.project.student.education.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/fee")
public class FeeController {

    @Autowired
    private FeeService feeService;

    @PostMapping("/admin/create")
    public ResponseEntity<StudentFee> createFee(@RequestBody CreateFeeRequest req) {
        return ResponseEntity.ok(feeService.createFee(req));
    }

    @PostMapping("/admin/bulk-create")
    public ResponseEntity<List<StudentFee>> bulk(@RequestBody List<CreateFeeRequest> reqs) {
        return ResponseEntity.ok(feeService.bulkCreate(reqs));
    }

    @GetMapping("/admin/student/{studentId}")
    public ResponseEntity<List<StudentFeeDTO>> allForStudent(@PathVariable String studentId) {
        return ResponseEntity.ok(feeService.getAllFees(studentId));
    }

    @GetMapping("/admin/payments")
    public ResponseEntity<List<Payment>> allPayments() {
        return ResponseEntity.ok(feeService.getAllPayments());
    }

    @GetMapping("/student/summary/{studentId}")
    public ResponseEntity<FeeSummaryDTO> summary(@PathVariable String studentId) {
        return ResponseEntity.ok(feeService.getSummary(studentId));
    }

    @GetMapping("/student/pending/{studentId}")
    public ResponseEntity<List<StudentFeeDTO>> pending(@PathVariable String studentId) {
        return ResponseEntity.ok(feeService.getPendingFees(studentId));
    }
    @GetMapping("/student/all/{studentId}")
    public ResponseEntity<List<StudentFeeDTO>> all(@PathVariable String studentId) {
        return ResponseEntity.ok(feeService.getAllFees(studentId));
    }
    @PostMapping("/student/pay")
    public ResponseEntity<Payment> pay(@RequestBody CreatePaymentRequest req) {
        return ResponseEntity.ok(feeService.pay(req));
    }
    @GetMapping("/student/payments/{studentId}")
    public ResponseEntity<List<Payment>> history(@PathVariable String studentId) {
        return ResponseEntity.ok(feeService.getPaymentHistory(studentId));
    }

    @GetMapping("/student/dashboard/{studentId}")
    public ResponseEntity<FeeDashboardResponse> dashboard(@PathVariable String studentId) {
        return ResponseEntity.ok(feeService.getFeeDashboard(studentId));
    }

    @PostMapping("/admin/class-fee")
    public ResponseEntity<ClassFeeResponse> createFeeForClass(@RequestBody ClassFeeRequest req) {
        return ResponseEntity.ok(feeService.createFeeForClass(req));
    }

    @GetMapping("/admin/dashboard/stats")
    public ResponseEntity<List<ClassFeeStatsDTO>> getAdminFeeStats() {
        return ResponseEntity.ok(feeService.getAllClassesFeeStats());
    }

    @GetMapping("/admin/class-status/{classSectionId}")
    public ResponseEntity<List<StudentFeeStatusDTO>> getClassFeeStatus(@PathVariable String classSectionId) {
        return ResponseEntity.ok(feeService.getClassStudentFeeStatus(classSectionId));
    }




}
