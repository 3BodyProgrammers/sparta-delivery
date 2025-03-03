package com.example.spartadelivery.domain.order.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderSaveResponseDto {

    private final Long id;
    private final Long storeId;
    private final String menuName;
    private final Integer price;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public OrderSaveResponseDto(Long id, Long storeId, String menuName, Integer price, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.storeId = storeId;
        this.menuName = menuName;
        this.price = price;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
