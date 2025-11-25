package com.project.student.education.entity;

import com.project.student.education.enums.FeeStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_fee")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentFee {

    @Id
    private String feeId;

    private String studentId;

    private String feeName;

    private Double amount;

    private Double amountPaid;

    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private FeeStatus status;

    private LocalDateTime createdAt;
}
