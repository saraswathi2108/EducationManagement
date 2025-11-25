package com.project.student.education.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectMarksDTO {

    private String subjectId;
    private String subjectName;

    private double paperObtained;
    private double paperMax;

    private double assignmentObtained;
    private double assignmentMax;

    private String totalDisplay;  
    private String status; // PASS/FAIL
}
