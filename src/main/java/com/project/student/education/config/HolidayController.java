package com.project.student.education.config;


import com.project.student.education.entity.Holiday;
import com.project.student.education.service.HolidayService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/student/calender")
public class HolidayController {


    private final HolidayService holidayService;

    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @PostMapping("/holiday")
    public ResponseEntity<Holiday> holiday(@RequestBody Holiday holiday) {
        Holiday holiday1 = holidayService.createHoliday(holiday);
        return new ResponseEntity<>(holiday1, HttpStatus.CREATED);

    }
    @GetMapping("/{year}/{month}")
    public ResponseEntity<List<Holiday>> getMonthlyHolidays(
            @PathVariable int year,
            @PathVariable int month
    ) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return ResponseEntity.ok(holidayService.getHolidays(start, end));
    }
}
