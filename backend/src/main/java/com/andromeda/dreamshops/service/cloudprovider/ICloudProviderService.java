package com.andromeda.dreamshops.service.cloudprovider;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface ICloudProviderService {
    Map uploadImage(MultipartFile file, String folder, String publicId, String transformation) throws IOException;
    Map uploadImage(MultipartFile file, String folder) throws IOException;
    Map updateImageByImageUrl(String imageUrl, MultipartFile newFile, String folder, String publicId, String transformation) throws IOException;
    Map updateImageByPublicId(String publicId, MultipartFile newFile, String folder, String transformation) throws IOException;
    void deleteImageByImageURl(String imageUrl) throws IOException;
    void deleteImageByPublicId(String publicId) throws IOException;
    void deleteFolder(String folder) throws Exception;
}
