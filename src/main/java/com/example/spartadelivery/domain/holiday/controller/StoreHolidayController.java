package com.example.spartadelivery.domain.holiday.controller;

import com.example.spartadelivery.domain.holiday.dto.request.StoreHolidayRequestDto;
import com.example.spartadelivery.domain.holiday.service.StoreHolidayService;
import com.example.spartadelivery.domain.store.dto.response.StoreResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StoreHolidayController {

    private final StoreHolidayService storeHolidayService;

    //TODO : 유저 구현 이후 AuthUser 통해 유저 확인
    @PostMapping("/stores/{id}/holidays")
    public ResponseEntity<StoreResponseDto> updateHolidays(@PathVariable Long id, Long userId, String userRole, StoreHolidayRequestDto request){
        request.validate();
        StoreResponseDto response = storeHolidayService.updateHolidays(id, userId, userRole, request);
        return ResponseEntity.ok(response);
    }

}