package com.example.SchedulingPro.service;

import com.example.SchedulingPro.jwt.JwtUtil;
import com.example.SchedulingPro.repository.ScheduleRepository;
import com.example.SchedulingPro.schedule.Schedule;
import com.example.SchedulingPro.schedule.ScheduleDto;
import com.example.SchedulingPro.user.User;
import com.example.SchedulingPro.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final JwtUtil jwtUtil;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public void writeSchedule(ScheduleDto scheduleDto, HttpServletRequest request) {

        String accessToken = getAccessTokenFromCookies(request);

        String username = jwtUtil.getUsername(accessToken);
        User user = userRepository.findByUsername(username);

        Schedule schedule = scheduleDto.toSchedule();
        schedule.setUser(user);

        System.out.println("Saving schedule: " + schedule.getContent());

        scheduleRepository.save(schedule);
    }

    public List<Schedule> allSchedule(HttpServletRequest request) {
        String accessToken = getAccessTokenFromCookies(request);

        String username = jwtUtil.getUsername(accessToken);
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new IllegalArgumentException("유효 하지 않은 사용자입니다.");
        }

        System.out.println(scheduleRepository.findByUserId(user.getId()));

        return scheduleRepository.findByUserId(user.getId());
    }

    public String getAccessTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
