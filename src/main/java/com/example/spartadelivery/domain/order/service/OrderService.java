package com.example.spartadelivery.domain.order.service;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.order.dto.request.OrderStatusUpdateRequestDto;
import com.example.spartadelivery.domain.order.dto.request.OrderSaveRequestDto;
import com.example.spartadelivery.domain.order.dto.response.OrderResponseDto;
import com.example.spartadelivery.domain.order.dto.response.OrderSaveResponseDto;
import com.example.spartadelivery.domain.order.dto.response.OrderStatusUpdateResponseDto;
import com.example.spartadelivery.domain.order.entity.Order;
import com.example.spartadelivery.domain.order.enums.OrderStatus;
import com.example.spartadelivery.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final StoreService storeService;
    private final MenuService menuService;
    private final UserService userService;


    public OrderSaveResponseDto save(Long userId, String userRole, OrderSaveRequestDto request) {
        if(!isUser(userRole)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "주문 요청은 고객만 가능합니다.");
        }

        Store store = storeService.findStoreById(request.getStoreId());
        if(storeService.isDeletedStore(request.getStoreId())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "해당 가게는 현재 폐업 상태입니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (isHoliday(now) || !storeService.isWithinBusinessHours(store, now)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "현재는 주문이 불가능한 시간입니다.");
        }

        Menu menu = menuService.findMenuByStoreAndName(request.getStoreId(), request.getMenuName());

        int totalPrice = menu.getPrice() * request.getQuantity();
        if (totalPrice < store.getMinOrderPrice()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "최소 주문 금액을 충족하지 않습니다.");
        }

        User user = userService.findUserById(userId);
        Order order = Order.toEntity(user, store, request.getMenuName(), request.getPrice(), OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);
        return OrderSaveResponseDto.of(savedOrder);
    }

    @Transactional
    public OrderStatusUpdateResponseDto updateOrderStatus(Long orderId, String userRole, OrderStatusUpdateRequestDto request) {

        if (!isOwner(userRole)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "주문 상태 변경은 사장님만 가능합니다.");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));

        OrderStatus newStatus = request.getNewStatus();
        validateStatusChange(newStatus);

        order.updateStatus(newStatus);

        return new OrderStatusUpdateResponseDto(order);
    }

    private void validateStatusChange(OrderStatus currentStatus, OrderStatus newStatus) {
        if (!currentStatus.canChangeTo(newStatus)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "잘못된 주문 상태 변경 요청입니다.");
        }
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrders(Long userId, String userRole, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        if ("USER".equals(userRole)) {
            return orderRepository.findByUserId(userId, pageable).map(OrderResponseDto::fromEntity);
        }

        if ("OWNER".equals(userRole)) {
            List<Store> stores = storeService.findStoresByOwnerId(userId);
            List<Long> storeIds = stores.stream().map(Store::getId).collect(Collectors.toList());
            return orderRepository.findByStoreIdIn(storeIds, pageable).map(OrderResponseDto::fromEntity);
        } else {
            throw new CustomException(HttpStatus.FORBIDDEN, "조회 권한이 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrder(Long orderId, Long userId, String userRole) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));

        if ("USER".equals(userRole)) {
            if (!order.getUser().getUserId().equals(userId)) {
                throw new CustomException(HttpStatus.FORBIDDEN, "본인이 주문한 내역만 조회할 수 있습니다.");
            }
        }

        if ("OWNER".equals(userRole)) {
            Store store = storeService.findStoreById(order.getStore().getId());
            if (!store.getOwner().getId().equals(userId)) {
                throw new CustomException(HttpStatus.FORBIDDEN, "본인의 가게에 들어온 주문만 조회할 수 있습니다.");
            }
        }

        return OrderResponseDto.fromEntity(order);
    }

    @Transactional
    public OrderResponseDto cancelOrder(Long orderId, Long userId, String userRole) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));

        if (order.getStatus() == OrderStatus.DELIVERY || order.getStatus() == OrderStatus.COMPLETED) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "배달 중이거나 완료된 주문은 취소할 수 없습니다.");
        }

        if ("USER".equals(userRole)) {
            if (!order.getUser().getId().equals(userId)) {
                throw new CustomException(HttpStatus.FORBIDDEN, "본인의 주문만 취소할 수 있습니다.");
            }
            if (order.getStatus() != OrderStatus.PENDING) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "주문이 접수되면 취소할 수 없습니다.");
            }
        }

        if ("OWNER".equals(userRole)) {
            Store store = storeService.findStoreById(order.getStore().getId());
            if (!store.getOwner().getId().equals(userId)) {
                throw new CustomException(HttpStatus.FORBIDDEN, "본인의 가게 주문만 취소할 수 있습니다.");
            }
        }

        order.updateStatus(OrderStatus.CANCELED);
        return OrderResponseDto.fromEntity(order);
    }



}
