package com.andromeda.dreamshops.controller;

import com.andromeda.dreamshops.dto.OrderDto;
import com.andromeda.dreamshops.dto.ShopDto;
import com.andromeda.dreamshops.exceptions.AlreadyExistsException;
import com.andromeda.dreamshops.exceptions.ResourceNotFoundException;
import com.andromeda.dreamshops.model.Shop;
import com.andromeda.dreamshops.request.AddShopRequest;
import com.andromeda.dreamshops.request.UpdateShopRequest;
import com.andromeda.dreamshops.response.ApiResponse;
import com.andromeda.dreamshops.service.shop.IShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/shops")
public class ShopController {
    private final IShopService shopService;

    //get all shops
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllShops() {
        List<Shop> shops = shopService.getAllShops();
        List<ShopDto> convertedShops = shopService.getConvertedShops(shops);
        return !convertedShops.isEmpty() ?
                ResponseEntity.ok(new ApiResponse("Shops retrieved successfully", convertedShops))
                : ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No shops found", null));
    }

    //get shop by id
    @GetMapping("/shop/{shopId}/shop")
    public ResponseEntity<ApiResponse> getShopById(@PathVariable Long shopId) {
        try {
            Shop shop = shopService.getShopById(shopId);
            ShopDto shopDto = shopService.convertToDto(shop);
            return ResponseEntity.ok(new ApiResponse("Shop retrieved successfully", shopDto));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    //get shop by name
    @GetMapping("/shop/by-shopName")
    public ResponseEntity<ApiResponse> getShopByName(@RequestParam String shopName){
        try {
            Shop shop = shopService.getShopByName(shopName);
            ShopDto shopDto = shopService.convertToDto(shop);
            return shop != null ?
                    ResponseEntity.ok(new ApiResponse("Shop retrieved successfully", shopDto)) :
                    ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No shop found with the given name", null));
        } catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    //get shop by user id (shop owner id)
    @GetMapping("/shop/user/{userId}/shop")
    public ResponseEntity<ApiResponse> getShopByUserId(@PathVariable Long userId){
        try {
            Shop shop = shopService.getShopByUserId(userId);
            ShopDto shopDto = shopService.convertToDto(shop);
            return shop != null ?
                    ResponseEntity.ok(new ApiResponse("Shop retrieved successfully", shopDto)) :
                    ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No shop found for the given user id", null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    //count products in shop
    @GetMapping("/shop/{shopId}/products/count")
    public ResponseEntity<ApiResponse> countProductsInShop(@PathVariable Long shopId) {
        try {
            Long count = shopService.countProductsInShop(shopId);
            return ResponseEntity.ok(new ApiResponse("Product count retrieved successfully", count));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    //add a shop
    @PostMapping("/add/{userId}/shop")
    public ResponseEntity<ApiResponse> addShop(@RequestBody AddShopRequest request, @PathVariable Long userId) {
        try {
            Shop shop = shopService.addShop(request, userId);
            ShopDto shopDto = shopService.convertToDto(shop);
            return ResponseEntity.ok(new ApiResponse("Shop added successfully", shopDto));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    // update shop details
    @PutMapping("/shop/{shopId}/update")
    public ResponseEntity<ApiResponse> updateShop(@PathVariable Long shopId, @RequestBody UpdateShopRequest request) {
        try {
            Shop updatedShop = shopService.updateShop(shopId, request);
            ShopDto shopDto = shopService.convertToDto(updatedShop);
            return ResponseEntity.ok(new ApiResponse("Shop updated successfully", shopDto));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    //delete shop by id
    @DeleteMapping("/shop/{shopId}/delete")
    public ResponseEntity<ApiResponse> deleteShop(@PathVariable Long shopId) {
        try {
            shopService.deleteShopById(shopId);
            return ResponseEntity.ok(new ApiResponse("Shop deleted successfully", shopId));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    // get orders by shop id
    @GetMapping("/shop/{shopId}/orders")
    public ResponseEntity<ApiResponse> getOrdersByShopId(@PathVariable Long shopId) {
        try {
            List<OrderDto> orders = shopService.getOrdersByShopId(shopId);
            return !orders.isEmpty() ?
                    ResponseEntity.ok(new ApiResponse("Orders retrieved successfully", orders)) :
                    ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No orders found for the given shopId", null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

}
