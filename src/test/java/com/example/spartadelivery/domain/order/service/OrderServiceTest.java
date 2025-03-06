package com.example.spartadelivery.domain.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.example.spartadelivery.common.dto.AuthUser;
import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.holiday.service.StoreHolidayService;
import com.example.spartadelivery.domain.order.dto.request.OrderSaveRequestDto;
import com.example.spartadelivery.domain.order.dto.request.OrderStatusUpdateRequestDto;
import com.example.spartadelivery.domain.order.dto.response.OrderSaveResponseDto;
import com.example.spartadelivery.domain.order.dto.response.OrderStatusUpdateResponseDto;
import com.example.spartadelivery.domain.order.entity.Order;
import com.example.spartadelivery.domain.order.enums.OrderStatus;
import com.example.spartadelivery.domain.order.repository.OrderRepository;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.store.service.StoreGetService;
import com.example.spartadelivery.domain.user.entity.User;
import com.example.spartadelivery.domain.user.enums.UserRole;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StoreGetService storeGetService;

    @Mock
    private StoreHolidayService storeHolidayService;

    @InjectMocks
    private OrderService orderService;

    @Nested
    class orderSaveTest {

        private OrderSaveRequestDto orderSaveRequestDto;
        private AuthUser user;
        private AuthUser owner;
        private User testUser1, testUser2;
        private Store testStore;

        @BeforeEach
        void setUp() {
            // Given - 테스트 데이터 생성
            // 테스트용 사용자 생성
            user = new AuthUser(1L, "aa@aa.com", "name", UserRole.USER);
            owner = new AuthUser(2L, "bb@bb.com", "name2", UserRole.OWNER);
            testUser1 = User.fromAuthUser(user);
            testUser2 = User.fromAuthUser(owner);

            // 테스트용 가게 생성
            testStore = Store.toEntity("테스트 가게", LocalDateTime.now().toLocalTime(),
                    LocalDateTime.now().plusHours(8).toLocalTime(), 10000, "공지", testUser2);

            // 주문 요청 DTO 생성 (storeId를 명확히 설정)
            orderSaveRequestDto = new OrderSaveRequestDto(1L, "치킨", 25000);
        }

        @Test
        void 가게가_없을_경우_실패(){
            //Given
            given(storeGetService.findByIdAndDeletedAtIsNull(any())).willThrow(new CustomException(HttpStatus.BAD_REQUEST, "해당 가게는 존재하지 않습니다."));

            //When & Then
            assertThatThrownBy(() -> orderService.save(user, orderSaveRequestDto))
                    .isInstanceOf(CustomException.class).hasMessage("해당 가게는 존재하지 않습니다.");
        }

        @Test
        void 주문이_불가능한_요일_경우_실패(){
            //Given
            given(storeGetService.findByIdAndDeletedAtIsNull(any())).willReturn(testStore);
            given(storeHolidayService.isHoliday(any(), any())).willReturn(true);

            //When & Then
            assertThatThrownBy(() -> orderService.save(user, orderSaveRequestDto))
                    .isInstanceOf(CustomException.class).hasMessage("현재는 주문이 불가능한 시간입니다.");
        }

        @Test
        void 주문이_불가능한_시간일_경우_실패(){
            //Given
            given(storeGetService.findByIdAndDeletedAtIsNull(any())).willReturn(testStore);
            given(storeHolidayService.isHoliday(any(), any())).willReturn(false);
            given(storeGetService.isWithinBusinessHours(any(), any())).willReturn(false);

            //When & Then
            assertThatThrownBy(() -> orderService.save(user, orderSaveRequestDto))
                    .isInstanceOf(CustomException.class).hasMessage("현재는 주문이 불가능한 시간입니다.");
        }

        @Test
        void 주문이_성공(){
            //Given
            Order order = Order.toEntity(testUser1, testStore, "치킨", 25000);
            given(storeGetService.findByIdAndDeletedAtIsNull(any())).willReturn(testStore);
            given(storeHolidayService.isHoliday(any(), any())).willReturn(false);
            given(storeGetService.isWithinBusinessHours(any(), any())).willReturn(true);
            given(orderRepository.save(any())).willReturn(order);

            //When
            OrderSaveResponseDto response = orderService.save(user, orderSaveRequestDto);

            //Then
            assertThat(response).isNotNull();
            assertThat(response.getMenuName()).isEqualTo("치킨");
        }
    }

   @Nested
    class orderStatusUpdateTest {

       private OrderStatusUpdateRequestDto request;
       private AuthUser user;
       private AuthUser owner;
       private User testUser1, testUser2;
       private Store testStore;
       private Order order;

        @BeforeEach
        void setUp() {
            user = new AuthUser(1L, "aa@aa.com", "name", UserRole.USER);
            owner = new AuthUser(2L, "bb@bb.com", "name2", UserRole.OWNER);
            testUser1 = User.fromAuthUser(user);
            testUser2 = User.fromAuthUser(owner);

            testStore = Store.toEntity("테스트 가게", LocalDateTime.now().toLocalTime(),
                    LocalDateTime.now().plusHours(8).toLocalTime(), 10000, "공지", testUser2);

            request = new OrderStatusUpdateRequestDto(OrderStatus.ACCEPTED);
            order = Order.toEntity(testUser1, testStore, "치킨", 25000);
        }

        @Test
        void 주문이_존재_하지_않는_경우_실패() {
            // given
            long orderId = 1L;
            given(orderRepository.findWithStoreAndUserById(any())).willReturn(Optional.empty());
            // when & then
            assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, owner, request))
                    .isInstanceOf(CustomException.class).hasMessage("주문을 찾을 수 없습니다.");
        }

       @Test
       void 주문_변경_요청자가_해당_가게_사장님이_아닌_경우_실패() {
           // given
           long orderId = 1L;
           AuthUser anotherOwner = new AuthUser(3L, "cc@cc.com", "name3", UserRole.OWNER);
           given(orderRepository.findWithStoreAndUserById(any())).willReturn(Optional.of(order));
           given(storeGetService.findByIdAndDeletedAtIsNull(any())).willReturn(testStore);
           // when & then
           assertThatThrownBy(() -> orderService.updateOrderStatus(orderId, anotherOwner, request))
                   .isInstanceOf(CustomException.class).hasMessage("본인의 가게에 들어온 주문만 상태 변경을 할 수 있습니다.");
       }

       @Test
       void PENDING에서가능한_변경_테스트() {
           assertTrue(OrderStatus.PENDING.canChangeTo(OrderStatus.ACCEPTED));
           assertTrue(OrderStatus.PENDING.canChangeTo(OrderStatus.CANCELED));
           assertFalse(OrderStatus.PENDING.canChangeTo(OrderStatus.DELIVERY));
           assertFalse(OrderStatus.PENDING.canChangeTo(OrderStatus.COMPLETED));
       }

       @Test
       void ACCEPTED에서가능한_변경_테스트() {
           assertTrue(OrderStatus.ACCEPTED.canChangeTo(OrderStatus.DELIVERY));
           assertFalse(OrderStatus.ACCEPTED.canChangeTo(OrderStatus.CANCELED));
           assertFalse(OrderStatus.ACCEPTED.canChangeTo(OrderStatus.COMPLETED));
       }

       @Test
       void DELIVERY에서가능한_변경_테스트() {
           assertTrue(OrderStatus.DELIVERY.canChangeTo(OrderStatus.COMPLETED));
           assertFalse(OrderStatus.DELIVERY.canChangeTo(OrderStatus.PENDING));
           assertFalse(OrderStatus.DELIVERY.canChangeTo(OrderStatus.ACCEPTED));
       }

       @Test
       void COMPLETED에서변경_불가능_테스트() {
           assertFalse(OrderStatus.COMPLETED.canChangeTo(OrderStatus.PENDING));
           assertFalse(OrderStatus.COMPLETED.canChangeTo(OrderStatus.ACCEPTED));
           assertFalse(OrderStatus.COMPLETED.canChangeTo(OrderStatus.DELIVERY));
       }

       @Test
       void CANCELED에서변경_불가능_테스트() {
           assertFalse(OrderStatus.CANCELED.canChangeTo(OrderStatus.PENDING));
           assertFalse(OrderStatus.CANCELED.canChangeTo(OrderStatus.ACCEPTED));
           assertFalse(OrderStatus.CANCELED.canChangeTo(OrderStatus.DELIVERY));
       }

       @Test
       void 주문_상태_변경성공() {
           // given
           long orderId = 1L;
           given(orderRepository.findWithStoreAndUserById(any())).willReturn(Optional.of(order));
           given(storeGetService.findByIdAndDeletedAtIsNull(any())).willReturn(testStore);

           // when
           OrderStatusUpdateResponseDto response = orderService.updateOrderStatus(orderId, owner, request);

           // then
           assertThat(response).isNotNull();
           assertThat(response.getUpdatedStatus()).isEqualTo(OrderStatus.ACCEPTED);
       }
    }
}