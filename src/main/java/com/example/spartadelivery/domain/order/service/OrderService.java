package com.example.spartadelivery.domain.order.service;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.order.dto.request.OrderSaveRequestDto;
import com.example.spartadelivery.domain.order.dto.response.OrderSaveResponseDto;
import com.example.spartadelivery.domain.order.enums.OrderStatus;
import com.example.spartadelivery.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final StoreService storeService;
    private final MenuService menuService;

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

        Order order = Order.toEntity(request.getStoreId(), request.getMenuName(), request.getPrice(), OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);
        return OrderSaveResponseDto.of(savedOrder);
    }

}
