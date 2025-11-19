package com.project.student.education.repository;

import com.project.student.education.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimetableRepository extends JpaRepository<Timetable, String > {
    boolean existsByClassSection_ClassSectionIdAndDayAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(String classSectionId, String day, String endTime, String startTime);

    List<Timetable> findByClassSection_ClassSectionIdAndDayOrderByStartTimeAsc(String classSectionId, String day);

    List<Timetable> findByClassSection_ClassSectionIdAndDay(String classSectionId, String today);

    List<Timetable> findByClassSection_ClassSectionIdOrderByDayAscStartTimeAsc(String classSectionId);

    boolean existsByClassSection_ClassSectionIdAndDayAndStartTimeAndEndTime(String classSectionId, String day, String startTime, String endTime);

    boolean existsByTeacher_TeacherIdAndDayAndStartTimeAndEndTime(String teacherId, String day, String startTime, String endTime);

    List<Timetable> findByClassSection_ClassSectionId(String classSectionId);
}
