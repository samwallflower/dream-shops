package com.andromeda.dreamshops.service.cart;

import com.andromeda.dreamshops.dto.CartDto;
import com.andromeda.dreamshops.model.Cart;
import com.andromeda.dreamshops.model.User;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCart(Long id);
    void clearCart(Long id);
    BigDecimal getTotalPrice(Long id);

    Cart initializeNewCart(User user);

    Cart getCartByUserId(Long userId);

    CartDto convertToCartDto(Cart cart);
}
