package com.project.student.education.service;


import com.project.student.education.entity.Holiday;
import com.project.student.education.repository.HolidayRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HolidayService {

    private final HolidayRepository  holidayRepository;

    public HolidayService(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }


    public Holiday createHoliday(Holiday holiday) {

        if (holidayRepository.existsByDate(holiday.getDate())) {
            throw new RuntimeException("Holiday already exists for this date");
        }

        return holidayRepository.save(holiday);
    }

    public List<Holiday> getHolidays(LocalDate start, LocalDate end) {
        return holidayRepository.findByDateBetween(start, end);
    }
}
