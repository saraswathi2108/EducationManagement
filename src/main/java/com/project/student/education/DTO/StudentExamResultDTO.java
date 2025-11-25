package com.project.student.education.DTO;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StudentExamResultDTO {

    private String examId;
    private String examName;

    private String studentId;
    private String studentName;
    private String className;
    private String section;

    private Double totalMarksObtained;
    private Double totalMarksMax;
    private Double percentage;

    private Integer rank;

    private List<SubjectResultDTO> subjects;
}
