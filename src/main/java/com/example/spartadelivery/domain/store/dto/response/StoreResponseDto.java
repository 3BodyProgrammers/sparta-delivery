package com.example.spartadelivery.domain.store.dto.response;

import com.example.spartadelivery.domain.holiday.enums.Holiday;
import com.example.spartadelivery.domain.store.entity.Store;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
public class StoreResponseDto {
//TODO : 이후 휴일 구현 시 휴일 추가

    private final Long id;
    private final String name;
    private final LocalTime openAt;
    private final LocalTime closeAt;
    private final Integer minimumPrice;
    private final List<Holiday> holidays;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    private StoreResponseDto(Long id, String name, LocalTime openAt, LocalTime closeAt, Integer minimumPrice, List<Holiday> holidays, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.minimumPrice = minimumPrice;
        this.holidays = holidays;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static StoreResponseDto of(Store store, List<Holiday> holidays) {
        return new StoreResponseDto(store.getId(), store.getName(), store.getOpenAt(), store.getCloseAt(), store.getMinimumPrice(), holidays, store.getCreatedAt(), store.getModifiedAt());
    }
}
