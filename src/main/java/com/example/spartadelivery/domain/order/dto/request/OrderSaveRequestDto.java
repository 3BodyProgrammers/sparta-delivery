package com.example.spartadelivery.domain.order.dto.request;

import com.example.spartadelivery.domain.order.enums.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class OrderSaveRequestDto {
    private Long storeId;

    @NotEmpty(message = "필수 입력 값 입니다.")
    private String menuName;

    private int price;

}
