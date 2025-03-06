package com.example.spartadelivery.domain.store.dto.response;

import com.example.spartadelivery.domain.store.entity.Store;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
public class StoreUpdateResponseDto {

    private final Long id;
    private final String name;
    private final LocalTime openAt;
    private final LocalTime closeAt;
    private final Integer minimumPrice;
    private final List<String> holidays;
    private final String notice;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    private StoreUpdateResponseDto(Long id, String name, LocalTime openAt, LocalTime closeAt, Integer minimumPrice, List<String> holidays, String notice, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.minimumPrice = minimumPrice;
        this.holidays = holidays;
        this.notice = notice;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static StoreUpdateResponseDto of(Store store, List<String> holidays) {
        return new StoreUpdateResponseDto(store.getId(), store.getName(), store.getOpenAt(), store.getCloseAt(), store.getMinimumPrice(), holidays, store.getNotice(), store.getCreatedAt(), LocalDateTime.now());
    }
}
