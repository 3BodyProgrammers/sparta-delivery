package com.example.spartadelivery.domain.order.dto.response;

import com.example.spartadelivery.domain.order.entity.Order;
import com.example.spartadelivery.domain.order.enums.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderSaveResponseDto {

    private final Long id;
    private final Long storeId;
    private final String menuName;
    private final Integer price;
    private final OrderStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public OrderSaveResponseDto(Long id, Long storeId, String menuName, Integer price, OrderStatus status, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.storeId = storeId;
        this.menuName = menuName;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static OrderSaveResponseDto of(Order order) {
        return new OrderSaveResponseDto(
                order.getId(),
                order.getStore().getId(),
                order.getMenuName(),
                order.getPrice(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getModifiedAt()
        );
    }
}
