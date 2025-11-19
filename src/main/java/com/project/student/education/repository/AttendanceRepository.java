package com.project.student.education.repository;

import com.project.student.education.entity.StudentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface AttendanceRepository  extends JpaRepository<StudentAttendance, Integer> {
    boolean existsByClassSectionIdAndDate(String classSectionId, LocalDate date);
}
