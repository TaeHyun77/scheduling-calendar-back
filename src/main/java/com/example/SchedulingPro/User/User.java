package com.example.SchedulingPro.User;

import com.example.SchedulingPro.Entity.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class) // Auditing 기능 활성화
@Entity
@Table(name = "user")
public class User extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String name;

    private String role;

    private String category; // 어떤 소셜인지

    protected String categoryId;

    private String email;

    @Builder
    public User(String username, String name, String role, String category, String categoryId) {
        this.username = username;
        this.name = name;
        this.role = role;
        this.category = category;
        this.categoryId = categoryId;
    }
}
