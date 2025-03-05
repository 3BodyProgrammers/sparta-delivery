package com.example.spartadelivery.domain.review.entity;

import com.example.spartadelivery.common.entity.BaseEntity;
import com.example.spartadelivery.domain.order.entity.Order;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name="reviews")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TINYINT", nullable = false)
    private Byte rating;  // 1~5 점

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    public Review(Byte rating, String comments, User user, Store store, Order order) {
        this.rating = rating;
        this.comments = comments;
        this.user = user;
        this.store = store;
        this.order = order;
    }

    public void update(Byte rating, String comments) {
        this.rating = rating;
        this.comments = comments;
    }


}
