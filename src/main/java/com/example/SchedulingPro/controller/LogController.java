package com.example.SchedulingPro.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Slf4j
@RestController
public class LogController {

    @Value("${kakao.admin-key}")
    private String kakaoAdminkey;

    @Value("${naver.client-id}")
    private String naver_clientId;

    @Value("${naver.secretKey}")
    private String naver_secretKey;

    // kakaoDev 공식 문서에서 logout 과정 참조한 것
    @PostMapping("/kakaoLogout/{username}")
    public ResponseEntity<?> kakaoLogout(@PathVariable("username") String username) {

        String targetIdType = "user_id";
        String targetId = username.split("_")[1];
        System.out.println("targetId" + targetId);

        RestTemplate restTemplate = new RestTemplate();

        // HTTP 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("Authorization", "KakaoAK " + kakaoAdminkey);

        // HTTP 요청 본문 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("target_id", targetId); // 로그아웃 대상 사용자 ID
        params.add("target_id_type", targetIdType); // ID 타입 ("user_id" 고정임)

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        String kakaoLogoutUrl = "https://kapi.kakao.com/v1/user/logout"; // 로그아웃 요청 url

        ResponseEntity<String> response = restTemplate.exchange(
                kakaoLogoutUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        return response;
    }

    // 구글은 /logout 경로로 세션 만료시키는 방식
    @PostMapping("/googleLogout")
    public ResponseEntity<String> googleLogout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok("로그아웃 성공");
    }

    // 네이버에서는 로그아웃 api를 따로 지원하지 않기에 아래와 같은 형식으로 access 토큰을 제거하는 방식을 따라야 함
    // access 토큰의 경우 특수 문자가 들어가므로 URLEncoder 해줘야 한다고 함
    @PostMapping("naverLogout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>("UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
        }

        String accessToken = authorizationHeader.substring(7);
        String encodedAccessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);

        String logoutUrl = String.format(
                "https://nid.naver.com/oauth2.0/token?grant_type=delete&client_id=%s&client_secret=%s&access_token=%s&service_provider=NAVER",
                naver_clientId,
                naver_secretKey,
                encodedAccessToken
        );

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Object> response = restTemplate.exchange(logoutUrl, HttpMethod.POST, null, Object.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok("Logout successful");
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("네이버 로그아웃 실패");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("네이버 로그아웃 중 에러 발생");
        }
    }
}
