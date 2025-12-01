package com.project.student.education.entity;

import AIExpose.Agent.Annotations.*;
import com.project.student.education.enums.FeeStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_fee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AIExposeDto(
        name = "StudentFee",
        description = "Represents a fee assigned to a student, including amount, due date, payment status, and timestamps."
)
public class StudentFee {

    @Id
    @Describe(
            description = "Unique identifier for the fee record.",
            dataType = "String",
            example = "FEE2025001"
    )
    private String feeId;

    @Describe(
            description = "ID of the student to whom this fee belongs.",
            dataType = "String",
            example = "STU2025001"
    )
    private String studentId;

    @Describe(
            description = "Name or type of the fee assigned to the student.",
            dataType = "String",
            example = "Tuition Fee"
    )
    private String feeName;

    @Describe(
            description = "Total amount assigned for the fee.",
            dataType = "Double",
            example = "1500.0"
    )
    private Double amount;

    @Describe(
            description = "The amount already paid towards this fee.",
            dataType = "Double",
            example = "500.0"
    )
    private Double amountPaid;

    @Describe(
            description = "The last date by which the fee must be paid.",
            dataType = "LocalDate",
            example = "2025-01-15"
    )
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Describe(
            description = "The current payment status of the fee.",
            dataType = "FeeStatus",
            example = "PARTIAL"
    )
    private FeeStatus status;

    @Describe(
            description = "Timestamp when the fee record was created.",
            dataType = "LocalDateTime",
            example = "2025-01-02T10:15:45"
    )
    private LocalDateTime createdAt;
}
