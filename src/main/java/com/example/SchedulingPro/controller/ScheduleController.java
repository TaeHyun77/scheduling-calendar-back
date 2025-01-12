package com.example.SchedulingPro.controller;

import com.example.SchedulingPro.entity.Schedule;
import com.example.SchedulingPro.dto.ScheduleDto;
import com.example.SchedulingPro.service.ScheduleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/modify/schedule/{eventId}")
    public void modifySchedule(@PathVariable("eventId") Long eventId, @RequestBody ScheduleDto scheduleDto) {
        scheduleService.scheduleModify(eventId, scheduleDto);
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
