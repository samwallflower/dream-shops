package com.andromeda.dreamshops.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserAccountRequest {
    private String username;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String gender;
    private String dashboardColor;
    private String preferredTheme;
    private String preferredLanguage;
}
