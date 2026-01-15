package com.andromeda.dreamshops.request;

import lombok.Data;

@Data
public class UpdateShopRequest {
    public String name;
    public String address;
    public String contactNumber;
    public String contactEmail;
    public String description;
}
