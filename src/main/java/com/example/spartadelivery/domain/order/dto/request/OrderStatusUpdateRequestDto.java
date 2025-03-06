package com.example.spartadelivery.domain.order.dto.request;

import com.example.spartadelivery.domain.order.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateRequestDto {
    private OrderStatus newStatus;
}
