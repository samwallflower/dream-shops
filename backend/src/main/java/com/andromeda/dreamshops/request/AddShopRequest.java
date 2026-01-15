package com.andromeda.dreamshops.request;

import lombok.Data;

@Data
public class AddShopRequest {
    private String name; // unique name for the shop
    private String address;
    private String contactNumber; // these will be shown for contacting the shop
    private String contactEmail; // these will be shown for contacting the shop
    private String description;
}
