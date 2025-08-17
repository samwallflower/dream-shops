package com.andromeda.dreamshops.service.order;

import com.andromeda.dreamshops.dto.OrderDto;
import com.andromeda.dreamshops.enums.OrderStatus;
import com.andromeda.dreamshops.exceptions.ResourceNotFoundException;
import com.andromeda.dreamshops.model.*;
import com.andromeda.dreamshops.repository.*;
import com.andromeda.dreamshops.service.cart.CartService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;

    /**
     * The `placeOrder` method is responsible for creating a new order based on the user's cart.
     * It retrieves the cart for the given user, creates an order, and then creates order items from the cart items.
     * Finally, it saves the order and clears the cart.
     *
     * @param userId The ID of the user placing the order.
     * @return The saved Order object.
     */
    @Transactional
    @Override
    public Order placeOrder(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        Order order = createOrder(cart);
        List<OrderItem> orderItems = createOrderItems(order, cart);
        order.setOrderItems(new HashSet<>(orderItems));
        order.setTotalAmount(calculateTotalAmount(orderItems));
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(cart.getId());
        cart.setTotalAmount(cart.getTotalAmount());

        return savedOrder;
    }



    private Order createOrder(Cart cart){
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        return order;
    }


    /**
     * The `createOrderItems` method constructs a list of `OrderItem` objects from the items in a given `Cart` for a specific `Order`. Here’s a step-by-step explanation:
     * 1. It takes an `Order` and a `Cart` as parameters.
     * 2. It retrieves the list of items from the cart.
     * 3. For each cart item:
     *    - Gets the associated `Product`.
     *    - Decreases the product's inventory by the quantity in the cart item.
     *    - Saves the updated product back to the database using `productRepository.save(product)`.
     *    - Creates a new `OrderItem` object, associating it with the order, product, quantity, and unit price.
     * 4. Collects all the created `OrderItem` objects into a list and returns it.
     * This method ensures that product inventory is updated and that each cart item is converted into an order item for the order.
     */

    private List<OrderItem> createOrderItems(Order order, Cart cart){
        return cart.getItems()
                .stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();
                    product.setInventory(product.getInventory() - cartItem.getQuantity());
                    productRepository.save(product);
                    return new OrderItem(
                            order,
                            product,
                            cartItem.getQuantity(),
                            cartItem.getUnitPrice());
                }).toList();
    }




    private BigDecimal calculateTotalAmount(List<OrderItem> orderItems){
        return orderItems
                .stream()
                .map(item -> item.getPrice()
                        .multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }




    @Override
    public OrderDto getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::convertToDto)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    @Override
    public List<OrderDto> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        return orders.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public OrderDto convertToDto(Order order) {
        return modelMapper.map(order, OrderDto.class);
    }
}
