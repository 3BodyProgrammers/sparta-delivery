package com.example.spartadelivery.domain.menu.controller;

import com.example.spartadelivery.domain.menu.dto.request.MenuSaveRequestDto;
import com.example.spartadelivery.domain.menu.dto.response.MenuSaveResponseDto;
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
    public ResponseEntity<MenuSaveResponseDto> saveMenus(@PathVariable Long storeId, Long userId, String userRole, @RequestBody MenuSaveRequestDto request){
        MenuSaveResponseDto response = menuService.saveMenus(storeId, userId, userRole, request);
        return ResponseEntity.ok(response);
    }

}
