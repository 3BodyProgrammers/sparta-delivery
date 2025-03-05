package com.example.spartadelivery.domain.storedashboard.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class StoreDashBoardPeriodResponseDto {

    private final LocalDateTime startDay;
    private final LocalDateTime endDay;
    private final Integer totalUser;
    private final Integer totalSales;
    private final List<StoreDashBoardDayResponseDto> dailyStats;

    public StoreDashBoardPeriodResponseDto(LocalDateTime startDay, LocalDateTime endDay, Integer totalUser,
                                           Integer totalSales, List<StoreDashBoardDayResponseDto> dailyStats) {
        this.startDay = startDay;
        this.endDay = endDay;
        this.totalUser = totalUser;
        this.totalSales = totalSales;
        this.dailyStats = dailyStats;
    }


    public static StoreDashBoardPeriodResponseDto of(LocalDateTime startDay, LocalDateTime endDay, Integer totalUser,
                                                     Integer totalSales,
                                                     List<StoreDashBoardDayResponseDto> dailyStats) {
        return new StoreDashBoardPeriodResponseDto(startDay, endDay, totalUser, totalSales, dailyStats);
    }

}
