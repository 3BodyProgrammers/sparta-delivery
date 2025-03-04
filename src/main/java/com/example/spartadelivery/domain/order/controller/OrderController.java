package com.example.spartadelivery.domain.order.controller;

import com.example.spartadelivery.common.annotation.Auth;
import com.example.spartadelivery.common.dto.AuthUser;
import com.example.spartadelivery.domain.order.dto.request.OrderSaveRequestDto;
import com.example.spartadelivery.domain.order.dto.request.OrderStatusUpdateRequestDto;
import com.example.spartadelivery.domain.order.dto.response.OrderResponseDto;
import com.example.spartadelivery.domain.order.dto.response.OrderSaveResponseDto;
import com.example.spartadelivery.domain.order.dto.response.OrderStatusUpdateResponseDto;
import com.example.spartadelivery.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderSaveResponseDto> save(
            @Auth AuthUser authUser,
            @RequestBody OrderSaveRequestDto request
    ) {
        OrderSaveResponseDto response = orderService.save(authUser.getId(), authUser.getUserRole().name(), request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderStatusUpdateResponseDto> updateOrderStatus(
            @PathVariable Long orderId,
            @Auth AuthUser authUser,
            @RequestBody OrderStatusUpdateRequestDto request
    ) {
        OrderStatusUpdateResponseDto response = orderService.updateOrderStatus(orderId, authUser.getUserRole().name(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> getOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @Auth AuthUser authUser
    ) {
        Page<OrderResponseDto> response = orderService.getOrders(authUser.getId(), authUser.getUserRole().name(), page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(
            @PathVariable Long orderId,
            @Auth AuthUser authUser
    ) {
        OrderResponseDto response = orderService.getOrder(orderId, authUser.getId(), authUser.getUserRole().name());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long orderId,
            @Auth AuthUser authUser
    ) {
        orderService.cancelOrder(orderId, authUser.getId(), authUser.getUserRole().name());
        return ResponseEntity.noContent().build();
    }
}
