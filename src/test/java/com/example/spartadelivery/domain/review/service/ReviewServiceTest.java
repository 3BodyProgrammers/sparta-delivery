package com.example.spartadelivery.domain.review.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import com.example.spartadelivery.common.dto.AuthUser;
import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.order.entity.Order;
import com.example.spartadelivery.domain.order.enums.OrderStatus;
import com.example.spartadelivery.domain.order.service.OrderService;
import com.example.spartadelivery.domain.review.dto.request.ReviewRequestDto;
import com.example.spartadelivery.domain.review.dto.response.ReviewPageResponseDto;
import com.example.spartadelivery.domain.review.dto.response.ReviewResponseDto;
import com.example.spartadelivery.domain.review.entity.Review;
import com.example.spartadelivery.domain.review.repository.ReviewRepository;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.store.service.StoreGetService;
import com.example.spartadelivery.domain.store.service.StoreService;
import com.example.spartadelivery.domain.user.entity.User;
import com.example.spartadelivery.domain.user.enums.UserRole;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private StoreGetService storeGetService;

    @InjectMocks
    private ReviewService reviewService;

    @Nested
    class saveReviewTest {

        private AuthUser authUser;
        private User user;
        private Store store;
        private Order order;
        private ReviewRequestDto requestDto;

        @BeforeEach
        void setUp() {
            authUser = new AuthUser(1L, "aa@aa.com", "name", UserRole.USER);
            user = User.fromAuthUser(authUser);
            store = Store.toEntity("Store", LocalTime.of(8, 0), LocalTime.of(22, 0), 10000, user);
            order = Order.toEntity(user, store, "menuName", 12000);
            requestDto = new ReviewRequestDto((byte) 5, "Great food!");
        }

        @Test
        void 리뷰를_생성할_수_있다() {
            // given
            long orderId = 1L;
            order.updateStatus(OrderStatus.COMPLETED);
            Review review = new Review(requestDto.getRating(), requestDto.getComments(), user, store, order);

            given(reviewRepository.existsByOrderId(orderId)).willReturn(false);
            given(orderService.findOrderWithStoreById(orderId)).willReturn(order);
            given(reviewRepository.save(any(Review.class))).willReturn(review);

            // when
            ReviewResponseDto response = reviewService.saveReview(authUser, orderId, requestDto);

            // then
            assertNotNull(response);
            assertEquals(requestDto.getRating(), response.getRating());
            assertEquals(requestDto.getComments(), response.getComments());
        }


        @Test
        void 이미_리뷰가_존재하거나_삭제되었으면_예외를_던진다() {
            // given
            long orderId = 1L;
            given(reviewRepository.existsByOrderId(orderId)).willReturn(true);

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> reviewService.saveReview(authUser, orderId, requestDto));
            assertEquals("이미 작성된 리뷰가 있거나 삭제된 주문입니다. 리뷰를 다시 작성할 수 없습니다.", exception.getMessage());
        }

        @Test
        void 배달이_완료되지_않은_주문이면_예외를_던진다() {
            // given
            long orderId = 1L;
            given(reviewRepository.existsByOrderId(orderId)).willReturn(false);
            given(orderService.findOrderWithStoreById(orderId)).willReturn(order);

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> reviewService.saveReview(authUser, orderId, requestDto));
            assertEquals("배달이 완료되지 않아 리뷰를 작성할 수 없습니다.", exception.getMessage());
        }

        @Test
        void 가게를_찾을_수_없으면_예외를_던진다() {
            // given
            long orderId = 1L;
            given(reviewRepository.existsByOrderId(orderId)).willReturn(false);
            given(orderService.findOrderWithStoreById(orderId)).willReturn(order);
            order.updateStatus(OrderStatus.COMPLETED);
            store.delete();

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> reviewService.saveReview(authUser, orderId, requestDto));
            assertEquals("해당 가게가 존재하지 않거나 삭제되었습니다.", exception.getMessage());
        }
    }

    @Nested
    class getReviewsTest {

        private Long storeId;
        private int page;
        private int size;
        private Page<Review> reviewPage;
        private Pageable pageable;

        @BeforeEach
        void setup() {
            storeId = 1L;
            page = 1;
            size = 10;

            Store store = mock(Store.class);
            given(storeGetService.findByIdAndDeletedAtIsNull(storeId)).willReturn(store);

            Review review1 = new Review((byte) 5, "Excellent", mock(User.class), store, mock(Order.class));
            Review review2 = new Review((byte) 4, "Good", mock(User.class), store, mock(Order.class));
            reviewPage = new PageImpl<>(List.of(review1, review2));

            pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.desc("createdAt")));
        }

        @Test
        void 리뷰_페이지를_조회할_수_있다() {
            // given
            Byte minRating = 1;
            Byte maxRating = 5;

            given(reviewRepository.findAllByStoreIdAndRatingRange(storeId, minRating, maxRating, pageable)).willReturn(reviewPage);
            given(reviewRepository.countReviewByStoreId(storeId)).willReturn(2L);
            given(reviewRepository.calculateAverageReviewScore(storeId)).willReturn(4.5);

            // when
            ReviewPageResponseDto response = reviewService.getReviews(storeId, page, size, minRating, maxRating);

            // then
            assertNotNull(response);
            assertEquals(2L, response.getTotalReviewCount());
            assertEquals(4.5, response.getAverageReviewScore());
            assertEquals(2, response.getReviews().getContent().size());
            assertEquals("Excellent", response.getReviews().getContent().get(0).getComments());
            assertEquals("Good", response.getReviews().getContent().get(1).getComments());
        }

        @Test
        void 조회_시_최소_최대_평점을_생략할_수_있다() {
            // given
            given(reviewRepository.findAllByStoreIdAndRatingRange(storeId, null, null, pageable)).willReturn(reviewPage);
            given(reviewRepository.countReviewByStoreId(storeId)).willReturn(2L);
            given(reviewRepository.calculateAverageReviewScore(storeId)).willReturn(4.5);

            // when
            ReviewPageResponseDto response = reviewService.getReviews(storeId, page, size, null, null);

            // then
            assertNotNull(response);
            assertEquals(2L, response.getTotalReviewCount());
            assertEquals(4.5, response.getAverageReviewScore());
            assertEquals(2, response.getReviews().getContent().size());
            assertEquals("Excellent", response.getReviews().getContent().get(0).getComments());
            assertEquals("Good", response.getReviews().getContent().get(1).getComments());
        }
    }

    @Nested
    class updateReviewTest {

        private AuthUser authUser;
        private User user;
        private Store store;
        private Order order;
        private ReviewRequestDto requestDto;
        private Review review;

        @BeforeEach
        void setUp() {
            authUser = new AuthUser(1L, "aa@aa.com", "name", UserRole.USER);
            user = User.fromAuthUser(authUser);
            store = Store.toEntity("Store", LocalTime.of(8, 0), LocalTime.of(22, 0), 10000, user);
            order = Order.toEntity(user, store, "menuName", 12000);
            requestDto = new ReviewRequestDto((byte) 5, "Great food!");
            review = new Review(requestDto.getRating(), requestDto.getComments(), user, store, order);
        }

        @Test
        void 리뷰를_수정할_수_있다() {
            // given
            long reviewId = 1L;
            order.updateStatus(OrderStatus.COMPLETED);

            given(reviewRepository.findReviewWithStoreById(reviewId)).willReturn(Optional.of(review));
            given(reviewRepository.save(any(Review.class))).willReturn(review);

            // when
            ReviewResponseDto response = reviewService.updateReview(authUser, reviewId, requestDto);

            // then
            assertNotNull(response);
            assertEquals(requestDto.getRating(), response.getRating());
            assertEquals(requestDto.getComments(), response.getComments());
        }

        @Test
        void 리뷰를_찾을_수_없으면_예외를_던진다() {
            // given
            long reviewId = 1L;
            order.updateStatus(OrderStatus.COMPLETED);
            given(reviewRepository.findReviewWithStoreById(reviewId)).willReturn(Optional.empty());

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> reviewService.updateReview(authUser, reviewId, requestDto));
            assertEquals("리뷰를 찾을 수 없습니다.", exception.getMessage());
        }

        @Test
        void 가게를_찾을_수_없으면_예외를_던진다() {
            // given
            long reviewId = 1L;
            order.updateStatus(OrderStatus.COMPLETED);

            // 가게가 삭제된 상태로 mock하기
            Store mockedStore = mock(Store.class);
            given(mockedStore.getDeletedAt()).willReturn(LocalDateTime.now());

            // 리뷰에 mock된 가게 설정
            Review mockedReview = mock(Review.class);
            given(mockedReview.getStore()).willReturn(mockedStore);

            given(reviewRepository.findReviewWithStoreById(reviewId)).willReturn(Optional.of(mockedReview));

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> reviewService.updateReview(authUser, reviewId, requestDto));
            assertEquals("해당 가게가 존재하지 않거나 삭제되었습니다.", exception.getMessage());
        }

        @Test
        void 리뷰가_삭제된_경우_예외를_던진다() {
            // given
            long reviewId = 1L;
            order.updateStatus(OrderStatus.COMPLETED);

            // 삭제된 상태의 리뷰를 모킹
            Review deletedReview = spy(review);
            given(deletedReview.getDeletedAt()).willReturn(LocalDateTime.now());

            given(reviewRepository.findReviewWithStoreById(reviewId)).willReturn(Optional.of(deletedReview));

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> reviewService.updateReview(authUser, reviewId, requestDto));
            assertEquals("해당 리뷰는 삭제되었습니다.", exception.getMessage());
        }

        @Test
        void 본인이_작성한_리뷰가_아니면_예외를_던진다() {
            // given
            long reviewId = 1L;
            order.updateStatus(OrderStatus.COMPLETED);

            User anotherUser = new User("other@user.com", "Password12!", "Other User", UserRole.USER);
            ReflectionTestUtils.setField(anotherUser, "id", 2L);

            Review newReview = new Review(requestDto.getRating(), requestDto.getComments(), anotherUser, store, order);

            given(reviewRepository.findReviewWithStoreById(reviewId)).willReturn(Optional.of(newReview));

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> reviewService.updateReview(authUser, reviewId, requestDto));
            assertEquals("본인이 작성한 리뷰만 수정할 수 있습니다.", exception.getMessage());
        }
    }

    @Nested
    class deleteReviewTest {

        private AuthUser authUser;
        private User user;
        private Store store;
        private Order order;
        private Review review;

        @BeforeEach
        void setUp() {
            authUser = new AuthUser(1L, "aa@aa.com", "name", UserRole.USER);
            user = User.fromAuthUser(authUser);
            store = Store.toEntity("Store", LocalTime.of(8, 0), LocalTime.of(22, 0), 10000, user);
            order = Order.toEntity(user, store, "menuName", 12000);
            review = new Review((byte) 5, "Great food!", user, store, order);
        }

        @Test
        void 리뷰를_삭제할_수_있다() {
            // given
            long reviewId = 1L;
            order.updateStatus(OrderStatus.COMPLETED);

            given(reviewRepository.findReviewWithStoreById(reviewId)).willReturn(Optional.of(review));

            // when
            String response = reviewService.deleteReview(authUser, reviewId);

            // then
            assertNotNull(response);
            assertEquals("리뷰 삭제가 완료되었습니다.", response);
            assertNotNull(review.getDeletedAt());
        }

        @Test
        void 리뷰를_찾을_수_없으면_예외를_던진다() {
            // given
            long reviewId = 1L;
            order.updateStatus(OrderStatus.COMPLETED);
            given(reviewRepository.findReviewWithStoreById(reviewId)).willReturn(Optional.empty());

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> reviewService.deleteReview(authUser, reviewId));
            assertEquals("리뷰를 찾을 수 없습니다.", exception.getMessage());
        }

        @Test
        void 가게를_찾을_수_없으면_예외를_던진다() {
            // given
            long reviewId = 1L;
            order.updateStatus(OrderStatus.COMPLETED);

            // 가게가 삭제된 상태로 mock하기
            Store mockedStore = mock(Store.class);
            given(mockedStore.getDeletedAt()).willReturn(LocalDateTime.now());

            // 리뷰에 mock된 가게 설정
            Review mockedReview = mock(Review.class);
            given(mockedReview.getStore()).willReturn(mockedStore);

            given(reviewRepository.findReviewWithStoreById(reviewId)).willReturn(Optional.of(mockedReview));

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> reviewService.deleteReview(authUser, reviewId));
            assertEquals("해당 가게가 존재하지 않거나 삭제되었습니다.", exception.getMessage());
        }

        @Test
        void 리뷰가_삭제된_경우_예외를_던진다() {
            // given
            long reviewId = 1L;
            order.updateStatus(OrderStatus.COMPLETED);

            // 삭제된 상태의 리뷰를 모킹
            Review deletedReview = spy(review);
            given(deletedReview.getDeletedAt()).willReturn(LocalDateTime.now());

            given(reviewRepository.findReviewWithStoreById(reviewId)).willReturn(Optional.of(deletedReview));

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> reviewService.deleteReview(authUser, reviewId));
            assertEquals("이미 삭제된 리뷰입니다.", exception.getMessage());
        }

        @Test
        void 본인이_작성한_리뷰가_아니면_예외를_던진다() {
            // given
            long reviewId = 1L;
            order.updateStatus(OrderStatus.COMPLETED);

            User anotherUser = new User("other@user.com", "Password12!", "Other User", UserRole.USER);
            ReflectionTestUtils.setField(anotherUser, "id", 2L);

            Review newReview = new Review((byte) 5, "Great food!", anotherUser, store, order);

            given(reviewRepository.findReviewWithStoreById(reviewId)).willReturn(Optional.of(newReview));

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> reviewService.deleteReview(authUser, reviewId));
            assertEquals("본인이 작성한 리뷰만 삭제할 수 있습니다.", exception.getMessage());
        }
    }
}