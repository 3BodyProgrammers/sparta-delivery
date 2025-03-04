package com.example.spartadelivery.domain.order.entity;

import com.example.spartadelivery.domain.order.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    }

}
