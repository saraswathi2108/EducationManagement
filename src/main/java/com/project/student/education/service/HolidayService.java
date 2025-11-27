package com.project.student.education.service;


import com.project.student.education.entity.Holiday;
import com.project.student.education.repository.HolidayRepository;
import com.project.student.education.repository.StudentRepository;
import com.project.student.education.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service

public class HolidayService {

    private final HolidayRepository  holidayRepository;
    private final StudentRepository studentRepository;
    private final NotificationService notificationService;

    public HolidayService(HolidayRepository holidayRepository, StudentRepository studentRepository, NotificationService notificationService) {
        this.holidayRepository = holidayRepository;
        this.studentRepository = studentRepository;
        this.notificationService = notificationService;
    }
    private TeacherRepository  teacherRepository;


    public Holiday createHoliday(Holiday holiday) {

        if (holidayRepository.existsByDate(holiday.getDate())) {
            throw new RuntimeException("Holiday already exists for this date");
        }

        Holiday saved= holidayRepository.save(holiday);
        // 🔔 Send notification to all students
        studentRepository.findAll().forEach(student -> {
            notificationService.sendNotification(
                    student.getStudentId(),
                    "Holiday Announced",
                    "Holiday on " + saved.getDate() + ": " + saved.getDescription(),
                    "HOLIDAY"
            );
        });

        teacherRepository.findAll().forEach(teacher -> {
            notificationService.sendNotification(
                    teacher.getTeacherId(),
                    "Holiday Announced",
                    "Holiday on " + saved.getDate() + ": " + saved.getDescription(),
                    "HOLIDAY"
            );
        });
        return  saved;
    }

    public List<Holiday> getHolidays(LocalDate start, LocalDate end) {
        return holidayRepository.findByDateBetween(start, end);
    }
}
