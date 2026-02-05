package com.andromeda.dreamshops.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String contactNumber;
    private String contactEmail;
    private String description;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "shop_id")
    private List<Product> products;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User shopOwner;

    @OneToMany(mappedBy = "shop")
    private List<Order> orders;

    @OneToOne(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private ShopAccount shopAccount;


    public Shop(String name) {
        this.name = name;
    }
    public Shop(String name,
                String address,
                String contactNumber,
                String contactEmail,
                String description) {
        this.name = name;
        this.address = address;
        this.contactNumber = contactNumber;
        this.contactEmail = contactEmail;
        this.description = description;
    }
}
