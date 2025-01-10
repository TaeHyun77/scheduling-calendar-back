package com.example.SchedulingPro.controller;

import com.example.SchedulingPro.schedule.Schedule;
import com.example.SchedulingPro.schedule.ScheduleDto;
import com.example.SchedulingPro.service.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/write/schedule")
    public void write_schedule(@RequestBody ScheduleDto scheduleDto, HttpServletRequest request) {

        scheduleService.writeSchedule(scheduleDto, request);

    }

    @GetMapping("/schedule/{eventId}")
    public Schedule getSchedule(@PathVariable("eventId") Long eventId) {

        return scheduleService.getSchedule(eventId);
    }

    @DeleteMapping("/delete/schedule/{eventId}")
    public void deleteSchedule(@PathVariable("eventId") Long eventId) {

        scheduleService.scheduleDelete(eventId);

    }

    @GetMapping("/all/schedule")
    public List<Schedule> getAllSchedules(HttpServletRequest request) {

        return scheduleService.allSchedule(request);

    }
}
