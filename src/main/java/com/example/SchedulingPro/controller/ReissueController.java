package com.example.SchedulingPro.controller;

import com.example.SchedulingPro.entity.Refresh;
import com.example.SchedulingPro.exception.CustomException;
import com.example.SchedulingPro.exception.ErrorCode;
import com.example.SchedulingPro.jwt.JwtUtil;
import com.example.SchedulingPro.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.util.ToStringUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
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
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.FAIL_TO_FIND_COOKIES);
        } else {
            System.out.println("refresh 쿠키 존재");
        }

        for (Cookie c : cookies) {
            if ("refreshToken".equals(c.getName())) {
                refresh = c.getValue();
                log.info("Received refresh token: " + refresh);
                break;
            }
        }

        if (refresh == null) {
            throw new CustomException(HttpStatus.BAD_REQUEST, ErrorCode.FAIL_TO_FIND_COOKIES);
        } else {
            System.out.println(refresh);
        }

        System.out.println("reissueController");

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            log.info("access token expired");
            return new ResponseEntity<>("access token expired", HttpStatus.BAD_REQUEST);
        }

        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {
            log.info("is not refresh token");
            return new ResponseEntity<>("is not refresh token", HttpStatus.BAD_REQUEST);
        }

        Boolean isExist = refreshRepository.existsByRefresh(refresh);

        if (isExist) {
            log.info("not exist refresh token");
            return new ResponseEntity<>("not exist refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
        String name = jwtUtil.getName(refresh);

        String newAccess = jwtUtil.createJwt("access", username, name, 3600000L); // 1시간
        String newRefresh = jwtUtil.createJwt("refresh", username, name,86400000L); // 24시간

        log.info("newAccess :" + newAccess);

        refreshRepository.deleteByRefresh(refresh);

        addRefreshEntity(username, newRefresh, 86400000L); // 24시간

        response.setHeader("Authorization", newAccess);
        response.addCookie(createCookie("refreshToken", newRefresh));

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("Authorization", newAccess);
        responseBody.put("refreshToken", newRefresh);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
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

        return cookie;
    }
}
