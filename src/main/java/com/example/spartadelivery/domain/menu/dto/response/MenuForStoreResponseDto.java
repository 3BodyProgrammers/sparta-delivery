package com.example.spartadelivery.domain.menu.dto.response;

import com.example.spartadelivery.domain.menu.entity.Menu;
import lombok.Getter;

@Getter
public class MenuForStoreResponseDto {

    private final Long id;
    private final String name;
    private final Integer price;

    private MenuForStoreResponseDto(Long id, String name, Integer price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public static MenuForStoreResponseDto of(Menu menu) {
        return new MenuForStoreResponseDto(menu.getId(), menu.getName(), menu.getPrice());
    }
}
