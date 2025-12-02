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
        name = "AttendanceRequest",
        description = "Request payload used for marking attendance for a class on a specific date."
)
public class AttendanceRequest {

    @Describe(
            description = "ID of the class section for which attendance is being marked.",
            dataType = "String",
            example = "CLS2025A1"
    )
    private String classSectionId;

    @Describe(
            description = "Date for which attendance is recorded.",
            dataType = "LocalDate",
            example = "2025-01-20"
    )
    private LocalDate date;

    @Describe(
            description = "List of attendance entries containing studentId and attendance status.",
            dataType = "List<Entry>",
            example = "[{studentId:'STU2025001', status:'P'}]"
    )
    private List<Entry> entries;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @AIExposeDto(
            name = "AttendanceEntry",
            description = "Represents attendance status for an individual student."
    )
    public static class Entry {

        @Describe(
                description = "ID of the student for whom attendance is recorded.",
                dataType = "String",
                example = "STU2025001"
        )
        private String studentId;

        @Describe(
                description = "Attendance status for the student. Possible values: P (Present), A (Absent), etc.",
                dataType = "String",
                example = "P"
        )
        private String status;
    }
}
