package com.project.student.education.controller;


import com.project.student.education.entity.FeeHead;
import com.project.student.education.entity.FeeStructure;
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

    @PostMapping("/feeType")
    public ResponseEntity<FeeHead>createFeeType(@RequestBody FeeHead feeHead) {
        return ResponseEntity.ok(feeService.create(feeHead));
    }

    @GetMapping("/feeType/{id}")
    public ResponseEntity<FeeHead>getById(@PathVariable String id){
        return ResponseEntity.ok(feeService.getById(id));


    }
    @GetMapping("/all")
    public ResponseEntity<List<FeeHead>> getAll(){
        return ResponseEntity.ok(feeService.getAll());
    }

    @PostMapping("/feeStructure")
    public ResponseEntity<FeeStructure> createFeeStructure(@RequestBody FeeStructure feeStructure) {
        return ResponseEntity.ok(feeService.createFeeStructure(feeStructure));
    }
    @GetMapping("/feeStructure")
    public ResponseEntity<List<FeeStructure>> getAllFeeStructure() {
        return ResponseEntity.ok(feeService.getAllfee());
    }
    @GetMapping("/feeStructure/class/{classId}")
    public ResponseEntity<List<FeeStructure>> getByClass(
            @PathVariable String classId,
            @RequestParam String academicYear) {

        return ResponseEntity.ok(feeService.getByClassAndYear(classId, academicYear));
    }
    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<FeeStructure>> getByRoute(
            @PathVariable String routeId,
            @RequestParam String academicYear) {

        return ResponseEntity.ok(feeService.getByRouteAndYear(routeId, academicYear));
    }
}
