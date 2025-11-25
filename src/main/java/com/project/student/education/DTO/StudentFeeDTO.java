package com.project.student.education.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class StudentFeeDTO {
    private String feeId;
    private String feeName;
    private Double amount;
    private Double amountPaid;
    private LocalDate dueDate;
    private String status;
}