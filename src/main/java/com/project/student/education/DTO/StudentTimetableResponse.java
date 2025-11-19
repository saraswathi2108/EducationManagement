package com.project.student.education.DTO;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentTimetableResponse {

    private String studentId;
    private String studentName;

    private ClassSectionMiniDTO classSection;   // className, section, academicYear

    private TeacherMiniDTO classTeacher;

    private List<StudentSubjectTimetableDTO> subjects;
    private List<TimetableMiniDTO> todayTimetable;

    private List<WeeklyTimetableDTO> fullWeekTimetable; // grouped by day
}
