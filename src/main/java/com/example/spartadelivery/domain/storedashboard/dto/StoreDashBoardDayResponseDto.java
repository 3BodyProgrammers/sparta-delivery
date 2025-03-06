package com.example.spartadelivery.domain.storedashboard.dto;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class StoreDashBoardDayResponseDto {

    private final LocalDate day;
    private final Integer dailyUser;
    private final Integer dailySales;

    private StoreDashBoardDayResponseDto(LocalDate day, Integer dailyUser, Integer dailySales) {
        this.day = day;
        this.dailyUser = dailyUser;
        this.dailySales = dailySales;
    }

    public static StoreDashBoardDayResponseDto of(LocalDate day, Integer dailyUser, Integer dailySales) {
        return new StoreDashBoardDayResponseDto(day, dailyUser, dailySales);
    }
}
