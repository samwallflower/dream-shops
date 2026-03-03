package com.andromeda.dreamshops.controller;

import com.andromeda.dreamshops.dto.ShopAccountDto;
import com.andromeda.dreamshops.exceptions.ResourceNotFoundException;
import com.andromeda.dreamshops.exceptions.ResourceProcessingException;
import com.andromeda.dreamshops.request.UpdateShopAccountRequest;
import com.andromeda.dreamshops.response.ApiResponse;
import com.andromeda.dreamshops.service.shop.IShopAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/shopAccounts")
public class ShopAccountController {
    private final IShopAccountService shopAccountService;

    // get shop account by id of the shop account
    @GetMapping("/shop-account/{id}")
    public ResponseEntity<ApiResponse> getShopAccountById(@PathVariable Long id){
        try {
            ShopAccountDto shopAccountDto = shopAccountService.getShopAccountById(id);
            return ResponseEntity.ok(new ApiResponse("Shop Account found", shopAccountDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Shop Account not found for id "+ id, e));
        }
    }

    // get shop account by shop id
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<ApiResponse> getShopAccountByShopId(@PathVariable Long shopId){
        try{
            ShopAccountDto shopAccountDto = shopAccountService.getShopAccountByShopId(shopId);
            return ResponseEntity.ok(new ApiResponse("Shop Account found", shopAccountDto));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Shop Account not found for shop id "+ shopId, e));
        }
    }

    // get all shop accounts
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllShopAccounts(){
        List<ShopAccountDto> shopAccounts = shopAccountService.getAllShopAccounts();
        return !shopAccounts.isEmpty()?
                ResponseEntity.ok(new ApiResponse("Shop Account found", shopAccounts)) :
                ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Shop Accounts not found", null));
    }

    // update shop account
    @PutMapping("/shop-account/shop/{shopId}")
    public ResponseEntity<ApiResponse>  updateShopAccount(@PathVariable Long shopId, @RequestBody UpdateShopAccountRequest request){
        try {
            ShopAccountDto shopAccountDto = shopAccountService.updateShopAccount(shopId, request);
            return ResponseEntity.ok(new ApiResponse("Shop Account updated", shopAccountDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Shop Account not found for shop id "+ shopId, e));
        }catch (Exception e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error updating shop account : "+ e.getMessage(), null));
        }
    }

    // update shop logo
    @PutMapping("/shop-account/shop/{shopId}/logo")
    public ResponseEntity<ApiResponse> updateShopLogo(@PathVariable Long shopId, @RequestParam("logo") MultipartFile logo) {
        try {
            ShopAccountDto shopAccountDto = shopAccountService.updateShopLogo(shopId, logo);
            return ResponseEntity.ok(new ApiResponse("Shop logo updated", shopAccountDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Shop Account not found for shop id " + shopId, e));
        }catch(ResourceProcessingException e){
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error processing shop logo : " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error updating shop logo : " + e.getMessage(), null));
        }
    }

    // update shop banner
    @PutMapping("/shop-account/shop/{shopId}/banner")
    public ResponseEntity<ApiResponse> updateShopBanner(@PathVariable Long shopId, @RequestParam("banner") MultipartFile banner) {
        try {
            ShopAccountDto shopAccountDto = shopAccountService.updateShopBanner(shopId, banner);
            return ResponseEntity.ok(new ApiResponse("Shop banner updated", shopAccountDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Shop Account not found for shop id " + shopId, e));
        } catch (ResourceProcessingException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error processing shop banner : " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error updating shop banner : " + e.getMessage(), null));
        }
    }

    // remove shop logo
    @DeleteMapping("/shop-account/shop/{shopId}/logo")
    public ResponseEntity<ApiResponse> removeShopLogo(@PathVariable Long shopId) {
        try {
            shopAccountService.removeShopLogo(shopId);
            return ResponseEntity.ok(new ApiResponse("Shop logo removed", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Shop Account not found for shop id " + shopId, e));
        } catch (ResourceProcessingException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error processing shop logo removal : " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error removing shop logo : " + e.getMessage(), null));
        }
    }

    // remove shop banner
    @DeleteMapping("/shop-account/shop/{shopId}/banner")
    public ResponseEntity<ApiResponse> removeShopBanner(@PathVariable Long shopId) {
        try {
            shopAccountService.removeShopBanner(shopId);
            return ResponseEntity.ok(new ApiResponse("Shop banner removed", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Shop Account not found for shop id " + shopId, e));
        } catch (ResourceProcessingException e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error processing shop banner removal : " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error removing shop banner : " + e.getMessage(), null));
        }
    }

}
