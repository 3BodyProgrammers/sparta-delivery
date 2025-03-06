package com.example.spartadelivery.domain.order.service;

import com.example.spartadelivery.common.dto.AuthUser;
import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.holiday.service.StoreHolidayService;
import com.example.spartadelivery.domain.order.dto.request.OrderSaveRequestDto;
import com.example.spartadelivery.domain.order.dto.request.OrderStatusUpdateRequestDto;
import com.example.spartadelivery.domain.order.dto.response.OrderResponseDto;
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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final StoreGetService storeGetService;
    private final StoreHolidayService storeHolidayService;


    @Transactional
    public OrderSaveResponseDto save(AuthUser authUser, OrderSaveRequestDto request) {
        Store store = storeGetService.findByIdAndDeletedAtIsNull(request.getStoreId());

        LocalDateTime now = LocalDateTime.now();
        if (storeHolidayService.isHoliday(store, now) || !storeGetService.isWithinBusinessHours(store, now)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "현재는 주문이 불가능한 시간입니다.");
        }

        User user = User.fromAuthUser(authUser);
        Order order = Order.toEntity(user, store, request.getMenuName(), request.getPrice());

        Order savedOrder = orderRepository.save(order);
        return OrderSaveResponseDto.of(savedOrder);
    }

    @Transactional
    public OrderStatusUpdateResponseDto updateOrderStatus(Long orderId, OrderStatusUpdateRequestDto request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));

        OrderStatus newStatus = request.getNewStatus();
        if (!order.getStatus().canChangeTo(newStatus)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "잘못된 주문 상태 변경 요청입니다.");
        }

        order.updateStatus(newStatus);

        return OrderStatusUpdateResponseDto.of(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrders(AuthUser authUser, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        User user = User.fromAuthUser(authUser);

        if (UserRole.USER.equals(user.getUserRole())) {
            return orderRepository.findByUserId(user.getId(), pageable).map(OrderResponseDto::fromEntity);
        }

        //Role=Owner
        List<Long> storeIds = storeGetService.findAllByUserId(user.getId())
                                            .stream()
                                            .map(Store::getId)
                                            .toList();
        return orderRepository.findByStoreIdIn(storeIds, pageable).map(OrderResponseDto::fromEntity);

    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(Long orderId, AuthUser authUser) {
        Order order = orderRepository.findWithStoreAndUserById(orderId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));

        User user = User.fromAuthUser(authUser);

        if (UserRole.USER.equals(user.getUserRole()) && !order.getUser().getId().equals(user.getId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "본인이 주문한 내역만 조회할 수 있습니다.");
        }

        Long storeId = order.getStore().getId();
    
        if (isOwnerOfStore(user, storeId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "본인의 가게에 들어온 주문만 조회할 수 있습니다.");
        }

        return OrderResponseDto.fromEntity(order);
    }

    @Transactional
    public void cancelOrder(Long orderId, AuthUser authUser) {
        Order order = orderRepository.findWithStoreAndUserById(orderId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));

        if (order.getStatus() == OrderStatus.DELIVERY || order.getStatus() == OrderStatus.COMPLETED) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "배달 중이거나 완료된 주문은 취소할 수 없습니다.");
        }

        User user = User.fromAuthUser(authUser);

        if (UserRole.USER.equals(user.getUserRole())) {
            if (!order.getUser().getId().equals(user.getId())) {
                throw new CustomException(HttpStatus.FORBIDDEN, "본인의 주문만 취소할 수 있습니다.");
            }
            if (order.getStatus() != OrderStatus.PENDING) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "주문이 접수되면 취소할 수 없습니다.");
            }
        }

        Long storeId = order.getStore().getId();

        if (isOwnerOfStore(user, storeId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "본인의 가게 주문만 취소할 수 있습니다.");
        }

        order.updateStatus(OrderStatus.CANCELED);
    }

    public Order findOrderWithStoreById(Long orderId) {
        return orderRepository.findWithStoreById(orderId)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "주문을 찾을 수 없습니다."));
    }

    private boolean isOwnerOfStore(User user, Long storeId) {
        return UserRole.OWNER.equals(user.getUserRole()) && !storeGetService.findByIdAndDeletedAtIsNull(storeId)
                .getUser().getId().equals(user.getId());
    }
}
