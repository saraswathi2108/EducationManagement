package com.project.student.education.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassSectionDTO {
    private String classSectionId;
    private String className;
    private String section;
    private String academicYear;
    private String classTeacherId;
    private String classTeacherName;
    private Integer capacity;
    private Integer currentStrength;
    private List<String> subjectIds;
}
