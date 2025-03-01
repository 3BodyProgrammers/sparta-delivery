package com.example.spartadelivery.domain.store.controller;

import com.example.spartadelivery.domain.store.dto.request.StoreSaveRequestDto;
import com.example.spartadelivery.domain.store.dto.response.StoreSaveResponseDto;
import com.example.spartadelivery.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    //Todo : 이후 유저 구현시 AuthUser로 변환
    @PostMapping("/stores")
    public ResponseEntity<StoreSaveResponseDto> save(Long userId, String userRole, StoreSaveRequestDto request) {
        StoreSaveResponseDto response = storeService.save(userId, userRole, request);
        return ResponseEntity.ok(response);
    }
}
