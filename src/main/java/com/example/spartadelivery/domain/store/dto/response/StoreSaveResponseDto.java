package com.example.spartadelivery.domain.store.dto.response;

import com.example.spartadelivery.domain.store.entity.Store;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class StoreSaveResponseDto {
//TODO : 이후 휴일 구현 시 휴일 추가

    private final Long id;
    private final String name;
    private final LocalTime openAt;
    private final LocalTime closeAt;
    private final Integer minimumPrice;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    private StoreSaveResponseDto(Long id, String name, LocalTime openAt, LocalTime closeAt, Integer minimumPrice, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.minimumPrice = minimumPrice;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static StoreSaveResponseDto of(Store store) {
        return new StoreSaveResponseDto(store.getId(), store.getName(), store.getOpenAt(), store.getCloseAt(), store.getMinimumPrice(), store.getCreatedAt(), store.getModifiedAt());
    }
}
