package com.andromeda.dreamshops.service.shop;

import com.andromeda.dreamshops.dto.ShopAccountDto;
import com.andromeda.dreamshops.exceptions.ResourceNotFoundException;
import com.andromeda.dreamshops.exceptions.ResourceProcessingException;
import com.andromeda.dreamshops.model.ShopAccount;
import com.andromeda.dreamshops.repository.ShopAccountRepository;
import com.andromeda.dreamshops.request.UpdateShopAccountRequest;
import com.andromeda.dreamshops.service.cloudprovider.ICloudProviderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopAccountService implements IShopAccountService{
    private final ShopAccountRepository shopAccountRepository;
    private final ICloudProviderService cloudProviderService;
    private final ModelMapper modelMapper;

    @Override
    public ShopAccountDto getShopAccountByShopId(Long shopId) {
        return shopAccountRepository.findByShopId(shopId)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Shop account not found for shop ID: " + shopId));
    }

    @Override
    public ShopAccountDto updateShopAccount(Long shopId, UpdateShopAccountRequest request) {
        ShopAccount shopAccount = shopAccountRepository.findByShopId(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop account not found for shop ID: " + shopId));

        Optional.ofNullable(request.getAnnouncement()).ifPresent(shopAccount::setAnnouncement);
        Optional.ofNullable(request.getOpeningHours()).ifPresent(shopAccount::setOpeningHours);
        Optional.ofNullable(request.getClosingHours()).ifPresent(shopAccount::setClosingHours);
        Optional.ofNullable(request.getDashboardColor()).ifPresent(shopAccount::setDashboardColor);
        Optional.ofNullable(request.getTermsOfService()).ifPresent(shopAccount::setTermsOfService);
        Optional.ofNullable(request.getPrivacyPolicy()).ifPresent(shopAccount::setPrivacyPolicy);
        Optional.ofNullable(request.getReturnPolicy()).ifPresent(shopAccount::setReturnPolicy);

        return convertToDto(shopAccountRepository.save(shopAccount));
    }

    @Override
    @Transactional
    public String updateShopLogo(Long shopId, MultipartFile logo) {
        ShopAccount shopAccount = shopAccountRepository.findByShopId(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop account not found for shop ID: " + shopId));

        String folderPath = "dreamshops/shops/shop-" + shopId + "/logo";
        String publicId = "logo";

        try{
            Map uploadResult = cloudProviderService.updateImageByPublicId(
                    publicId,
                    logo,
                    folderPath,
                    "c_fill,h_200,w_200");
            String logoUrl = (String) uploadResult.get("secure_url");
            shopAccount.setLogoUrl(logoUrl);
            return shopAccountRepository.save(shopAccount).getLogoUrl();
        } catch (IOException e) {
            throw new ResourceProcessingException("Failed to upload logo: " + e.getMessage());
        }
    }

    @Override
    public String updateShopBanner(Long shopId, MultipartFile banner) {
            ShopAccount shopAccount = shopAccountRepository.findByShopId(shopId)
                    .orElseThrow(() -> new ResourceNotFoundException("Shop account not found for shop ID: " + shopId));

            String folderPath = "dreamshops/shops/shop-" + shopId + "/banner";
            String publicId = "banner";

            try{
                Map uploadResult = cloudProviderService.updateImageByPublicId(
                        publicId,
                        banner,
                        folderPath,
                        "c_fill,h_400,w_1200");
                String bannerUrl = (String) uploadResult.get("secure_url");
                shopAccount.setBannerUrl(bannerUrl);
                return shopAccountRepository.save(shopAccount).getBannerUrl();
            } catch (IOException e) {
                throw new ResourceProcessingException("Failed to upload banner: " + e.getMessage());
            }
    }

    @Override
    public void removeShopLogo(Long shopId) {
        ShopAccount shopAccount = shopAccountRepository.findByShopId(shopId)
                .orElseThrow(()-> new ResourceNotFoundException("Shop account not found for shop ID: " + shopId));

        if(shopAccount.getLogoUrl() == null|| shopAccount.getBannerUrl().isEmpty()) {
            return;
        }
        try{
            cloudProviderService.deleteImageByImageURl(shopAccount.getLogoUrl());
            shopAccount.setLogoUrl(null);
            shopAccountRepository.save(shopAccount);
        } catch (IOException e) {
            throw new ResourceProcessingException("Failed to delete logo: " + e.getMessage());
        }

    }

    @Override
    public void removeShopBanner(Long shopId) {
        ShopAccount shopAccount = shopAccountRepository.findByShopId(shopId)
                .orElseThrow(()-> new ResourceNotFoundException("Shop account not found for shop ID: " + shopId));
        if(shopAccount.getBannerUrl() == null || shopAccount.getBannerUrl().isEmpty()) {return;}
        try{
            cloudProviderService.deleteImageByImageURl(shopAccount.getBannerUrl());
            shopAccount.setBannerUrl(null);
            shopAccountRepository.save(shopAccount);
        } catch (IOException e) {
            throw new ResourceProcessingException("Failed to delete banner: " + e.getMessage());
        }

    }

    @Override
    public ShopAccountDto convertToDto(ShopAccount shopAccount) {
        return modelMapper.map(shopAccount, ShopAccountDto.class);
    }

    @Override
    public List<ShopAccountDto> convertToDtoList(List<ShopAccount> shopAccounts) {
        return shopAccounts.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<ShopAccountDto> getAllShopAccounts() {
        return shopAccountRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public ShopAccountDto getShopAccountById(Long id) {
        return shopAccountRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(()-> new ResourceNotFoundException("Shop account not found for ID: " + id));
    }
}
