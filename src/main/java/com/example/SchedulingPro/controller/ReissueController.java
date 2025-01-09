package com.example.SchedulingPro.controller;

import com.example.SchedulingPro.entity.Refresh;
import com.example.SchedulingPro.jwt.JwtUtil;
import com.example.SchedulingPro.entity.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    /*
        refresh 토큰으로 access 토큰을 재발급 받기 위한 Controller임

        refresh 토큰으로 access 토큰을 재발급하고 refresh 토큰도 같이 재발급
        여기서, 재발급 받기 전 refresh 토큰을 사용 금지하게 처리해야 함
    */

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return new ResponseEntity<>("No cookies found", HttpStatus.BAD_REQUEST);
        }

        for (Cookie c : cookies) {
            if ("refreshToken".equals(c.getName())) {
                refresh = c.getValue();
                break;
            }
        }

        if (refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        System.out.println("Refresh Token: " + refresh);

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("access token expired", HttpStatus.BAD_REQUEST);
        }

        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        Boolean isExist = refreshRepository.existsByRefresh(refresh);

        if (!isExist) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
        String name = jwtUtil.getName(refresh);
        String email = jwtUtil.getEmail(refresh);
        String role = jwtUtil.getRole(refresh);

        String newAccess = jwtUtil.createJwt("access", username, name, 60*60*60L);
        String newRefresh = jwtUtil.createJwt("refresh", username, name,60*60*60*10L);

        refreshRepository.deleteByRefresh(refresh);

        addRefreshEntity(username, newRefresh, 60*60*60*10L);

        response.setHeader("Authorization", newAccess);
        response.addCookie(createCookie("refreshToken", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public void addRefreshEntity(String username, String refresh, Long expired) {
        Date date = new Date(System.currentTimeMillis() + expired);

        Refresh refreshEntity = Refresh.builder()
                .username(username)
                .refresh(refresh)
                .expiration(date.toString())
                .build();

        refreshRepository.save(refreshEntity);
    }

    public Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setSecure(true);

        return cookie;
    }
}
