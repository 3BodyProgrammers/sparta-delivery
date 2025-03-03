package com.example.spartadelivery.domain.menu.entity;

import com.example.spartadelivery.common.entity.BaseEntity;
import com.example.spartadelivery.domain.menu.dto.request.MenuSaveRequestDto;
import com.example.spartadelivery.domain.menu.dto.request.MenuUpdateRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "menus")
@NoArgsConstructor
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    //TODO : 유저 및 가게 구현 이후 추가
    private Long userId;
    private String userRole;

    private Long storeId;

    private Menu(String name, Integer price, Long userId, String userRole, Long storeId) {
        this.name = name;
        this.price = price;
        this.userId = userId;
        this.userRole = userRole;
        this.storeId = storeId;
    }

    public static Menu toEntity(MenuSaveRequestDto request, Long userId, String userRole, Long storeId) {
        return new Menu(request.getName(), request.getPrice(), userId, userRole, storeId);
    }

    public void update(MenuUpdateRequestDto request) {
        this.name = request.getName();
        this.price = request.getPrice();

    }

    public void delete() {
        super.delete();
    }

    public void restore() {
        super.restore();
    }
}
