package com.andromeda.dreamshops.service.image;

import com.andromeda.dreamshops.dto.ImageDto;
import com.andromeda.dreamshops.exceptions.ResourceNotFoundException;
import com.andromeda.dreamshops.model.Image;
import com.andromeda.dreamshops.model.Product;
import com.andromeda.dreamshops.repository.ImageRepository;
import com.andromeda.dreamshops.service.cloudprovider.ICloudProviderService;
import com.andromeda.dreamshops.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService{
    private final ImageRepository imageRepository;
    private final IProductService productService;
    private final ICloudProviderService cloudProviderService;


    @Override
    public Image getImagebyId(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("No image found with id: " + id));
    }

    @Override
    public void deleteImageById(Long id) throws IOException {
        Image image = getImagebyId(id);
        cloudProviderService.deleteImageByPublicId(image.getPublicId());
        imageRepository.deleteById(id);
    }

    @Override
    public List<ImageDto> saveImages(List<MultipartFile> files, Long productId) throws IOException {
        Product product = productService.getProductById(productId);

        if(product.getShop() == null){
            throw new ResourceNotFoundException("Cannot add images to a product that is not associated with any shop productId: " + productId);
        }
        Long shopId = product.getShop().getId();

        List<ImageDto> savedImageDto = new ArrayList<>();

        // for every file in files
        for (MultipartFile file : files) {
            String folder = buildProductImageFolder(shopId);

            String publicId = buildProductImagePublicId(productId, file.getOriginalFilename());
            Map uploadResult = cloudProviderService.uploadImage(file, folder, publicId, null);

            String imageUrl = (String) uploadResult.get("secure_url");
            String actualPublicId = (String) uploadResult.get("public_id");



            // saving metadata to database
            Image image = new Image();
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImageUrl(imageUrl);
            image.setPublicId(actualPublicId);
            image.setProduct(product);

            Image savedImage = imageRepository.save(image);
            savedImageDto.add(convertToDto(savedImage));

        }
        return savedImageDto;
    }

    @Override
    public void updateImage(MultipartFile file, Long imageId) throws IOException{
        Image existingImage = getImagebyId(imageId);
        Product product = existingImage.getProduct();
        if(product == null || product.getShop() == null){
            throw new ResourceNotFoundException("Cannot update image that is not associated with any product or shop. imageId: " + imageId);
        }

        String folder = buildProductImageFolder(product.getShop().getId());

        // Update image in cloud storage

        Map updatedResult = cloudProviderService.updateImageByPublicId(
                existingImage.getPublicId(), // old image public id
                file,                       // new file
                folder,                     // folder
                null
        );
        String updatedImageUrl = (String) updatedResult.get("secure_url");
        existingImage.setFileName(file.getOriginalFilename());
        existingImage.setFileType(file.getContentType());
        existingImage.setImageUrl(updatedImageUrl);
        existingImage.setPublicId(updatedResult.get("public_id").toString());
        imageRepository.save(existingImage);
    }

    @Override
    public List<Image> getImagesByProductId(Long productId) {
        return imageRepository.findByProductId(productId);
    }

    @Override
    public ImageDto convertToDto(Image image) {
        ImageDto imageDto = new ImageDto();
        imageDto.setId(image.getId());
        imageDto.setFileName(image.getFileName());
        imageDto.setImageUrl(image.getImageUrl());
        return imageDto;
    }

    @Override
    public List<ImageDto> convertToDtoList(List<Image> images) {
        return images.stream()
                .map(this::convertToDto)
                .toList();
    }

    // Helper method to build product image folder path
    // e.g., dreamshops/shops/shop-1/products
    private String buildProductImageFolder(Long shopId){
        return String.format("dreamshops/shops/shop-%d/products", shopId);
    }

    // product-1-ipone-13-pro-max
    private String buildProductImagePublicId(Long productId, String fileName){
        String name = fileName.contains(".") ? fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
        String cleanName = name
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-") // replace non-alphanumeric with hyphens
                .replaceAll("^-+|-+$", "");    // trim leading/trailing hyphens

        return String.format("product-%d-%s",productId, cleanName);
    }
}
