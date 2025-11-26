package com.project.student.education.DTO;

import com.project.student.education.entity.Payment;
import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeeDashboardResponse {

    private FeeSummaryDTO summary;
    private List<StudentFeeDTO> pendingFees;
    private List<StudentFeeDTO> allFees;
    private List<Payment> paymentHistory;
}
