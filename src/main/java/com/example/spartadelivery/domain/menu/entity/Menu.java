package com.example.spartadelivery.domain.menu.entity;

import com.example.spartadelivery.common.entity.BaseEntity;
import com.example.spartadelivery.domain.menu.dto.request.MenuSaveRequestDto;
import com.example.spartadelivery.domain.menu.dto.request.MenuUpdateRequestDto;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.user.entity.User;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    private Menu(String name, Integer price, User user, Store store) {
        this.name = name;
        this.price = price;
        this.user = user;
        this.store = store;
    }

    public static Menu toEntity(MenuSaveRequestDto request, User user, Store store) {
        return new Menu(request.getName(), request.getPrice(), user, store);
    }

    public void update(String name, Integer price) {
        this.name = name;
        this.price = price;

    }

    public void delete() {
        super.delete();
    }

    public void restore() {
        super.restore();
    }
}
