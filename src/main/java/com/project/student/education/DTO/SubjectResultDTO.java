package com.project.student.education.DTO;

import com.project.student.education.enums.ExamAttendanceStatus;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SubjectResultDTO {

    private String subjectId;
    private String subjectName;

    private Double paperObtained;
    private Double paperTotal;

    private Double assignmentObtained;
    private Double assignmentTotal;

    private Double subjectTotalObtained;
    private Double subjectTotalMax;

    private ExamAttendanceStatus attendanceStatus;
    private String status;
}
