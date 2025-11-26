package com.project.student.education.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.student.education.config.AssignmentId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assignment {

    @EmbeddedId
    private AssignmentId id;

    private String title;
    private String description;
    private String createdBy;
    private String assignedTo;
    private String status;

    private LocalDate assignedDate;
    private LocalDate dueDate;
    private String attachedFiles;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    @JsonBackReference("teacher-assignments")
    private Teacher teacher;

    @ManyToOne
    @MapsId("subjectId")
    @JoinColumn(name = "subject_id", referencedColumnName = "subjectId")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "class_section_id")
    private ClassSection classSection;


    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("assignment-submission")
    private List<AssignmentSubmission> submissionHistory;



}
