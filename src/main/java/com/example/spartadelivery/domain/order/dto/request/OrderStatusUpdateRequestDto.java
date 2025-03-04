package com.example.spartadelivery.domain.order.dto.request;

import com.example.spartadelivery.domain.order.enums.OrderStatus;
import lombok.Getter;

@Getter
public class OrderStatusUpdateRequestDto {
    private OrderStatus newStatus;
}
