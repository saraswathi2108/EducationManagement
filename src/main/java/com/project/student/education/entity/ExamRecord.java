package com.project.student.education.entity;

import com.project.student.education.enums.ExamAttendanceStatus;
import com.project.student.education.enums.ExamResultStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "exam_record",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"exam_id", "class_section_id", "subject_id", "student_id", "exam_date"}
        )
)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ExamRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @Column(name = "exam_id", nullable = false)
    private String examId;

    @Column(name = "class_section_id", nullable = false)
    private String classSectionId;

    @Column(name = "subject_id", nullable = false)
    private String subjectId;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "invigilator_id")
    private String invigilatorId;


    // RELATIONS
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_section_id", insertable = false, updatable = false)
    private ClassSection classSection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", insertable = false, updatable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    private Student student;


    // EXAM INFO
    private LocalDate examDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private String roomNumber;


    // PAPER MARKS
    private Double paperTotal;        // Example 80
    private Double paperObtained;     // Example 70

    // ASSIGNMENT MARKS
    private Double assignmentTotal;   // Example 20
    private Double assignmentObtained; // Example 18

    // CONDITIONS
    private Double passMarks;

    @Enumerated(EnumType.STRING)
    private ExamAttendanceStatus attendanceStatus;

    // STATUS (ENTERED / PUBLISHED)
    @Enumerated(EnumType.STRING)
    private ExamResultStatus resultStatus;


    private String remarks;
    private String enteredBy;
    private String publishedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
