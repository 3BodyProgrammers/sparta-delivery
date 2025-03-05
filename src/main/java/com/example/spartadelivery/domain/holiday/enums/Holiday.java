package com.example.spartadelivery.domain.holiday.enums;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.user.enums.UserRole;
import java.util.Arrays;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum Holiday {
    MONDAY("월", 1),
    TUESDAY("화", 2),
    WEDNESDAY("수", 4),
    THURSDAY("목", 8),
    FRIDAY("금", 16),
    SATURDAY("토", 32),
    SUNDAY("일", 64);

    private final String day;
    private final Integer value;

    Holiday(String day, Integer value) {
        this.day = day;
        this.value = value;
    }
}