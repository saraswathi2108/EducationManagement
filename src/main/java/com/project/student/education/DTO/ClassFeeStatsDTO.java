package com.project.student.education.DTO;
import lombok.Builder;
import lombok.Data;
 
@Data
@Builder
public class ClassFeeStatsDTO {
    private String classSectionId;
    private String className;
    private String section;
    private Double totalExpectedFee;
    private Double totalCollectedFee;
    private Double totalPendingFee;
}