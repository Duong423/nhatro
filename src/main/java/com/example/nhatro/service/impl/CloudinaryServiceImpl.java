package com.example.nhatro.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.nhatro.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }

    @Override
    public void deleteFile(String imageUrl) {
        try {
            // Extract public_id from Cloudinary URL
            // URL format: https://res.cloudinary.com/{cloud_name}/image/upload/v{version}/{public_id}.{format}
            String publicId = extractPublicIdFromUrl(imageUrl);
            if (publicId != null && !publicId.isEmpty()) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file from Cloudinary", e);
        }
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        try {
            // Extract public_id from URL
            // Example: https://res.cloudinary.com/demo/image/upload/v1234567890/sample.jpg
            // public_id would be: sample
            String[] parts = imageUrl.split("/upload/");
            if (parts.length > 1) {
                String pathAfterUpload = parts[1];
                // Remove version if present (v1234567890/)
                pathAfterUpload = pathAfterUpload.replaceFirst("v\\d+/", "");
                // Remove file extension
                int lastDot = pathAfterUpload.lastIndexOf('.');
                if (lastDot > 0) {
                    return pathAfterUpload.substring(0, lastDot);
                }
                return pathAfterUpload;
            }
        } catch (Exception e) {
            System.err.println("Error extracting public_id from URL: " + imageUrl);
        }
        return null;
    }
}
