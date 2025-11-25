package com.project.student.education.DTO;

import lombok.*;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MarksEntryRequest {

    private Long recordId;

    private String studentId;

    private Double paperObtained;
    private Double paperTotal;

    private Double assignmentObtained;
    private Double assignmentTotal;

    private String attendanceStatus;
    private String remarks;
}
