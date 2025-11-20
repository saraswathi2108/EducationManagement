package com.project.student.education.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceViewDTO {

    private String studentId;
    private int present;
    private int absent;
    private int holidays;
    private double percentage;

    private List<Daily> dailyRecords;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Daily {
        private LocalDate date;
        private String status;
    }
}
