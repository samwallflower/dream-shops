package com.andromeda.dreamshops.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalTime;

@Data
public class UpdateShopAccountRequest {
    private String dashboardColor;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime openingHours;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime closingHours;
    private String announcement;
    private String returnPolicy;
    private String termsOfService;
    private String privacyPolicy;
}