package com.project.student.education.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ClassFeeRequest {
    private String classSectionId;
    private String feeName;
    private Double amount;
    private LocalDate dueDate;
}
