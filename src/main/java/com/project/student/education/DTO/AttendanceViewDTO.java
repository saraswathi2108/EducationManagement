package com.project.student.education.DTO;

import AIExpose.Agent.Annotations.AIExposeDto;
import AIExpose.Agent.Annotations.Describe;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AIExposeDto(
        name = "AttendanceViewDTO",
        description = "Response containing a student's attendance summary and daily breakdown."
)
public class AttendanceViewDTO {

    @Describe(
            description = "ID of the student whose attendance is being displayed.",
            dataType = "String",
            example = "STU2025001"
    )
    private String studentId;

    @Describe(
            description = "Total number of days the student was present.",
            dataType = "int",
            example = "120"
    )
    private int present;

    @Describe(
            description = "Total number of days the student was absent.",
            dataType = "int",
            example = "15"
    )
    private int absent;

    @Describe(
            description = "Total number of holidays during the selected period.",
            dataType = "int",
            example = "20"
    )
    private int holidays;

    @Describe(
            description = "Attendance percentage calculated as (present / workingDays) * 100.",
            dataType = "double",
            example = "88.54"
    )
    private double percentage;

    @Describe(
            description = "List of daily attendance entries with date and status.",
            dataType = "List<Daily>",
            example = "[{date:'2025-01-05', status:'PRESENT'}]"
    )
    private List<Daily> dailyRecords;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @AIExposeDto(
            name = "AttendanceDailyRecord",
            description = "Represents attendance status for a specific date."
    )
    public static class Daily {

        @Describe(
                description = "Date for which attendance is recorded.",
                dataType = "LocalDate",
                example = "2025-01-05"
        )
        private LocalDate date;

        @Describe(
                description = "Attendance status for the day (PRESENT, ABSENT, HOLIDAY, NOT_MARKED).",
                dataType = "String",
                example = "PRESENT"
        )
        private String status;
    }
}
