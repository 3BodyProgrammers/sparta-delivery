package com.example.spartadelivery.domain.store.entity;

import com.example.spartadelivery.common.entity.BaseEntity;
import com.example.spartadelivery.config.LocalTimeConverter;
import com.example.spartadelivery.domain.store.dto.request.StoreUpdateRequestDto;
import com.example.spartadelivery.domain.user.entity.User;
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

    @Column(nullable = false)
    private Integer holiday;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Store(String name, LocalTime openAt, LocalTime closeAt, Integer minimumPrice, Integer holiday, User user) {
        this.name = name;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.holiday = holiday;
        this.minimumPrice = minimumPrice;
        this.user = user;
    }

    public static Store toEntity(String name, LocalTime openAt, LocalTime closeAt, Integer minimumPrice, User user) {
        return new Store(name, openAt, closeAt, minimumPrice, 0, user);
    }

    public void update(String name, LocalTime openAt, LocalTime closeAt, Integer minimumPrice) {
        this.name = name;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.minimumPrice = minimumPrice;
    }

    public void delete() {
        super.delete();
    }

    public void updateHolidays(Integer holiday) {
        this.holiday = holiday;
    }
}
