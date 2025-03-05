package com.example.spartadelivery.domain.store.dto.response;

import lombok.Getter;

@Getter
public class StoreDeleteResponseDto {

    private final String message;

    private StoreDeleteResponseDto(String message) {
        this.message = message;
    }

    public static StoreDeleteResponseDto of(String message) {
        return new StoreDeleteResponseDto(message);
    }
}
