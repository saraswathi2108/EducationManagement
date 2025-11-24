package com.project.student.education.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class StudentTransport {

    @Id
    private String id;

    private String studentId;

    @ManyToOne
    @JoinColumn(name = "route_id", referencedColumnName = "routeId")
    private TransportRoute route;

    private String pickupStop;
    private String dropStop;
    private String pickupTime;
    private String dropTime;

    private String feeStatus;
}
