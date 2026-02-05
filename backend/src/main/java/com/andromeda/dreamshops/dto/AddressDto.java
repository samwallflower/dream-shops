package com.andromeda.dreamshops.dto;

import com.andromeda.dreamshops.enums.AddressType;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link com.andromeda.dreamshops.model.Address}
 */
@Data
public class AddressDto implements Serializable {
    Long id;
    String street;
    String houseNumber;
    String floor;
    String city;
    String state;
    String zip;
    String country;
    boolean isDefault;
    AddressType addressType;
}