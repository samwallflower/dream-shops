package com.andromeda.dreamshops.service.cart;

import com.andromeda.dreamshops.model.CartItem;

public interface ICartItemService {
    void addItemToCart(Long cartId, Long productId, int quantity);
    void removeItemFromCart(Long cartId, Long productId);
    void updateItemQuantity(Long cartId, Long productId, int quantity);

    CartItem getCartItemById(Long cartId, Long productId);
}
