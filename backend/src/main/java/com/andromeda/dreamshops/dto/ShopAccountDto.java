package com.andromeda.dreamshops.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class ShopAccountDto {
    private Long id;
    private String logoUrl;
    private String bannerUrl;
    private String dashboardColor;
    private String slug;
    private LocalTime openingHours;
    private LocalTime closingHours;
    private String announcement;
    private BigDecimal averageRate;
    private String returnPolicy;
    private String termsOfService;
    private String privacyPolicy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
