package com.example.spartadelivery.domain.order.controller;

import com.example.spartadelivery.common.annotation.Auth;
import com.example.spartadelivery.common.annotation.Order;
import com.example.spartadelivery.common.annotation.Owner;
import com.example.spartadelivery.common.annotation.User;
import com.example.spartadelivery.common.dto.AuthUser;
import com.example.spartadelivery.domain.order.dto.request.OrderSaveRequestDto;
import com.example.spartadelivery.domain.order.dto.request.OrderStatusUpdateRequestDto;
import com.example.spartadelivery.domain.order.dto.response.OrderResponseDto;
import com.example.spartadelivery.domain.order.dto.response.OrderSaveResponseDto;
import com.example.spartadelivery.domain.order.dto.response.OrderStatusUpdateResponseDto;
import com.example.spartadelivery.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @User
    @Order
    @PostMapping
    public ResponseEntity<OrderSaveResponseDto> save(
            @Auth AuthUser authUser,
            @Valid @RequestBody OrderSaveRequestDto request
    ) {
        OrderSaveResponseDto response = orderService.save(authUser, request);
        return ResponseEntity.ok(response);
    }

    @Owner
    @Order
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderStatusUpdateResponseDto> updateOrderStatus(
            @PathVariable Long orderId,
            @Auth AuthUser authUser,
            @RequestBody OrderStatusUpdateRequestDto request
    ) {
        OrderStatusUpdateResponseDto response = orderService.updateOrderStatus(orderId, authUser, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDto>> getOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @Auth AuthUser authUser
    ) {
        Page<OrderResponseDto> response = orderService.getOrders(authUser, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(
            @PathVariable Long orderId,
            @Auth AuthUser authUser
    ) {
        OrderResponseDto response = orderService.getOrder(orderId, authUser);
        return ResponseEntity.ok(response);
    }

    @Order
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long orderId,
            @Auth AuthUser authUser
    ) {
        orderService.cancelOrder(orderId, authUser);
        return ResponseEntity.noContent().build();
    }
}
