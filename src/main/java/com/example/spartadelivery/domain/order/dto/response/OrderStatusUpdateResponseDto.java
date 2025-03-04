package com.example.spartadelivery.domain.order.dto.response;

import com.example.spartadelivery.domain.order.enums.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderStatusUpdateResponseDto {
    private final Long id;
    private final Long storeId;
    private final String menuName;
    private final Integer price;
    private final OrderStatus updatedStatus;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public OrderStatusUpdateResponseDto(Long id, Long storeId, String menuName, Integer price, OrderStatus updatedStatus, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.storeId = storeId;
        this.menuName = menuName;
        this.price = price;
        this.updatedStatus = updatedStatus;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
