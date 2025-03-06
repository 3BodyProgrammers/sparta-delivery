package com.example.spartadelivery.domain.store.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreUpdateRequestDto {

    @NotEmpty(message = "가게 이름은 필수 값 입니다.")
    @Size(max = 50, message = "가게 이름은 최대 50자 입니다.")
    private String name;

    @NotEmpty(message = "오픈 시간은 필수 값 입니다.")
    @DateTimeFormat
    private String openAt;

    @NotEmpty(message = "마감 시간은 필수 값 입니다.")
    @DateTimeFormat
    private String closeAt;

    @NotNull(message = "최소 주문 금액은 필수 값 입니다.")
    private Integer minimumPrice;

    @NotEmpty(message = "가게 공지는 필수 값 입니다.")
    private String notice;

}
