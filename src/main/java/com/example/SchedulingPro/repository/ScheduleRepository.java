package com.example.SchedulingPro.repository;

import com.example.SchedulingPro.schedule.Schedule;
import com.example.SchedulingPro.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByUserId(Long userId);
}
