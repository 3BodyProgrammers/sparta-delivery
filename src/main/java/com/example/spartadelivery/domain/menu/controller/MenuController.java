package com.example.spartadelivery.domain.menu.controller;

import com.example.spartadelivery.domain.menu.dto.request.MenuSaveRequestDto;
import com.example.spartadelivery.domain.menu.dto.request.MenuUpdateRequestDto;
import com.example.spartadelivery.domain.menu.dto.response.MenuSaveResponseDto;
import com.example.spartadelivery.domain.menu.dto.response.MenuUpdateResponseDto;
import com.example.spartadelivery.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PostMapping("/stores/{storeId}/menus")
    public ResponseEntity<MenuSaveResponseDto> saveMenu(@PathVariable Long storeId, Long userId, String userRole, @RequestBody MenuSaveRequestDto request) {
        MenuSaveResponseDto response = menuService.saveMenu(storeId, userId, userRole, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/stores/{storeId}/menus/{id}")
    public ResponseEntity<MenuUpdateResponseDto> updateMenu(@PathVariable Long storeId, @PathVariable Long id, Long userId, String userRole, @RequestBody MenuUpdateRequestDto request) {
        MenuUpdateResponseDto response = menuService.updateMenu(id, storeId, userId, userRole, request);
        return ResponseEntity.ok(response);
    }

}
