package com.andromeda.dreamshops.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;

    //Many cartItems can have one product . like many people can buy same product at the same time
    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="cart_id")
    private Cart cart;

    public void setTotalPrice() {
        if (unitPrice != null && quantity > 0) {
            this.totalPrice = this.unitPrice.multiply(new BigDecimal(quantity));
        } else {
            this.totalPrice = BigDecimal.ZERO;
        }
    }
}
