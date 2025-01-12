package com.example.SchedulingPro.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    UNKNOWN("000_UNKNOWN", "알 수 없는 에러가 발생했습니다."),

    FAIL_TO_FIND_USER("FAIL_TO_FIND_USER", "유저 조회 실패"),

    FAIL_TO_FIND_SCHEDULE("FAIL_TO_FIND_SCHEDULE", "일정 조회 실패"),

    FAIL_TO_DELETE_SCHEDULE("FAIL_TO_DELETE_SCHEDULE", "일정 삭제 실패"),

    FAIL_TO_FIND_COOKIES("FAIL_TO_FIND_COOKIES", "쿠키를 조회할 수 없습니다.");

    private final String errorCode;

    private final String message;
}
