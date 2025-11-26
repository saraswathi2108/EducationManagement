package com.project.student.education.DTO;
import lombok.Builder;
import lombok.Data;
 
@Data
@Builder
public class StudentFeeStatusDTO {
    private String studentId;
    private String studentName;
    private String rollNumber;
    private Double totalFee;    // From Student entity
    private Double paidAmount;  // From StudentFee table sum
    private Double balanceAmount;
    private String status;      // PAID / PARTIAL / PENDING
}