package com.project.student.education.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentSubmissionDTO {

    private String assignmentId;
    private String subjectId;
    private Long submissionNumber;
    private String studentId;
    private LocalDateTime submittedDate;
    private String note;
    private String remark;
    private String reviewedBy;
    private String status;

    private List<String> relatedLinks;
}
