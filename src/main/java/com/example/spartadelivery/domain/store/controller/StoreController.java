package com.example.spartadelivery.domain.store.controller;

import com.example.spartadelivery.common.annotation.Auth;
import com.example.spartadelivery.common.annotation.Owner;
import com.example.spartadelivery.common.dto.AuthUser;
import com.example.spartadelivery.domain.store.dto.request.StoreSaveRequestDto;
import com.example.spartadelivery.domain.store.dto.request.StoreUpdateRequestDto;
import com.example.spartadelivery.domain.store.dto.response.*;
import com.example.spartadelivery.domain.store.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @Owner
    @PostMapping
    public ResponseEntity<StoreSaveResponseDto> saveStore(@Auth AuthUser authUser,
                                                          @Valid @RequestBody StoreSaveRequestDto request) {
        StoreSaveResponseDto response = storeService.saveStore(authUser, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<StoreResponseDto>> getStores(@RequestParam(required = false) String name,
                                                            @RequestParam(defaultValue = "1") Integer page,
                                                            @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(storeService.getStores(name, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDetailResponseDto> getStore(@PathVariable Long id) {
        return ResponseEntity.ok(storeService.getStore(id));
    }

    @Owner
    @PutMapping("/{id}")
    public ResponseEntity<StoreUpdateResponseDto> updateStore(@PathVariable Long id, @Auth AuthUser authUser,
                                                              @Valid @RequestBody StoreUpdateRequestDto request) {
        StoreUpdateResponseDto response = storeService.updateStore(id, authUser, request);
        return ResponseEntity.ok(response);
    }

    @Owner
    @PostMapping("/delete/{id}")
    public ResponseEntity<StoreDeleteResponseDto> deleteStore(@PathVariable Long id, @Auth AuthUser authUser) {
        StoreDeleteResponseDto response = storeService.deleteStore(id, authUser);
        return ResponseEntity.ok(response);
    }
}
