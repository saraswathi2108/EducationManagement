package com.project.student.education.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class FeeStructure {

    @Id
    private String id;

    // Which fee head? (TUITION / TRANSPORT / ANNUAL / EXAM / LAB ...)
    private String feeHeadId;
    // Example: "CLASS_8", "CLASS_10"
    private String classSectionId;     // nullable for transport-only fees

    // For transport fees (based on route)
    // Example: "ROUTE_12"
    private String routeId;

    // For term-based fee (Term 1 / Term 2 / Term 3)
    // Example: "TERM1", "TERM2", "TERM3" – optional
    private String termCode;    // nullable for non-term fees

    // Academic year label – example "2025-26"
    private String academicYear;

    private Double amount;
}
