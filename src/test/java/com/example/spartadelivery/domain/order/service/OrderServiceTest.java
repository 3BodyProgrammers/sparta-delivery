package com.example.spartadelivery.domain.order.service;

import com.example.spartadelivery.common.annotation.Auth;
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
import com.example.spartadelivery.domain.store.repository.StoreRepository;
import com.example.spartadelivery.domain.store.service.StoreService;
import com.example.spartadelivery.domain.user.entity.User;
import com.example.spartadelivery.domain.user.enums.UserRole;
import com.example.spartadelivery.domain.user.repository.UserRepository;
import com.example.spartadelivery.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private StoreService storeService;

    @Mock
    private UserService userService;

    @Mock
    private StoreHolidayService storeHolidayService;

    @InjectMocks
    private OrderService orderService;

    @Nested
    class orderSaveTest {
        private OrderSaveRequestDto orderSaveRequestDto;
        private AuthUser authUser;
        private User testUser;
        private Store testStore;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);

            // Given - 테스트 데이터 생성
            // 테스트용 사용자 생성
            authUser = new AuthUser(1L, "aa@aa.com", "name", UserRole.USER);
            testUser = new User(authUser.getEmail(), "password123", authUser.getName(), authUser.getUserRole());

            // 테스트용 가게 생성
            testStore = Store.toEntity("테스트 가게", LocalDateTime.now().toLocalTime(),
                    LocalDateTime.now().plusHours(8).toLocalTime(), 10000, testUser);

            // 주문 요청 DTO 생성 (storeId를 명확히 설정)
            orderSaveRequestDto = new OrderSaveRequestDto(1L, "치킨", 25000);
        }

        @Test
        void 고객이_아닌_사용자가_주문한_경우_실패() {
            //Given
            when(userService.isUser(any())).thenReturn(false);
            //When+Then
            assertThatThrownBy(() -> orderService.save(testUser.getId(), "OWNER", orderSaveRequestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("주문 요청은 고객만 가능합니다.");
        }

        @Test
        void 폐업한_가계에_주문한_경우_실패() {
            //given
            when(userService.isUser(any())).thenReturn(true);
            when(storeService.findStoreById(anyLong())).thenReturn(testStore); // 테스트용 가게 반환
            when(storeService.isDeletedStore(anyLong())).thenReturn(true); // 가게가 폐업 상태로 설정

            //when+then
            assertThatThrownBy(() -> orderService.save(testUser.getId(), "USER", orderSaveRequestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("해당 가게는 현재 폐업 상태입니다.");
        }

        @Test
        void 휴일에_주문한_경우_실패() {
            //given
            when(userService.isUser(any())).thenReturn(true);
            when(storeService.findStoreById(anyLong())).thenReturn(testStore);
            when(storeService.isDeletedStore(anyLong())).thenReturn(false);
            when(storeHolidayService.isHoliday(any(), any())).thenReturn(true); // 가게가 휴일 상태로 설정

            // When + Then
            assertThatThrownBy(() -> orderService.save(testUser.getId(), "USER", orderSaveRequestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("현재는 주문이 불가능한 시간입니다.");
        }

        @Test
        void 영업시간_아닌_시간에_주문한_경우_실패() {
            // Given
            when(userService.isUser(any())).thenReturn(true);
            when(storeService.findStoreById(anyLong())).thenReturn(testStore);
            when(storeService.isDeletedStore(anyLong())).thenReturn(false);
            when(storeHolidayService.isHoliday(any(), any())).thenReturn(false);
            when(storeService.isWithinBusinessHours(any(), any())).thenReturn(false); // 영업시간 외 설정

            // When + Then
            assertThatThrownBy(() -> orderService.save(testUser.getId(), "USER", orderSaveRequestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("현재는 주문이 불가능한 시간입니다.");
        }

        @Test
        void 정상적인_주문을_한_경우_성공() {
            // Given
            when(userService.isUser(any())).thenReturn(true);
            when(storeService.findStoreById(anyLong())).thenReturn(testStore);
            when(storeService.isDeletedStore(anyLong())).thenReturn(false);
            when(storeHolidayService.isHoliday(any(), any())).thenReturn(false);
            when(storeService.isWithinBusinessHours(any(), any())).thenReturn(true);
            when(userService.findUserById(anyLong())).thenReturn(testUser);
            when(orderRepository.save(any())).thenAnswer(invocation -> {
                Order order = invocation.getArgument(0);
                return order; // 저장된 객체 반환
            });

            // When
            OrderSaveResponseDto responseDto = orderService.save(1L, "USER", orderSaveRequestDto);

            Order savedOrder = Order.toEntity(testUser, testStore, responseDto.getMenuName(), responseDto.getPrice());

            // Then
            assertNotNull(savedOrder);
            assertEquals("치킨", savedOrder.getMenuName());
            assertEquals(25000, savedOrder.getPrice());
        }
    }

    @Nested
    class orderStatusUpdateTest {
        private OrderStatusUpdateRequestDto updateRequestDto;
        private Order testOrder;
        private User testOwner;
        private Store testStore;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);

            testOwner = new User("owner@example.com", "password123", "사장님", UserRole.OWNER);
            testStore = Store.toEntity("테스트 가게", LocalDateTime.now().toLocalTime(),
                    LocalDateTime.now().plusHours(8).toLocalTime(), 10000, testOwner);
            testOrder = Order.toEntity(testOwner, testStore, "치킨", 25000);
        }

        @Test
        void 고객이_주문_상태를_변경_하려는_경우_실패() {
            // Given
            when(userService.isOwner(any())).thenReturn(false); // 사용자가 사장님이 아님

            updateRequestDto = new OrderStatusUpdateRequestDto(OrderStatus.ACCEPTED);

            // When + Then
            assertThatThrownBy(() -> orderService.updateOrderStatus(1L, "USER", updateRequestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("주문 상태 변경은 사장님만 가능합니다.");
        }

        @Test
        void 존재하지_않는_주문의_상태를_변경하려는_경우_실패() {
            // Given
            when(userService.isOwner(any())).thenReturn(true);
            when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.empty()); // 주문이 존재하지 않음

            updateRequestDto = new OrderStatusUpdateRequestDto(OrderStatus.ACCEPTED);

            // When + Then
            assertThatThrownBy(() -> orderService.updateOrderStatus(1L, "OWNER", updateRequestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("주문을 찾을 수 없습니다.");
        }

        @Test
        void 이미_종료되었거나_취소된_주문을_변경하려는_경우_실패() {
            // Given
            when(userService.isOwner(any())).thenReturn(true);
            when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.of(testOrder));

            // 주문을 COMPLETED 상태로 변경
            testOrder.updateStatus(OrderStatus.COMPLETED);
            updateRequestDto = new OrderStatusUpdateRequestDto(OrderStatus.ACCEPTED);

            // When + Then
            assertThatThrownBy(() -> orderService.updateOrderStatus(1L, "OWNER", updateRequestDto))
                    .isInstanceOf(CustomException.class)
                    .hasMessage("잘못된 주문 상태 변경 요청입니다.");
        }

        @Test
        void 정상적으로_주문_상태를_변경하는_경우_성공() {
            // Given
            when(userService.isOwner(any())).thenReturn(true);
            when(orderRepository.findById(anyLong())).thenReturn(java.util.Optional.of(testOrder));

            updateRequestDto = new OrderStatusUpdateRequestDto(OrderStatus.ACCEPTED);

            // When
            OrderStatusUpdateResponseDto responseDto = orderService.updateOrderStatus(1L, "OWNER", updateRequestDto);

            // Then
            assertNotNull(responseDto);
            assertEquals(OrderStatus.ACCEPTED, responseDto.getUpdatedStatus());
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
    }
}