package com.andromeda.dreamshops.model;

import com.andromeda.dreamshops.enums.AddressType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String street;
    private String houseNumber;
    private String floor;
    private String city;
    private String state;
    private String zip;
    private String country;

    private boolean isDefault = false;

    @Enumerated(EnumType.STRING)
    private AddressType addressType; // SHIPPING , BILLING, BOTH

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_account_id", referencedColumnName = "id")
    private UserAccount userAccount;
}
