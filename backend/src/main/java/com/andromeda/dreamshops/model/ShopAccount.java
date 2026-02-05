package com.andromeda.dreamshops.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ShopAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String logoUrl;
    private String bannerUrl;
    private String dashboardColor = "#FFFFFF"; // default white

    @Column(unique = true)
    private String slug;

    // business hours
    private LocalTime openingHours;
    private LocalTime closingHours;

    private String announcement;
    private BigDecimal averageRate = BigDecimal.valueOf(5.0);


    //policies maybe URL or text
    @Column(length = 5000)
    private String returnPolicy;

    @Column(length = 5000)
    private String termsOfService;

    @Column(length = 5000)
    private String privacyPolicy;


    @OneToOne
    @JoinColumn(name = "shop_id", referencedColumnName = "id")
    private Shop shop;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
