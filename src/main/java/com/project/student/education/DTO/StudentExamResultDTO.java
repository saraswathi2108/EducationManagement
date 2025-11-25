package com.project.student.education.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentExamResultDTO {

    private String studentId;
    private String studentName;
    private String examId;
    private String examName;

    private int numSubjects;

    private double totalMarksObtained;
    private double maxTotalMarks;

    private double percentage;
    private int rank;

    private String resultMessage;

    private List<SubjectMarksDTO> subjects;
}
