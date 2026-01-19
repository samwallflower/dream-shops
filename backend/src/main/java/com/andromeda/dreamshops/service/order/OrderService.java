package com.andromeda.dreamshops.service.order;

import com.andromeda.dreamshops.dto.OrderDto;
import com.andromeda.dreamshops.enums.OrderStatus;
import com.andromeda.dreamshops.exceptions.GeneralException;
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
        Shop shop = cart.getItems()
                .stream()
                .findFirst()
                .map(cartItem -> cartItem
                        .getProduct().getShop())
                .orElseThrow(()->
                        new ResourceNotFoundException("No items in cart found. Add items to cart before placing an order."));
        order.setShop(shop);
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
    public List<OrderDto> getOrdersByShopId(Long shopId) {
        List<Order> orders = orderRepository.findByShopId(shopId);
        return orders.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public OrderDto convertToDto(Order order) {
        return modelMapper.map(order, OrderDto.class);
    }

    // order status flow - PENDING -> CONFIRMED -> PROCESSING -> SHIPPED -> IN_TRANSIT -> DELIVERED
    // From only PENDING either we can go to CONFIRMED or CANCELLED
    // From CONFIRMED we can go to PROCESSING then SHIPPED IN_TRANSIT then DELIVERED
    // once an order is CONFIRMED it can no longer be CANCELLED

    @Override
    public OrderDto updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        OrderStatus currentStatus = order.getOrderStatus();

        if (status.equals(OrderStatus.PENDING)){
            throw new GeneralException("Cannot revert order status back to PENDING.");
        }

        if (currentStatus == OrderStatus.CANCELLED || currentStatus == OrderStatus.DELIVERED) {
            throw new GeneralException("Cannot change status of a " + currentStatus + " order.");
        }

        order.setOrderStatus(status);
        return convertToDto(orderRepository.save(order));
    }

    @Override
    public OrderDto confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getOrderStatus().equals(OrderStatus.PENDING)) {
            throw new GeneralException("Only pending orders can be confirmed. Current status: " + order.getOrderStatus());
        }
        order.setOrderStatus(OrderStatus.CONFIRMED);
        return convertToDto(orderRepository.save(order));
    }

    @Override
    public OrderDto cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        // basically we need to check if the order is already cancelled or completed
        // only if the order is pending we can cancel it
        // if it is anything other than pending we cannot cancel it
        // anything that is not pending means it is either confirmed or cancelled or delivered
        // so only checking for pending status works
        if (!order.getOrderStatus().equals(OrderStatus.PENDING)) {
            throw new GeneralException("Order is already " + order.getOrderStatus() + " and cannot be cancelled at this point.");
        }

        restockInventory(order);
        order.setOrderStatus(OrderStatus.CANCELLED);
        return convertToDto(orderRepository.save(order));
    }

    private void restockInventory(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setInventory(product.getInventory() + item.getQuantity());
            productRepository.save(product);
        }
    }
}
