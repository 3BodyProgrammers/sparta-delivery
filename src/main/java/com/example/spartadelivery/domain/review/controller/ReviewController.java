package com.example.spartadelivery.domain.review.controller;

import com.example.spartadelivery.common.annotation.Auth;
import com.example.spartadelivery.common.annotation.User;
import com.example.spartadelivery.common.dto.AuthUser;
import com.example.spartadelivery.domain.review.dto.request.ReviewRequestDto;
import com.example.spartadelivery.domain.review.dto.response.ReviewPageResponseDto;
import com.example.spartadelivery.domain.review.dto.response.ReviewResponseDto;
import com.example.spartadelivery.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @User
    @PostMapping("/orders/{orderId}/reviews")
    public ResponseEntity<ReviewResponseDto> saveReview(
            @Auth AuthUser authUser,
            @PathVariable Long orderId,
            @Valid @RequestBody ReviewRequestDto requestDto
    ) {
        return ResponseEntity.ok(reviewService.saveReview(authUser, orderId, requestDto));
    }


    @GetMapping("/stores/{storeId}/reivews")
    public ResponseEntity<ReviewPageResponseDto> getReviews(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Byte minRating,
            @RequestParam(required = false) Byte maxRating
    ) {
        return ResponseEntity.ok(reviewService.getReviews(storeId, page, size, minRating, maxRating));
    }

    @User
    @PutMapping("/reviews/{id}")
    public ResponseEntity<ReviewResponseDto> updateReview(
            @Auth AuthUser authUser,
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequestDto requestDto
    ) {
        return ResponseEntity.ok(reviewService.updateReview(authUser, id, requestDto));
    }

    @User
    @PostMapping("/reviews/delete/{id}")
    public ResponseEntity<String> deleteReview(
            @Auth AuthUser authUser,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(reviewService.deleteReview(authUser, id));
    }

}
