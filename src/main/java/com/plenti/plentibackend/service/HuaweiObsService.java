package com.plenti.plentibackend.service;

import com.obs.services.ObsClient;
import com.obs.services.model.PutObjectRequest;
import com.obs.services.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Service for Huawei OBS (Object Storage Service) file operations
 */
@Service
@Slf4j
public class HuaweiObsService {

    @Value("${huawei.obs.access-key-id:}")
    private String accessKeyId;

    @Value("${huawei.obs.secret-access-key:}")
    private String secretAccessKey;

    @Value("${huawei.obs.bucket-name:}")
    private String bucketName;

    @Value("${huawei.obs.region:af-south-1}")
    private String region;

    @Value("${huawei.obs.endpoint:https://obs.af-south-1.myhuaweicloud.com}")
    private String endpoint;

    private ObsClient obsClient;

    @PostConstruct
    public void init() {
        if (accessKeyId != null && !accessKeyId.isEmpty() && 
            secretAccessKey != null && !secretAccessKey.isEmpty()) {
            try {
                obsClient = new ObsClient(accessKeyId, secretAccessKey, endpoint);
                log.info("Huawei OBS client initialized successfully");
            } catch (Exception e) {
                log.error("Failed to initialize Huawei OBS client: {}", e.getMessage());
            }
        } else {
            log.warn("Huawei OBS credentials not configured. File upload will be disabled.");
        }
    }

    @PreDestroy
    public void cleanup() {
        if (obsClient != null) {
            try {
                obsClient.close();
                log.info("Huawei OBS client closed successfully");
            } catch (Exception e) {
                log.error("Error closing Huawei OBS client: {}", e.getMessage());
            }
        }
    }

    /**
     * Upload a file to OBS
     */
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        if (obsClient == null) {
            throw new IllegalStateException("Huawei OBS client is not initialized");
        }

        String fileName = generateFileName(file.getOriginalFilename());
        String objectKey = directory + "/" + fileName;

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, inputStream);
            PutObjectResult result = obsClient.putObject(request);
            
            String fileUrl = String.format("%s/%s/%s", endpoint, bucketName, objectKey);
            log.info("File uploaded successfully: {}", fileUrl);
            return fileUrl;
        } catch (Exception e) {
            log.error("Failed to upload file to OBS: {}", e.getMessage());
            throw new IOException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    /**
     * Upload product image
     */
    public String uploadProductImage(MultipartFile file, Long productId) throws IOException {
        return uploadFile(file, "products/" + productId);
    }

    /**
     * Upload user profile image
     */
    public String uploadUserProfileImage(MultipartFile file, Long userId) throws IOException {
        return uploadFile(file, "users/" + userId);
    }

    /**
     * Upload category image
     */
    public String uploadCategoryImage(MultipartFile file, Long categoryId) throws IOException {
        return uploadFile(file, "categories/" + categoryId);
    }

    /**
     * Delete file from OBS
     */
    public boolean deleteFile(String fileUrl) {
        if (obsClient == null) {
            log.warn("Huawei OBS client is not initialized");
            return false;
        }

        try {
            // Extract object key from URL
            String objectKey = extractObjectKeyFromUrl(fileUrl);
            obsClient.deleteObject(bucketName, objectKey);
            log.info("File deleted successfully: {}", fileUrl);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete file from OBS: {}", e.getMessage());
            return false;
        }
    }

    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    private String extractObjectKeyFromUrl(String fileUrl) {
        // Extract object key from full URL
        // Example: https://obs.af-south-1.myhuaweicloud.com/bucket/path/file.jpg -> path/file.jpg
        if (fileUrl.contains(bucketName + "/")) {
            return fileUrl.substring(fileUrl.indexOf(bucketName + "/") + bucketName.length() + 1);
        }
        return fileUrl;
    }
}
