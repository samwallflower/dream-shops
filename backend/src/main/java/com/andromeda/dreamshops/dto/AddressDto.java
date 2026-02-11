package com.andromeda.dreamshops.dto;

import com.andromeda.dreamshops.enums.AddressType;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link com.andromeda.dreamshops.model.Address}
 */
@Data
public class AddressDto implements Serializable {
    private Long id;
    private String street;
    private String houseNumber;
    private String floor;
    private String city;
    private String state;
    private String zip;
    private String country;
    private boolean isDefault;
    private String addressType;
}