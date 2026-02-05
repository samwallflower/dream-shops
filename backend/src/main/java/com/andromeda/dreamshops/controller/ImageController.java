package com.andromeda.dreamshops.controller;

import com.andromeda.dreamshops.dto.ImageDto;
import com.andromeda.dreamshops.exceptions.ResourceNotFoundException;
import com.andromeda.dreamshops.model.Image;
import com.andromeda.dreamshops.response.ApiResponse;
import com.andromeda.dreamshops.service.image.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/images")
public class ImageController {
    private final IImageService imageService;

    // upload images
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> saveImages(@RequestParam List<MultipartFile> files, @RequestParam Long productId){
        try {
            List<ImageDto> imageDtos = imageService.saveImages(files, productId);
            return ResponseEntity.ok(new ApiResponse("Upload successful", imageDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Upload failed!", e.getMessage()));
        }
    }


    // update image
    @PutMapping("/image/{imageId}/update")
    public ResponseEntity<ApiResponse> updateImage(@PathVariable Long imageId, @RequestParam MultipartFile file){
        try {
            Image image = imageService.getImagebyId(imageId);
            if (image != null ){
                imageService.updateImage(file, imageId);
                return ResponseEntity.ok(new ApiResponse("Update successful", null));
            }
        } catch (ResourceNotFoundException | IOException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Update failed!", INTERNAL_SERVER_ERROR));
    }

    // delete image
    @DeleteMapping("/image/{imageId}/delete")
    public ResponseEntity<ApiResponse> deleteImage(@PathVariable Long imageId){
        try {
            Image image = imageService.getImagebyId(imageId);
            if (image != null ){
                imageService.deleteImageById(imageId);
                return ResponseEntity.ok(new ApiResponse("Delete successful", null));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Update failed!", INTERNAL_SERVER_ERROR));
    }

    // get all images by product id
    @GetMapping("/product/{productId}/images")
    public ResponseEntity<ApiResponse> getImagesByProductId(@PathVariable Long productId) {
        try {
            List<Image> images = imageService.getImagesByProductId(productId);
            List<ImageDto> imageDtos = imageService.convertToDtoList(images);
            return !imageDtos.isEmpty() ?
                    ResponseEntity.ok(new ApiResponse("Images found!", imageDtos)) :
                    ResponseEntity.status(NOT_FOUND).body(new ApiResponse("No images found for product id: " + productId, null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: "+ e.getMessage(), null));
        }
    }

    // get image by id
    @GetMapping("/image/{imageId}")
    public ResponseEntity<ApiResponse> getImageById(@PathVariable Long imageId) {
        try {
            Image image = imageService.getImagebyId(imageId);
            ImageDto imageDto = imageService.convertToDto(image);
            return ResponseEntity.ok(new ApiResponse("Image found!", imageDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " + e.getMessage(), null));
        }
    }
}
