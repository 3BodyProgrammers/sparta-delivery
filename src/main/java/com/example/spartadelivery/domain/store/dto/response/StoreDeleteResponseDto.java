package com.example.spartadelivery.domain.store.dto.response;

import com.example.spartadelivery.domain.store.entity.Store;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
