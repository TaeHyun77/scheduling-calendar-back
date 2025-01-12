package com.example.SchedulingPro.service;

import com.example.SchedulingPro.exception.CustomException;
import com.example.SchedulingPro.jwt.JwtUtil;
import com.example.SchedulingPro.repository.ScheduleRepository;
import com.example.SchedulingPro.entity.Schedule;
import com.example.SchedulingPro.dto.ScheduleDto;
import com.example.SchedulingPro.entity.User;
import com.example.SchedulingPro.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.SchedulingPro.exception.ErrorCode;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ScheduleService {

    private final JwtUtil jwtUtil;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public void writeSchedule(ScheduleDto scheduleDto, HttpServletRequest request) {

        String accessToken = getAccessTokenFromCookies(request);

        String username = jwtUtil.getUsername(accessToken);

        User user;

        try {
            user = userRepository.findByUsername(username);
        } catch (Exception e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.FAIL_TO_FIND_USER);
        }

        Schedule schedule = scheduleDto.toSchedule();
        schedule.setUser(user);

        scheduleRepository.save(schedule);
    }

    @Transactional
    public void scheduleModify(Long eventId, ScheduleDto scheduleDto) {

        Schedule schedule = scheduleRepository.findById(eventId).
                orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.FAIL_TO_FIND_SCHEDULE));

        schedule.updateSchedule(scheduleDto.getTitle(), scheduleDto.getPlace(), scheduleDto.getStart(), scheduleDto.getEnd(), scheduleDto.getContent());

        scheduleRepository.save(schedule);
    }

    @Transactional
    public void scheduleDelete(Long eventId) {

        Schedule schedule = scheduleRepository.findById(eventId).
                orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.FAIL_TO_FIND_SCHEDULE));

        try {
            scheduleRepository.delete(schedule);
        } catch (Exception e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.FAIL_TO_DELETE_SCHEDULE);
        }
    }

    public Schedule getSchedule(Long eventId) {

        System.out.println(scheduleRepository.findById(eventId));

        return scheduleRepository.findById(eventId).
                orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.FAIL_TO_FIND_SCHEDULE));
    }

    public List<Schedule> allSchedule(HttpServletRequest request) {
        String accessToken = getAccessTokenFromCookies(request);

        String username = jwtUtil.getUsername(accessToken);
        User user;

        try {
            user = userRepository.findByUsername(username);
        } catch (Exception e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.FAIL_TO_FIND_USER);
        }

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
