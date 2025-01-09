package com.example.SchedulingPro.repository;

import com.example.SchedulingPro.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
