package com.example.SchedulingPro.controller;

import com.example.SchedulingPro.jwt.JwtUtil;
import com.example.SchedulingPro.user.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    private final JwtUtil jwtUtil;

    @GetMapping("/info")
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
