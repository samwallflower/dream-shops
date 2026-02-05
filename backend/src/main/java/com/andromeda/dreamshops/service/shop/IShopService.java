package com.andromeda.dreamshops.service.shop;

import com.andromeda.dreamshops.dto.OrderDto;
import com.andromeda.dreamshops.dto.ShopDto;
import com.andromeda.dreamshops.model.Shop;
import com.andromeda.dreamshops.request.AddShopRequest;
import com.andromeda.dreamshops.request.UpdateShopRequest;

import java.util.List;

public interface IShopService {
    Shop addShop(AddShopRequest shop, Long userId);
    Shop getShopByName(String name);
    Shop getShopById(Long id);
    Shop updateShop(Long id, UpdateShopRequest shop);
    void deleteShopById(Long id) throws Exception;

    void deleteShopImages(Long shopId) throws Exception;

    Shop getShopByUserId(Long userId);// mainly shop owner id
    boolean existsByUserId(Long userId);

    List<OrderDto> getOrdersByShopId(Long shopId);
    List<Shop> getAllShops();
    boolean existsByName(String shopName);
    Long countProductsInShop(Long shopId);
    // DTO conversions
    ShopDto convertToDto(Shop shop);
    List<ShopDto> getConvertedShops(List<Shop> shops);
}
