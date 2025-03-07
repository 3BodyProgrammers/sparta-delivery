package com.example.spartadelivery.domain.menu.controller;

import com.example.spartadelivery.common.annotation.Auth;
import com.example.spartadelivery.common.annotation.Owner;
import com.example.spartadelivery.common.dto.AuthUser;
import com.example.spartadelivery.domain.menu.dto.request.MenuSaveRequestDto;
import com.example.spartadelivery.domain.menu.dto.request.MenuUpdateRequestDto;
import com.example.spartadelivery.domain.menu.dto.response.MenuDeleteResponseDto;
import com.example.spartadelivery.domain.menu.dto.response.MenuSaveResponseDto;
import com.example.spartadelivery.domain.menu.dto.response.MenuUpdateResponseDto;
import com.example.spartadelivery.domain.menu.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stores/{storeId}")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @Owner
    @PostMapping("/menus")
    public ResponseEntity<MenuSaveResponseDto> saveMenu(@PathVariable Long storeId, @Auth AuthUser authUser, @Valid @RequestBody MenuSaveRequestDto request) {
        MenuSaveResponseDto response = menuService.saveMenu(storeId, authUser, request);
        return ResponseEntity.ok(response);
    }

    @Owner
    @PutMapping("/menus/{id}")
    public ResponseEntity<MenuUpdateResponseDto> updateMenu(@PathVariable Long storeId, @PathVariable Long id, @Auth AuthUser authUser, @Valid @RequestBody MenuUpdateRequestDto request) {
        MenuUpdateResponseDto response = menuService.updateMenu(id, storeId, authUser, request);
        return ResponseEntity.ok(response);
    }

    @Owner
    @PostMapping("/menus/{id}")
    public ResponseEntity<MenuDeleteResponseDto> deleteMenu(@PathVariable Long storeId, @PathVariable Long id, @Auth AuthUser authUser) {
        MenuDeleteResponseDto response = menuService.deleteMenu(storeId, id, authUser);
        return ResponseEntity.ok(response);
    }


}
