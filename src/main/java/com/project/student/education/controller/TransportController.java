package com.project.student.education.controller;


import com.project.student.education.DTO.ComprehensiveScheduleRequest;
import com.project.student.education.DTO.StudentTransportDTO;
import com.project.student.education.DTO.TransportAssignRequest;
import com.project.student.education.entity.TransportRoute;
import com.project.student.education.service.TransportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/transport")
@RequiredArgsConstructor
public class TransportController {

    private final TransportService transportService;



    @PostMapping("/route")
    public ResponseEntity<TransportRoute> create(@RequestBody TransportRoute route) {
        return ResponseEntity.ok(transportService.createRoute(route));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransportRoute> update(
            @PathVariable String id,
            @RequestBody TransportRoute route) {
        return ResponseEntity.ok(transportService.updateRoute(id, route));
    }

    @PostMapping("/assign/{studentId}")
    public ResponseEntity<StudentTransportDTO> assign(
            @PathVariable String studentId,
            @RequestBody TransportAssignRequest assignRequest) {

        return ResponseEntity.ok(transportService.assignTransport(studentId, assignRequest));
    }

    @PutMapping("/assign/{studentId}")
    public ResponseEntity<StudentTransportDTO> updateTransport(
            @PathVariable String studentId,
            @RequestBody TransportAssignRequest assignRequest) {

        return ResponseEntity.ok(
                transportService.assignTransport(studentId, assignRequest)
        );
    }
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentTransportDTO> getDetails(@PathVariable String studentId) {
        return ResponseEntity.ok(transportService.getStudentTransportDetails(studentId));
    }


    @GetMapping("/route/{routeId}/students")
    public ResponseEntity<?> getStudentsByRoute(@PathVariable String routeId) {
        return ResponseEntity.ok(transportService.getStudentsByRoute(routeId));
    }

    @GetMapping("/routes")
    public ResponseEntity<List<TransportRoute>> getRoutes() {
        return ResponseEntity.ok(transportService.getAllRoute());
    }





}
