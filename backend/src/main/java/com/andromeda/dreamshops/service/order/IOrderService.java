package com.andromeda.dreamshops.service.order;

import com.andromeda.dreamshops.dto.OrderDto;
import com.andromeda.dreamshops.model.Order;

import java.util.List;

public interface IOrderService {
    Order placeOrder(Long userId);
    OrderDto getOrderById(Long orderId);

    List<OrderDto> getUserOrders(Long userId);

    OrderDto convertToDto(Order order);
}
