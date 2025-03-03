package com.example.spartadelivery.domain.menu.dto.response;

import lombok.Getter;

@Getter
public class MenuDeleteResponseDto {

    private String message;

    private MenuDeleteResponseDto(String message) {
        this.message = message;
    }

    public static MenuDeleteResponseDto of(String message) {
        return new MenuDeleteResponseDto(message);
    }
}
