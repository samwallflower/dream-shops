package com.andromeda.dreamshops.service.cloudprovider;

import com.andromeda.dreamshops.exceptions.ResourceNotFoundException;
import com.andromeda.dreamshops.exceptions.ResourceProcessingException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudProviderService implements ICloudProviderService {
    private final Cloudinary cloudinary;


    @Override
    public Map uploadImage(MultipartFile file,
                           String folder,
                           String publicId,
                           String transformation) throws IOException {
        if (file == null || file.isEmpty()){
            throw new ResourceNotFoundException("Image file cannot be null or empty");
        }
        Map<String,Object> params = ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "auto"
        );
        if(publicId!= null && !publicId.isEmpty()){
            params.put("public_id", publicId);
        }
        if (transformation != null && !transformation.isEmpty()) {
            params.put("transformation", transformation);
        }
        return cloudinary.uploader().upload(file.getBytes(), params);
    }

    @Override
    public Map uploadImage(MultipartFile file, String folder) throws IOException {
        return uploadImage(file, folder, null,null);
    }

    @Override
    public Map updateImageByImageUrl(String imageUrl,
                                     MultipartFile newFile,
                                     String folder,
                                     String publicId,
                                     String transformation) throws IOException {
        if(newFile==null || newFile.isEmpty()){
            throw new ResourceNotFoundException("New image file cannot be null or empty");
        }
        if(imageUrl==null || imageUrl.isEmpty()){
            throw new ResourceNotFoundException("Image URL cannot be null or empty");
        }
        deleteImageByImageURl(imageUrl);
        return uploadImage(newFile, folder, publicId, transformation);
    }

    @Override
    public Map updateImageByPublicId(String publicId,
                                     MultipartFile newFile,
                                     String folder,
                                     String transformation) throws IOException {
        if(newFile==null || newFile.isEmpty()){
            throw new ResourceNotFoundException("New image file cannot be null or empty");
        }
        Map<String, Object> params = ObjectUtils.asMap(
                "folder", folder,
                "public_id", publicId,
                "overwrite", true,
                "resource_type", "auto"
        );
        if(transformation != null && !transformation.isEmpty()){
            params.put("transformation", transformation);
        }
        return cloudinary.uploader().upload(newFile.getBytes(), params);
    }

    @Override
    public void deleteImageByImageURl(String imageUrl) throws IOException{
        String publicId = extractPublicId(imageUrl);
        cloudinary.uploader().destroy(publicId,ObjectUtils.emptyMap());

    }

    @Override
    public void deleteImageByPublicId(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId,ObjectUtils.emptyMap());
    }

    @Override
    public void deleteFolder(String folder) throws Exception {
        cloudinary.api().deleteResourcesByPrefix(folder,ObjectUtils.emptyMap());
        cloudinary.api().deleteFolder(folder,ObjectUtils.emptyMap());

    }

    private String extractPublicId(String imageUrl) {
        if (imageUrl==null || !imageUrl.contains("/upload/")) {
            throw new ResourceProcessingException("Invalid image url: "+ imageUrl);
        }
        try {
            String[] parts = imageUrl.split("/upload/");
            if(parts.length<2)
                throw new ResourceProcessingException("Invalid image url structure: "+ imageUrl);

            String path = parts[1];

            if (path.matches("^v\\d+/.*")){
                path = path.substring(path.indexOf("/")+1);
            }
            int lastDot = path.lastIndexOf('.');
            if (lastDot > 0) {
                path = path.substring(0, lastDot);
            }
            if(path.trim().isEmpty()){throw new ResourceNotFoundException("Extracted public Id is empty from url: "+ imageUrl);}
            return path;

        }catch (ResourceProcessingException | ResourceNotFoundException e){
            throw e;
        } catch (Exception e){
            throw new ResourceProcessingException("Error while extracting public id: "+e.getMessage());
        }
    }
}
