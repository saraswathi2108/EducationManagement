package com.project.student.education.DTO;

import lombok.*;

import java.util.List;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentWeeklyTimetableDTO {
    private String studentId;
    private String studentName;
    private String classSectionId;
    private ClassSectionMiniDTO classSection;
    private TeacherMiniDTO classTeacher;

    private List<DayEntry> weeklyTimetable;

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class DayEntry {
        private String day;
        private String date;
        private List<Period> periods;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class Period {
        private String startTime;
        private String endTime;
        private String subjectId;
        private String subjectName;
        private String teacherId;
        private String teacherName;
    }
}
