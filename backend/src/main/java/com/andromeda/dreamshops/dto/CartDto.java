package com.andromeda.dreamshops.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class CartDto {
    private Long cartId;
    private Set<CartItemDto> cartItems;
    private BigDecimal totalAmount;
}
