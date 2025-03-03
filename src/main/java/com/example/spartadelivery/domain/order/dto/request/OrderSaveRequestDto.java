package com.example.spartadelivery.domain.order.dto.request;

import lombok.Getter;

@Getter
public class OrderSaveRequestDto {
    private Long storeId;
    private String menuName;
    private int price;
}
