package com.example.spartadelivery.domain.review.dto.response;

import com.example.spartadelivery.domain.review.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewResponseDto {

    private final Long id;
    private final Long userId;
    private final Long storeId;
    private final Long orderId;
    private final Byte rating;
    private final String comments;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    private ReviewResponseDto(Long id, Long userId, Long storeId, Long orderId, Byte rating, String comments,
                              LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.userId = userId;
        this.storeId = storeId;
        this.orderId = orderId;
        this.rating = rating;
        this.comments = comments;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static ReviewResponseDto of(Review review) {
        return new ReviewResponseDto(review.getId(), review.getUser().getId(), review.getStore().getId(),
                review.getOrder().getId(), review.getRating(), review.getComments(), review.getCreatedAt(), review.getModifiedAt());
    }

}
