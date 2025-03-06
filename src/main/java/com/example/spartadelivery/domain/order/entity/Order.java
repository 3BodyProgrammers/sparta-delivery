package com.example.spartadelivery.domain.order.entity;

import com.example.spartadelivery.common.entity.BaseEntity;
import com.example.spartadelivery.domain.order.enums.OrderStatus;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false)
    private String menuName;

    @Column(nullable = false)
    private int price;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public static Order toEntity(User user, Store store, String menuName, int price) {
        Order order = new Order();
        order.user = user;
        order.store = store;
        order.menuName = menuName;
        order.price = price;
        order.status = OrderStatus.PENDING;
        return order;
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
        if (newStatus == OrderStatus.COMPLETED || newStatus == OrderStatus.CANCELED) {
            super.delete();
        }
    }

}