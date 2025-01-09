package com.example.SchedulingPro.controller;

import com.example.SchedulingPro.schedule.Schedule;
import com.example.SchedulingPro.schedule.ScheduleDto;
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

    @GetMapping("/all/schedule")
    public List<Schedule> getAllSchedules(HttpServletRequest request) {

        return scheduleService.allSchedule(request);

    }
}
