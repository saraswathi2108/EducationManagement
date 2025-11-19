package com.project.student.education.DTO;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentExamFinalResultDTO {

    private String studentId;
    private String studentName;

    private String examId;
    private String examName;

    private int numberOfSubjects;
    private double totalMarks;
    private double maxTotalMarks;
    private double percentage;
    private String rank;
    private String finalStatus;

    private List<StudentSubjectResultDTO> subjects;
}
