package com.example.SchedulingPro.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserResponseDto {
    private String username;
    private String role;
    private String name;
    private String email;

    public static UserResponseDto of(String username, String role, String name, String email) {
        return UserResponseDto.builder()
                .username(username)
                .role(role)
                .name(name)
                .email(email)
                .build();
    }

}


