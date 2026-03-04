package com.andromeda.dreamshops.dto;

import java.util.List;

public class UserAccountDto {
    private Long id;
    private String username;
    private String profilePictureUrl;
    private String phoneNumber;
    private String dateOfBirth;
    private String gender;
    private String dashboardColor;
    private String preferredTheme;
    private String preferredLanguage;
    private String accountStatus;
    private List<AddressDto> savedAddresses;
}
