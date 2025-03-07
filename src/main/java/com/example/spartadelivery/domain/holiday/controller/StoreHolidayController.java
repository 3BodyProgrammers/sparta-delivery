package com.example.spartadelivery.domain.holiday.controller;

import com.example.spartadelivery.common.annotation.Auth;
import com.example.spartadelivery.common.annotation.Owner;
import com.example.spartadelivery.common.dto.AuthUser;
import com.example.spartadelivery.domain.holiday.dto.request.StoreHolidayRequestDto;
import com.example.spartadelivery.domain.holiday.service.StoreHolidayService;
import com.example.spartadelivery.domain.store.dto.response.StoreResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StoreHolidayController {

    private final StoreHolidayService storeHolidayService;

    @Owner
    @PostMapping("/stores/{id}/holidays")
    public ResponseEntity<StoreResponseDto> updateHolidays(@PathVariable Long id, @Auth AuthUser authUser, @RequestBody StoreHolidayRequestDto request){
        StoreResponseDto response = storeHolidayService.updateHolidays(id, authUser, request);
        return ResponseEntity.ok(response);
    }

}