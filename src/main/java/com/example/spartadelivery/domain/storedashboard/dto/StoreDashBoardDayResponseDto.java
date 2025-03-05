package com.example.spartadelivery.domain.storedashboard.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class StoreDashBoardDayResponseDto {

    private final LocalDateTime day;
    private final Integer dailyUser;
    private final Integer dailySales;

    private StoreDashBoardDayResponseDto(LocalDateTime day, Integer dailyUser, Integer dailySales) {
        this.day = day;
        this.dailyUser = dailyUser;
        this.dailySales = dailySales;
    }

    public static StoreDashBoardDayResponseDto of(LocalDateTime day, Integer dailyUser, Integer dailySales) {
        return new StoreDashBoardDayResponseDto(day, dailyUser, dailySales);
    }
}
