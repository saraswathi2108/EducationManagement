package com.project.student.education.DTO;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRequest {

    private String classSectionId;
    private LocalDate date;
    private List<Entry> entries;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entry {
        private String studentId;
        private String status;
    }
}

