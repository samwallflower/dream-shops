package com.andromeda.dreamshops.service.shop;

import com.andromeda.dreamshops.dto.ShopAccountDto;
import com.andromeda.dreamshops.model.ShopAccount;
import com.andromeda.dreamshops.request.UpdateShopAccountRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IShopAccountService {
    ShopAccountDto getShopAccountByShopId(Long shopId);
    ShopAccountDto updateShopAccount(Long shopId, UpdateShopAccountRequest request);

    ShopAccountDto updateShopLogo(Long shopId, MultipartFile logo);
    ShopAccountDto updateShopBanner(Long shopId, MultipartFile banner);

    void removeShopLogo(Long shopId);
    void removeShopBanner(Long shopId);

    //ShopAccountDto updateBusinessHours(Long shopId, String openingTime, String closingTime);
    ShopAccountDto convertToDto(ShopAccount shopAccount);
    List<ShopAccountDto> convertToDtoList(List<ShopAccount> shopAccounts);

    List<ShopAccountDto> getAllShopAccounts();

    ShopAccountDto getShopAccountById(Long id);

}
