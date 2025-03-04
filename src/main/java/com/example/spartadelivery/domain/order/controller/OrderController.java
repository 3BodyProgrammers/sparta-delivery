package com.example.spartadelivery.domain.order.controller;

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
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/stores/{storeId}/menus/{menuId}/orders")
    public ResponseEntity<OrderSaveResponseDto> save(
            @RequestHeader("User_Id") Long userId,
            @RequestHeader("User_Role") String userRole,
            @RequestBody OrderSaveRequestDto request
    ) {
        OrderSaveResponseDto response = orderService.save(userId, userRole, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderStatusUpdateResponseDto> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestHeader("User_Role") String userRole,
            @RequestBody OrderStatusUpdateRequestDto request) {

        OrderStatusUpdateResponseDto response = orderService.updateOrderStatus(orderId, userRole, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponseDto>> getOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("User_Id") Long userId,
            @RequestHeader("User_Role") String userRole) {

        Page<OrderResponseDto> response = orderService.getOrders(userId, userRole, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(
            @PathVariable Long orderId,
            @RequestHeader("User_Id") Long userId,
            @RequestHeader("User_Role") String userRole) {

        OrderResponseDto response = orderService.getOrder(orderId, userId, userRole);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/orders/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long orderId,
            @RequestHeader("User_Id") Long userId,
            @RequestHeader("User_Role") String userRole) {

        orderService.cancelOrder(orderId, userId, userRole);
        return ResponseEntity.noContent().build();
    }

}
