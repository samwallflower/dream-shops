package com.andromeda.dreamshops.service.image;

import com.andromeda.dreamshops.dto.ImageDto;
import com.andromeda.dreamshops.exceptions.ResourceNotFoundException;
import com.andromeda.dreamshops.model.Image;
import com.andromeda.dreamshops.model.Product;
import com.andromeda.dreamshops.repository.ImageRepository;
import com.andromeda.dreamshops.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService{
    private final ImageRepository imageRepository;
    private final IProductService productService;


    @Override
    public Image getImagebyId(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("No image found with id: " + id));
    }

    @Override
    public void deleteImageById(Long id) {
        imageRepository.findById(id)
                .ifPresentOrElse(imageRepository :: delete, ()->{
                    throw new ResourceNotFoundException("No image found with id: " + id);
                });
    }

    @Override
    public List<ImageDto> saveImages(List<MultipartFile> files, Long productId) {
        Product product = productService.getProductById(productId);
        List<ImageDto> savedImageDto = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProduct(product);

                String buildDownloadUrl = "/api/v1/images/image/download/";
                //String downloadUrl = buildDownloadUrl + image.getId();
                //image.setDownloadUrl(downloadUrl);

                Image savedImage =  imageRepository.save(image);

                savedImage.setDownloadUrl(buildDownloadUrl + savedImage.getId());
                imageRepository.save(savedImage);

                ImageDto imageDto = convertToDto(savedImage);
                savedImageDto.add(imageDto);

            } catch (SQLException | IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return savedImageDto;
    }

    @Override
    public void updateImage(MultipartFile file, Long imageId) {
        try {
            Image image = getImagebyId(imageId);
            image.setFileName(file.getOriginalFilename());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
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
        imageDto.setDownloadUrl(image.getDownloadUrl());
        return imageDto;
    }

    @Override
    public List<ImageDto> convertToDtoList(List<Image> images) {
        return images.stream()
                .map(this::convertToDto)
                .toList();
    }


}
