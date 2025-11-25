package com.project.student.education.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    private String paymentId;

    private String feeId;

    private String studentId;

    private Double amount;

    private LocalDateTime paymentDate;

    private String method;

    private String transactionRef;
}
