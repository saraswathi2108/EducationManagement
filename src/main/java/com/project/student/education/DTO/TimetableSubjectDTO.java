package com.project.student.education.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetableSubjectDTO {
    private String subjectId;
    private String subjectName;
    private String startTime;
    private String endTime;
    private String roomNumber;
    private String className;
    private String sectionName;

}
