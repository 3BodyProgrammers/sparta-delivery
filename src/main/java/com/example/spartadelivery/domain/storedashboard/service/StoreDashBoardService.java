package com.example.spartadelivery.domain.storedashboard.service;

import com.example.spartadelivery.domain.storedashboard.dto.StoreDashBoardDayResponseDto;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreDashBoardService {
    public StoreDashBoardDayResponseDto getDailyStat(Long id, String day) {

    }

    public StoreDashBoardDayResponseDto getPeriodStat(Long id, String startDay, String endDay) {

    }
}
