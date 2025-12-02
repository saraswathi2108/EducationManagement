package com.project.student.education.DTO;

import com.project.student.education.DTO.StudentMiniDTO;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamRecordDTO {

    private Long recordId;


    private String examId;
    private String classSectionId;
    private String subjectId;
    private String studentId;
    private String invigilatorId;



    private StudentMiniDTO student;
    private ClassSectionMiniDTO classSection;
    private SubjectMiniDTO subject;
    private TeacherMiniDTO invigilator;


    private LocalDate examDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private String roomNumber;


    private Double maxMarks;
    private Double passMarks;

    private Double marksObtained;
    private String grade;


    private String attendanceStatus;

    private String status;

    private String remarks;

    private Boolean revaluationRequested;
    private String revaluationStatus;

    private String enteredBy;
    private String verifiedBy;
    private String publishedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
