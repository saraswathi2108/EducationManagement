package com.project.student.education.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTimetableRequest {

    private String classSectionId;

    private List<PeriodRequest> periods;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PeriodRequest {

        private String day;
        private String subjectId;     // SUB2025002
        private String teacherId;     // TCH2025001
        private String startTime;     // "09:00"
        private String endTime;       // "09:45"
    }
}
