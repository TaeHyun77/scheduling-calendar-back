package com.example.SchedulingPro.dto;

import com.example.SchedulingPro.entity.Schedule;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ScheduleDto {

    private String title;
    private String place;
    private String start;
    private String end;
    private String content;

    @Builder
    public ScheduleDto(String title, String place, String start, String end, String content) {
        this.title = title;
        this.place = place;
        this.start = start;
        this.end = end;
        this.content = content;
    }

    public Schedule toSchedule() {
        return Schedule.builder()
                .title(title)
                .place(place)
                .start(start)
                .end(end)
                .content(content)
                .build();
    }
}
