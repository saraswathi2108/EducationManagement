package com.project.student.education.controller;


import com.project.student.education.DTO.ComprehensiveScheduleRequest;
import com.project.student.education.DTO.StudentTransportDTO;
import com.project.student.education.DTO.TransportAssignRequest;
import com.project.student.education.entity.TransportRoute;
import com.project.student.education.service.TransportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/student/transport")
@RequiredArgsConstructor
public class TransportController {

    private final TransportService transportService;


    // ADMIN ONLY — Create route
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/route")
    public ResponseEntity<TransportRoute> create(@RequestBody TransportRoute route) {
        return ResponseEntity.ok(transportService.createRoute(route));
    }


    // ADMIN ONLY — Update route
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TransportRoute> update(
            @PathVariable String id,
            @RequestBody TransportRoute route) {
        return ResponseEntity.ok(transportService.updateRoute(id, route));
    }


    // ADMIN ONLY — Assign transport
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign/{studentId}")
    public ResponseEntity<StudentTransportDTO> assign(
            @PathVariable String studentId,
            @RequestBody TransportAssignRequest assignRequest) {

        return ResponseEntity.ok(transportService.assignTransport(studentId, assignRequest));
    }


    // ADMIN ONLY — Update transport
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/assign/{studentId}")
    public ResponseEntity<StudentTransportDTO> updateTransport(
            @PathVariable String studentId,
            @RequestBody TransportAssignRequest assignRequest) {

        return ResponseEntity.ok(transportService.assignTransport(studentId, assignRequest));
    }


    // ADMIN + STUDENT + PARENT — Student must only access own details
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT','PARENT')")
    @GetMapping("/{studentId}")
    public ResponseEntity<StudentTransportDTO> getDetails(@PathVariable String studentId) {
        return ResponseEntity.ok(transportService.getStudentTransportDetails(studentId));
    }


    // ADMIN + TEACHER — View students using each transport route
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/route/{routeId}/students")
    public ResponseEntity<?> getStudentsByRoute(@PathVariable String routeId) {
        return ResponseEntity.ok(transportService.getStudentsByRoute(routeId));
    }


    // ADMIN + TEACHER — View all transport routes
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    @GetMapping("/routes")
    public ResponseEntity<List<TransportRoute>> getRoutes() {
        return ResponseEntity.ok(transportService.getAllRoute());
    }

}
