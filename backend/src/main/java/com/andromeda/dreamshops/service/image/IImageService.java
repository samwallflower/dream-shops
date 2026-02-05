package com.andromeda.dreamshops.service.image;

import com.andromeda.dreamshops.dto.ImageDto;
import com.andromeda.dreamshops.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface IImageService {
    Image getImagebyId(Long id);
    void deleteImageById(Long id) throws IOException;
    List<ImageDto> saveImages(List<MultipartFile> files, Long productId) throws IOException;
    void updateImage(MultipartFile file, Long imageId) throws IOException;
    List<Image> getImagesByProductId(Long productId);
    ImageDto convertToDto(Image image);
    List<ImageDto> convertToDtoList(List<Image> images);

}
