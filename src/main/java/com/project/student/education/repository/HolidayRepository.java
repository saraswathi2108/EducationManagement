package com.project.student.education.repository;

import com.project.student.education.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    List<Holiday> findByDateBetween(LocalDate start, LocalDate end);

    boolean existsByDate(LocalDate date);
}
