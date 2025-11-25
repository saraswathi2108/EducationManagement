package com.project.student.education.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeSummaryDTO {
    private Double totalFee;
    private Double paidAmount;
    private Double pendingAmount;
}
