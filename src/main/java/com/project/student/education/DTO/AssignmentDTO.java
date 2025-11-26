package com.project.student.education.DTO;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentDTO {
    private String assignmentId;
    private String subjectId;
    private String title;
    private String description;
    private String createdBy;
    private String assignedTo;
    private String status;
    private LocalDate assignedDate;
    private LocalDate dueDate;
    private String attachedFiles;
}
