package com.project.student.education.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateFeeRequest {
    private String studentId;
    private String feeName;
    private Double amount;
    private LocalDate dueDate;
}