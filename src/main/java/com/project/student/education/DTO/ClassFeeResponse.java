package com.project.student.education.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClassFeeResponse {
    private String classSectionId;
    private String feeName;
    private Double amount;
    private int totalStudents;
    private String status;
}
