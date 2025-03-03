package com.example.spartadelivery.domain.menu.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@NotEmpty
@AllArgsConstructor
public class MenuSaveRequestDto {

    @NotEmpty(message = "메뉴 이름은 필수 값입니다.")
    @Size(max = 50, message = "메뉴 이름은 최대 50자 입니다.")
    private String name;

    @NotEmpty(message = "메뉴 가격은 필수 값입니다.")
    private Integer price;

}
