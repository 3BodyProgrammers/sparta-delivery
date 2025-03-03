package com.example.spartadelivery.domain.menu.dto.response;

import com.example.spartadelivery.domain.menu.entity.Menu;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MenuSaveResponseDto {

    private final Long id;
    private final String name;
    private final Integer price;
    //TODO : 이후 가게 Response로 교체
    private final Long storeId;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    private MenuSaveResponseDto(Long id, String name, Integer price, Long storeId, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.storeId = storeId;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static MenuSaveResponseDto of(Menu menu) {
        return new MenuSaveResponseDto(menu.getId(), menu.getName(), menu.getPrice(), menu.getStoreId(), menu.getCreatedAt(), menu.getModifiedAt());
    }
}
