package com.example.spartadelivery.domain.review.service;

import com.example.spartadelivery.common.annotation.Auth;
import com.example.spartadelivery.common.dto.AuthUser;
import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.order.entity.Order;
import com.example.spartadelivery.domain.order.enums.OrderStatus;
import com.example.spartadelivery.domain.order.service.OrderGetService;
import com.example.spartadelivery.domain.review.dto.request.ReviewRequestDto;
import com.example.spartadelivery.domain.review.dto.response.ReviewPageResponseDto;
import com.example.spartadelivery.domain.review.dto.response.ReviewResponseDto;
import com.example.spartadelivery.domain.review.entity.Review;
import com.example.spartadelivery.domain.review.repository.ReviewRepository;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.store.service.StoreGetService;
import com.example.spartadelivery.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderGetService orderGetService;
    private final StoreGetService storeGetService;

    @Transactional
    public ReviewResponseDto saveReview(@Auth AuthUser authUser, Long orderId, ReviewRequestDto requestDto) {
        User user = User.fromAuthUser(authUser);

        if (reviewRepository.existsByOrderId(orderId)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 작성된 리뷰가 있거나 삭제된 주문입니다. 리뷰를 다시 작성할 수 없습니다.");
        }

        Order order = orderGetService.findOrderWithStoreById(orderId);

        if (!isCompleted(order.getStatus())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "배달이 완료되지 않아 리뷰를 작성할 수 없습니다.");
        }

        Store store = order.getStore();

        if (store == null || store.getDeletedAt() != null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "해당 가게가 존재하지 않거나 삭제되었습니다.");
        }

        Review review = new Review(requestDto.getRating(), requestDto.getComments(), user, store, order);
        Review savedReview = reviewRepository.save(review);

        return ReviewResponseDto.of(savedReview);
    }

    @Transactional(readOnly = true)
    public ReviewPageResponseDto getReviews(Long storeId, int page, int size, Byte minRating, Byte maxRating) {
        // 가게가 존재 여부 및 삭제 여부 확인
        storeGetService.findByIdAndDeletedAtIsNull(storeId);

        // 리뷰 생성일을 기준으로 내림차순 정렬
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("createdAt")));

        Page<Review> reviews = reviewRepository.findAllByStoreIdAndRatingRange(storeId, minRating, maxRating, pageable);

        Long totalReviewCount = reviewRepository.countReviewByStoreId(storeId);
        Double averageReviewScore = reviewRepository.calculateAverageReviewScore(storeId);

        Page<ReviewResponseDto> reviewResponseDtos = reviews.map(ReviewResponseDto::of);

        return new ReviewPageResponseDto(reviewResponseDtos, totalReviewCount, averageReviewScore);
    }

    public ReviewResponseDto updateReview(AuthUser authUser, Long id, ReviewRequestDto requestDto) {
        User user = User.fromAuthUser(authUser);

        Review review = findReviewById(id);

        if (review.getDeletedAt() != null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "해당 리뷰는 삭제되었습니다.");
        }

        if (!review.getUser().getId().equals(user.getId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        review.update(requestDto.getRating(), requestDto.getComments());
        Review updatedReview = reviewRepository.save(review);

        return ReviewResponseDto.of(updatedReview);
    }

    @Transactional
    public String deleteReview(AuthUser authUser, Long id) {
        User user = User.fromAuthUser(authUser);

        Review review = findReviewById(id);

        if (review.getDeletedAt() != null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "이미 삭제된 리뷰입니다.");
        }

        if (!review.getUser().getId().equals(user.getId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }

        review.delete();

        return "리뷰 삭제가 완료되었습니다.";
    }

    // 배달 완료된 주문인지 확인
    public boolean isCompleted(OrderStatus status) {
        return status == OrderStatus.COMPLETED;
    }

    public Review findReviewById(Long reviewId) {
        Review review = reviewRepository.findReviewWithStoreById(reviewId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."));

        if (review.getStore() == null || review.getStore().getDeletedAt() != null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "해당 가게가 존재하지 않거나 삭제되었습니다.");
        }

        return review;
    }
}
