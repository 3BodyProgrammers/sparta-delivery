package com.example.spartadelivery.domain.storedashboard.controller;

import com.example.spartadelivery.common.annotation.Auth;
import com.example.spartadelivery.common.dto.AuthUser;
import com.example.spartadelivery.domain.storedashboard.dto.StoreDashBoardDayResponseDto;
import com.example.spartadelivery.domain.storedashboard.dto.StoreDashBoardPeriodResponseDto;
import com.example.spartadelivery.domain.storedashboard.service.StoreDashBoardService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class StoreDashBoardController {

    private final StoreDashBoardService storeDashBoardService;

    @GetMapping("/stores/{id}/dashboard/daily")
    public ResponseEntity<StoreDashBoardDayResponseDto> getDailyStat(@PathVariable Long id,
                                                                     @Auth AuthUser authUser,
                                                                     @RequestParam @NotBlank(message = "일은 필수 값입니다.") String day) {
        StoreDashBoardDayResponseDto response = storeDashBoardService.getDailyStat(id, authUser, day);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stores/{id}/dashboard/period")
    public ResponseEntity<StoreDashBoardPeriodResponseDto> getDailyStat(@PathVariable Long id,
                                                                     @Auth AuthUser authUser,
                                                                     @RequestParam @NotBlank(message = "시작일은 필수 값입니다.") String startDay,
                                                                     @RequestParam @NotBlank(message = "종료일은 필수 값입니다.") String endDay) {
        StoreDashBoardPeriodResponseDto response = storeDashBoardService.getPeriodStat(id, authUser, startDay, endDay);
        return ResponseEntity.ok(response);
    }
}
