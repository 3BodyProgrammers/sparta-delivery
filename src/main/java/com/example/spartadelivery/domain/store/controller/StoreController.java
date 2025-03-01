package com.example.spartadelivery.domain.store.controller;

import com.example.spartadelivery.domain.store.dto.request.StoreSaveRequestDto;
import com.example.spartadelivery.domain.store.dto.request.StoreUpdateRequestDto;
import com.example.spartadelivery.domain.store.dto.response.StoreDetailResponseDto;
import com.example.spartadelivery.domain.store.dto.response.StoreResponseDto;
import com.example.spartadelivery.domain.store.dto.response.StoreSaveResponseDto;
import com.example.spartadelivery.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    //Todo : 이후 유저 구현시 AuthUser로 변환
    @PostMapping("/stores")
    public ResponseEntity<StoreSaveResponseDto> saveStore(Long userId, String userRole, @RequestBody StoreSaveRequestDto request) {
        StoreSaveResponseDto response = storeService.saveStore(userId, userRole, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stores")
    public ResponseEntity<Page<StoreResponseDto>> getStores(@RequestParam(required = false) String name, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(storeService.getStores(name, page, size));
    }

    @GetMapping("/stores/{id}")
    public ResponseEntity<StoreDetailResponseDto> getStore(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.getStore(id));
    }

    //Todo : 이후 유저 구현시 AuthUser로 변환
    @PutMapping("/stores/{id}")
    public ResponseEntity<StoreResponseDto> updaeteStore(@PathVariable Long id, Long userId, String userRole, @RequestBody StoreUpdateRequestDto request) {
        StoreResponseDto response = storeService.updateStore(id, userId, userRole, request);
        return ResponseEntity.ok(response);
    }
}
