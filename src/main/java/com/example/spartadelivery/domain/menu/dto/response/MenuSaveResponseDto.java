package com.example.spartadelivery.domain.menu.dto.response;

import com.example.spartadelivery.domain.menu.entity.Menu;
import com.example.spartadelivery.domain.store.dto.response.StoreResponseDto;
import com.example.spartadelivery.domain.store.entity.Store;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MenuSaveResponseDto {

    private final Long id;
    private final String name;
    private final Integer price;
    private final String storeName;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    private MenuSaveResponseDto(Long id, String name, Integer price, String storeName, LocalDateTime createdAt,
                                LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.storeName = storeName;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static MenuSaveResponseDto of(Menu menu, String storeName) {
        return new MenuSaveResponseDto(menu.getId(), menu.getName(), menu.getPrice(), storeName,
                menu.getCreatedAt(), menu.getModifiedAt());
    }
}
