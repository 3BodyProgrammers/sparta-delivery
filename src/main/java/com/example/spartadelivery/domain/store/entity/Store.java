package com.example.spartadelivery.domain.store.entity;

import com.example.spartadelivery.common.entity.BaseEntity;
import com.example.spartadelivery.config.LocalTimeConverter;
import com.example.spartadelivery.domain.store.dto.request.StoreUpdateRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@Entity
@Table(name = "stores")
@NoArgsConstructor
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalTime openAt;

    @Column(nullable = false)
    private LocalTime closeAt;

    @Column(nullable = false)
    private Integer minimumPrice;

    //TODO : 나중에 유저 구현 이후 유저로 업데이트
    @Column(nullable = false)
    private Long userId;
    @Column(nullable = false)
    private String userRole;


    private Store(String name, LocalTime openAt, LocalTime closeAt, Integer minimumPrice, Long userId,
                  String userRole) {
        this.name = name;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.minimumPrice = minimumPrice;
        this.userId = userId;
        this.userRole = userRole;
    }

    public static Store toEntity(String name, LocalTime openAt, LocalTime closeAt, Integer minimumPrice, Long userId,
                                 String userRole) {
        return new Store(name, openAt, closeAt, minimumPrice, userId, userRole);
    }

    public void update(StoreUpdateRequestDto request) {
        this.name = request.getName();
        this.openAt = LocalTimeConverter.toLocalTime(request.getOpenAt());
        this.closeAt = LocalTimeConverter.toLocalTime(request.getCloseAt());
        this.minimumPrice = request.getMinimumPrice();
    }

    public void delete() {
        super.delete();
    }
}
