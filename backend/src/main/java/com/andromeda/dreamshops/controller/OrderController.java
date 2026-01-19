package com.andromeda.dreamshops.controller;

import com.andromeda.dreamshops.dto.OrderDto;
import com.andromeda.dreamshops.enums.OrderStatus;
import com.andromeda.dreamshops.exceptions.GeneralException;
import com.andromeda.dreamshops.exceptions.ResourceNotFoundException;
import com.andromeda.dreamshops.model.Order;
import com.andromeda.dreamshops.response.ApiResponse;
import com.andromeda.dreamshops.service.order.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderController {
    private final IOrderService orderService;

    // Create Order
    @PostMapping("/order/create")
    public ResponseEntity<ApiResponse> createOrder(@RequestParam Long userId){
        try {
            Order order = orderService.placeOrder(userId);
            OrderDto orderDto = orderService.convertToDto(order);
            return ResponseEntity.ok(new ApiResponse("Order placed successfully", orderDto));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("Failed to place order: " + e.getMessage(), null));
        }
    }

    // Get Order by ID
    @GetMapping("/{orderId}/order")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable Long orderId) {
        try {
            OrderDto order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(new ApiResponse("Order retrieved successfully", order));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    // Get Orders for a User
    @GetMapping("user/{userId}/orders")
    public ResponseEntity<ApiResponse> getUserOrders(@PathVariable Long userId) {
        try {
            List<OrderDto> orders = orderService.getUserOrders(userId);
            return ResponseEntity.ok(new ApiResponse("Orders retrieved successfully", orders));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    // Update Order Status
    @PutMapping("/{orderId}/update-status")
    public ResponseEntity<ApiResponse> updateOrderStatus(@PathVariable Long orderId,
                                                         @RequestParam OrderStatus status) {
        try {
            OrderDto updatedOrder = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(new ApiResponse("Order status updated successfully", updatedOrder));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    // cancel order
    @PutMapping("/{orderId}/cancel" )
    public ResponseEntity<ApiResponse> cancelOrder(@PathVariable Long orderId) {
        try {
            OrderDto canceledOrder = orderService.cancelOrder(orderId);
            return ResponseEntity.ok(new ApiResponse("Order canceled successfully", canceledOrder));
        } catch (GeneralException e) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }

    // confirm order
    @PutMapping("/{orderId}/confirm" )
    public ResponseEntity<ApiResponse> confirmOrder(@PathVariable Long orderId) {
        try {
            OrderDto confirmedOrder = orderService.confirmOrder(orderId);
            return ResponseEntity.ok(new ApiResponse("Order confirmed successfully", confirmedOrder));
        } catch (GeneralException e) {
            return ResponseEntity.status(BAD_REQUEST)
                    .body(new ApiResponse(e.getMessage(), null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), null));
        }
    }
}
