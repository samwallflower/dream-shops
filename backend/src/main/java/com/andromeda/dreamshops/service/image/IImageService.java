package com.andromeda.dreamshops.service.image;

import com.andromeda.dreamshops.dto.ImageDto;
import com.andromeda.dreamshops.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface IImageService {
    Image getImagebyId(Long id);
    void deleteImageById(Long id);
    List<ImageDto> saveImages(List<MultipartFile> files, Long productId);
    void updateImage(MultipartFile file, Long imageId);

}
