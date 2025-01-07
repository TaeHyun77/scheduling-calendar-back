package com.example.SchedulingPro.Details;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class GoogleUserDetails implements OAuth2UserInfo {

    private Map<String, Object> attributes; // 구글에서 받아온 정보 값

    @Override
    public String getCategory() {
        return "google";
    }

    @Override
    public String getCategoryId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}
