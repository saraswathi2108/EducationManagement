package com.project.student.education.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.student.education.config.AssignmentSubmissionId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentSubmission {

    @EmbeddedId
    private AssignmentSubmissionId id;

    private LocalDateTime submittedDate;
    private String studentId;
    private String note;
    private String remark;
    private String reviewedBy;
    private String status;
    private List<String> relatedLinks;

    @ManyToOne
    @MapsId("assignmentId")
    @JoinColumns({
            @JoinColumn(name = "assignment_id", referencedColumnName = "assignment_id"),
            @JoinColumn(name = "subject_id", referencedColumnName = "subject_id")
    })
    @JsonBackReference("assignment-submission")
    private Assignment assignment;

}
