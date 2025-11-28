package com.project.student.education.service;


import com.project.student.education.DTO.StudentTransportDTO;
import com.project.student.education.DTO.TransportAssignRequest;
import com.project.student.education.entity.IdGenerator;
import com.project.student.education.entity.StudentTransport;
import com.project.student.education.entity.TransportRoute;
import com.project.student.education.repository.StudentTransportRepository;
import com.project.student.education.repository.TransportRouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor



public class TransportService {

    private final IdGenerator idGenerator;
    private final TransportRouteRepository transportRouteRepository;

    private final StudentTransportRepository studentTransportRepository;
    private final NotificationService notificationService;

    public TransportRoute createRoute(TransportRoute route) {
        if (transportRouteRepository.existsByRouteName(route.getRouteName())) {
            throw new RuntimeException("Route with same name already exists");
        }
        route.setRouteId(idGenerator.generateId("TRT"));
        return transportRouteRepository.save(route);
    }

    public TransportRoute updateRoute(String routeId, TransportRoute updated) {
        TransportRoute existing = getRoute(routeId);

        existing.setRouteName(updated.getRouteName());
        existing.setPickupStartTime(updated.getPickupStartTime());
        existing.setDropStartTime(updated.getDropStartTime());
        existing.setVehicleName(updated.getVehicleName());
        existing.setVehicleNumber(updated.getVehicleNumber());
        existing.setDriverName(updated.getDriverName());
        existing.setDriverPhone(updated.getDriverPhone());

        return transportRouteRepository.save(existing);
    }

    public TransportRoute getRoute(String routeId) {
        return transportRouteRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));
    }

    public StudentTransportDTO assignTransport(String studentId, TransportAssignRequest req) {

        TransportRoute route = getRoute(req.getRouteId());

        StudentTransport st = studentTransportRepository
                .findByStudentId(studentId)
                .orElseGet(() -> {
                    StudentTransport newSt = new StudentTransport();
                    newSt.setId(idGenerator.generateId("STT"));
                    newSt.setStudentId(studentId);
                    return newSt;
                });

        st.setRoute(route);
        st.setPickupStop(req.getPickupStop());
        st.setDropStop(req.getDropStop());
        st.setPickupTime(req.getPickupTime());
        st.setDropTime(req.getDropTime());
        st.setFeeStatus(req.getFeeStatus());

        studentTransportRepository.save(st);
        notificationService.sendNotification(
                studentId,
                "transport assigned",
                "Message",

                "transport"
        );

        return mapToDTO(st);
    }




    public StudentTransportDTO getStudentTransportDetails(String studentId) {

        StudentTransport st = studentTransportRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Transport not assigned"));

        return mapToDTO(st);
    }

    public List<StudentTransportDTO> getStudentsByRoute(String routeId) {

        List<StudentTransport> assignments =
                studentTransportRepository.findByRoute_RouteId(routeId);

        if (assignments.isEmpty()) {
            throw new RuntimeException("No students assigned to this route");
        }

        List<StudentTransportDTO> result = new ArrayList<>();

        for (StudentTransport assign : assignments) {

            StudentTransportDTO dto = new StudentTransportDTO();


            dto.setStudentId(assign.getStudentId());


            dto.setRouteName(assign.getRoute().getRouteName());
            dto.setPickupStop(assign.getPickupStop());
            dto.setDropStop(assign.getDropStop());

            dto.setPickupTime(assign.getPickupTime());
            dto.setDropTime(assign.getDropTime());

            if (assign.getRoute() != null) {
                dto.setVehicleName(assign.getRoute().getVehicleName());
                dto.setVehicleNumber(assign.getRoute().getVehicleNumber());
                dto.setDriverName(assign.getRoute().getDriverName());
                dto.setDriverPhone(assign.getRoute().getDriverPhone());
            }

            dto.setFeeStatus(assign.getFeeStatus());

            result.add(dto);
        }

        return result;
    }

    public List<TransportRoute> getAllRoute() {
        return transportRouteRepository.findAll();
    }
    private StudentTransportDTO mapToDTO(StudentTransport st) {

        return StudentTransportDTO.builder()
                .studentId(st.getStudentId())
                .routeName(st.getRoute().getRouteName())
                .pickupStop(st.getPickupStop())
                .dropStop(st.getDropStop())
                .pickupTime(st.getPickupTime())
                .dropTime(st.getDropTime())
                .vehicleName(st.getRoute().getVehicleName())
                .vehicleNumber(st.getRoute().getVehicleNumber())
                .driverName(st.getRoute().getDriverName())
                .driverPhone(st.getRoute().getDriverPhone())
                .feeStatus(st.getFeeStatus())
                .build();
    }
}