package com.example.SchedulingPro.entity;

import com.example.SchedulingPro.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String place;


    private String start;
    private String end;
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Schedule(String title, String place, String start, String end, String content) {
        this.title = title;
        this.place = place;
        this.start = start;
        this.end = end;
        this.content = content;
    }

    public void updateSchedule(String title, String place, String start, String end, String content) {
        this.title = title;
        this.place = place;
        this.start = start;
        this.end = end;
        this.content = content;
    }
}
