package com.example.SchedulingPro.dto;

import com.example.SchedulingPro.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
public class UserRequestDto {

    private String username;


    private String name;

    private String role;

    private String category;

    protected String categoryId;

    @Builder
    public UserRequestDto(String username, String name, String role, String category, String categoryId) {
        this.username = username;
        this.name = name;
        this.role = role;
        this.category = category;
        this.categoryId = categoryId;
    }

    public User toEntity() {
        return User.builder()
                .username(username)
                .name(name)
                .category(category)
                .categoryId(categoryId)
                .role(role)
                .build();
    }
}
