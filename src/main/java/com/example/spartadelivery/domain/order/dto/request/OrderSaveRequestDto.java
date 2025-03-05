package com.example.spartadelivery.domain.order.dto.request;

import com.example.spartadelivery.domain.order.enums.OrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSaveRequestDto {
    private Long storeId;

    @NotEmpty(message = "필수 입력 값 입니다.")
    private String menuName;

    @NotNull(message = "필수 입력 값 입니다.")
    @Positive(message = "가격은 0보다 커야 합니다.")
    private int price;
}
