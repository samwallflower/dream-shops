package com.andromeda.dreamshops.dto;

import com.andromeda.dreamshops.model.Order;
import lombok.Data;

import java.util.List;

@Data
public class ShopDto {
    private Long id;
    private String name;
    private String address;
    private String contactNumber; // these will be shown for contacting the shop
    private String contactEmail; // these will be shown for contacting the shop
    private String description;
    private List<ProductDto> products;
    private List<OrderDto> orders; // List of orders associated with the shop
}
