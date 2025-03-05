package com.example.spartadelivery.domain.review.dto.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class ReviewPageResponseDto {

    private final Page<ReviewResponseDto> reviews;
    private final Long totalReviewCount;
    private final Double averageReviewScore;

    public ReviewPageResponseDto(Page<ReviewResponseDto> reviews, Long totalReviewCount, Double averageReviewScore) {
        this.reviews = reviews;
        this.totalReviewCount = totalReviewCount;
        this.averageReviewScore = averageReviewScore;
    }

}
