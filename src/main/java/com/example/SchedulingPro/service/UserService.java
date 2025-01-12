package com.example.SchedulingPro.service;

import com.example.SchedulingPro.dto.UserResponseDto;
import com.example.SchedulingPro.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Getter
@RequiredArgsConstructor
@Service
public class UserService {

    private final JwtUtil jwtUtil;

    public ResponseEntity<?> userInfo(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        }

        String token = authorizationHeader.substring(7);

        String username = jwtUtil.getUsername(token);
        String name = jwtUtil.getName(token);
        String role = jwtUtil.getRole(token);
        String email = jwtUtil.getEmail(token);

        UserResponseDto info = UserResponseDto.of(username, role, name, email);

        return new ResponseEntity<>(info, HttpStatus.OK);
    }
}
