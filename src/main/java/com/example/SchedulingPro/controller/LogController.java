package com.example.SchedulingPro.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
public class LogController {

    @Value("${kakao.admin-key}")
    private String kakaoAdminkey;

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
}
