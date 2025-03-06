package com.example.spartadelivery.domain.menu.dto.response;

import com.example.spartadelivery.domain.menu.entity.Menu;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MenuUpdateResponseDto {

    private final Long id;
    private final String name;
    private final Integer price;
    private final String storeName;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    private MenuUpdateResponseDto(Long id, String name, Integer price, String storeName, LocalDateTime createdAt,
                                  LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.storeName = storeName;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static MenuUpdateResponseDto of(Menu menu, String storeName) {
        return new MenuUpdateResponseDto(menu.getId(), menu.getName(), menu.getPrice(), storeName,
                menu.getCreatedAt(), LocalDateTime.now());
    }
}
