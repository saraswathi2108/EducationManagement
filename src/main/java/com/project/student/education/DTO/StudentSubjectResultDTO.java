package com.project.student.education.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSubjectResultDTO {

    private String subjectName;

    private double paperObtained;
    private double paperTotal;

    private double assignmentObtained;
    private double assignmentTotal;

    private double totalObtained;   // paper + assignment
    private double totalMax;        // paperTotal + assignmentTotal

    private String status; // PASS / FAIL
}
