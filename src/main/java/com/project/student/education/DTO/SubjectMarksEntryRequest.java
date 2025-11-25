package com.project.student.education.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubjectMarksEntryRequest {
    private String examId;
    private String classSectionId;
    private String subjectId;

    private List<MarksEntryRequest> entries;
}
