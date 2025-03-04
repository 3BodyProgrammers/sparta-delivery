package com.example.spartadelivery.domain.store.dto.response;

import com.example.spartadelivery.domain.menu.dto.response.MenuResponseDto;
import com.example.spartadelivery.domain.store.entity.Store;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
public class StoreDetailResponseDto {
//TODO : 이후 휴일 구현 시 휴일 추가
//TODO : 이후 메뉴 구현시 메뉴 리스트 추가

    private final Long id;
    private final String name;
    private final LocalTime openAt;
    private final LocalTime closeAt;
    private final Integer minimumPrice;
    private final List<MenuResponseDto> menuList;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    private StoreDetailResponseDto(Long id, String name, LocalTime openAt, LocalTime closeAt, Integer minimumPrice,
                                   List<MenuResponseDto> menuList, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.minimumPrice = minimumPrice;
        this.menuList = menuList;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static StoreDetailResponseDto of(Store store, List<MenuResponseDto> menuList) {
        return new StoreDetailResponseDto(store.getId(), store.getName(), store.getOpenAt(), store.getCloseAt(),
                store.getMinimumPrice(), menuList, store.getCreatedAt(), store.getModifiedAt());
    }
}
