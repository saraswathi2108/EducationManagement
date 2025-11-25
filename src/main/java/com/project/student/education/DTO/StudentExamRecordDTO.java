package com.project.student.education.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentExamRecordDTO {

    private Long recordId;

    private String studentId;
    private String fullName;
    private String rollNumber;
}
