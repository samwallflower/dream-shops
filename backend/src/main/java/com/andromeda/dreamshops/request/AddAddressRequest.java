package com.andromeda.dreamshops.request;

import com.andromeda.dreamshops.enums.AddressType;
import lombok.Data;

@Data
public class AddAddressRequest {
    private String street;
    private String houseNumber;
    private String floor;
    private String city;
    private String state;
    private String zip;
    private String country;
    private boolean isDefault;
    private String addressType; // SHIPPING , BILLING , BOTH

}