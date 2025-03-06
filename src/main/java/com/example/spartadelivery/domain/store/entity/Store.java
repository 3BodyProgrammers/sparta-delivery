package com.example.spartadelivery.domain.store.entity;

import com.example.spartadelivery.common.entity.BaseEntity;
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

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String notice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Store(String name, LocalTime openAt, LocalTime closeAt, Integer minimumPrice, Integer holiday, String notice, User user) {
        this.name = name;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.holiday = holiday;
        this.minimumPrice = minimumPrice;
        this.notice = notice;
        this.user = user;
    }

    public static Store toEntity(String name, LocalTime openAt, LocalTime closeAt, Integer minimumPrice, String notice, User user) {
        return new Store(name, openAt, closeAt, minimumPrice, 0, notice, user);
    }

    public void update(String name, LocalTime openAt, LocalTime closeAt, Integer minimumPrice, String notice) {
        this.name = name;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.minimumPrice = minimumPrice;
        this.notice = notice;
    }

    public void delete() {
        super.delete();
    }

    public void updateHolidays(Integer holiday) {
        this.holiday = holiday;
    }
}
