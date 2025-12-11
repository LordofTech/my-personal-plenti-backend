package com.plenti.plentibackend.service;

import com.obs.services.ObsClient;
import com.obs.services.model.*;
import com.plenti.plentibackend.exception.PlentiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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

    @Value("${file.upload.max-size:5242880}") // 5MB default
    private long maxFileSize;

    @Value("${file.upload.allowed-types:image/jpeg,image/png,image/gif,image/webp,application/pdf}")
    private String allowedContentTypes;

    private ObsClient obsClient;
    
    private static final List<String> IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    private static final List<String> DOCUMENT_TYPES = Arrays.asList(
        "application/pdf", "application/msword", 
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

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
     * Upload a file to OBS with validation
     */
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        if (obsClient == null) {
            throw new IllegalStateException("Huawei OBS client is not initialized");
        }
        
        validateFile(file);

        String fileName = generateFileName(file.getOriginalFilename());
        String objectKey = directory + "/" + fileName;

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, inputStream);
            
            // Set content type
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            request.setMetadata(metadata);
            
            PutObjectResult result = obsClient.putObject(request);
            
            String fileUrl = String.format("%s/%s/%s", endpoint, bucketName, objectKey);
            log.info("File uploaded successfully: {} (ETag: {})", fileUrl, result.getEtag());
            return fileUrl;
        } catch (Exception e) {
            log.error("Failed to upload file to OBS: {}", e.getMessage(), e);
            throw new IOException("Failed to upload file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Upload file with custom metadata
     */
    public String uploadFileWithMetadata(MultipartFile file, String directory, Map<String, String> customMetadata) throws IOException {
        if (obsClient == null) {
            throw new IllegalStateException("Huawei OBS client is not initialized");
        }
        
        validateFile(file);

        String fileName = generateFileName(file.getOriginalFilename());
        String objectKey = directory + "/" + fileName;

        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, inputStream);
            
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            
            // Add custom metadata
            if (customMetadata != null) {
                customMetadata.forEach(metadata::addUserMetadata);
            }
            
            request.setMetadata(metadata);
            PutObjectResult result = obsClient.putObject(request);
            
            String fileUrl = String.format("%s/%s/%s", endpoint, bucketName, objectKey);
            log.info("File with metadata uploaded successfully: {}", fileUrl);
            return fileUrl;
        } catch (Exception e) {
            log.error("Failed to upload file with metadata to OBS: {}", e.getMessage(), e);
            throw new IOException("Failed to upload file: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate a signed URL for temporary access (expires in 1 hour)
     */
    public String generateSignedUrl(String fileUrl, long expirationInSeconds) {
        if (obsClient == null) {
            log.warn("Huawei OBS client is not initialized");
            return fileUrl; // Return original URL if OBS not configured
        }

        try {
            String objectKey = extractObjectKeyFromUrl(fileUrl);
            
            TemporarySignatureRequest request = new TemporarySignatureRequest(
                HttpMethodEnum.GET, expirationInSeconds
            );
            request.setBucketName(bucketName);
            request.setObjectKey(objectKey);
            
            TemporarySignatureResponse response = obsClient.createTemporarySignature(request);
            String signedUrl = response.getSignedUrl();
            
            log.info("Generated signed URL for {} (expires in {} seconds)", objectKey, expirationInSeconds);
            return signedUrl;
        } catch (Exception e) {
            log.error("Failed to generate signed URL: {}", e.getMessage(), e);
            return fileUrl; // Fallback to original URL
        }
    }
    
    /**
     * Generate a signed URL with default 1 hour expiration
     */
    public String generateSignedUrl(String fileUrl) {
        return generateSignedUrl(fileUrl, 3600); // 1 hour
    }
    
    /**
     * Check if file exists in OBS
     */
    public boolean fileExists(String fileUrl) {
        if (obsClient == null) {
            return false;
        }

        try {
            String objectKey = extractObjectKeyFromUrl(fileUrl);
            GetObjectMetadataRequest request = new GetObjectMetadataRequest(bucketName, objectKey);
            obsClient.getObjectMetadata(request);
            return true;
        } catch (Exception e) {
            log.debug("File does not exist: {}", fileUrl);
            return false;
        }
    }
    
    /**
     * Get file metadata
     */
    public Map<String, Object> getFileMetadata(String fileUrl) {
        if (obsClient == null) {
            throw new IllegalStateException("Huawei OBS client is not initialized");
        }

        try {
            String objectKey = extractObjectKeyFromUrl(fileUrl);
            GetObjectMetadataRequest request = new GetObjectMetadataRequest(bucketName, objectKey);
            ObjectMetadata metadata = obsClient.getObjectMetadata(request);
            
            Map<String, Object> result = new HashMap<>();
            result.put("contentType", metadata.getContentType());
            result.put("contentLength", metadata.getContentLength());
            result.put("lastModified", metadata.getLastModified());
            result.put("etag", metadata.getEtag());
            // Note: getUserMetadata() requires a key parameter in this SDK version
            // result.put("userMetadata", metadata.getUserMetadata());
            
            return result;
        } catch (Exception e) {
            log.error("Failed to get file metadata: {}", e.getMessage(), e);
            throw new PlentiException("Failed to get file metadata: " + e.getMessage());
        }
    }
    
    /**
     * Validate file before upload
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new PlentiException("File is empty or null");
        }
        
        // Check file size
        if (file.getSize() > maxFileSize) {
            throw new PlentiException(String.format(
                "File size exceeds maximum allowed size of %d MB", 
                maxFileSize / (1024 * 1024)
            ));
        }
        
        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            throw new PlentiException("File content type is not specified");
        }
        
        List<String> allowedTypes = Arrays.asList(allowedContentTypes.split(","));
        if (!allowedTypes.contains(contentType)) {
            throw new PlentiException("File type not allowed: " + contentType);
        }
        
        // Check filename
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isEmpty()) {
            throw new PlentiException("File name is not specified");
        }
    }
    
    /**
     * Validate image file specifically
     */
    private void validateImageFile(MultipartFile file) {
        validateFile(file);
        
        String contentType = file.getContentType();
        if (!IMAGE_TYPES.contains(contentType)) {
            throw new PlentiException(
                "Invalid image file type. Allowed types: JPEG, PNG, GIF, WebP"
            );
        }
        
        // Additional size check for images (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new PlentiException("Image size exceeds 5MB limit");
        }
    }

    /**
     * Upload product image
     */
    public String uploadProductImage(MultipartFile file, Long productId) throws IOException {
        validateImageFile(file);
        return uploadFile(file, "products/" + productId);
    }

    /**
     * Upload user profile image
     */
    public String uploadUserProfileImage(MultipartFile file, Long userId) throws IOException {
        validateImageFile(file);
        return uploadFile(file, "users/" + userId);
    }

    /**
     * Upload category image
     */
    public String uploadCategoryImage(MultipartFile file, Long categoryId) throws IOException {
        validateImageFile(file);
        return uploadFile(file, "categories/" + categoryId);
    }
    
    /**
     * Upload banner image
     */
    public String uploadBannerImage(MultipartFile file) throws IOException {
        validateImageFile(file);
        return uploadFile(file, "banners");
    }
    
    /**
     * Upload document (PDF, DOC, etc.)
     */
    public String uploadDocument(MultipartFile file, String directory) throws IOException {
        String contentType = file.getContentType();
        if (!DOCUMENT_TYPES.contains(contentType)) {
            throw new PlentiException("Invalid document type. Allowed: PDF, DOC, DOCX");
        }
        return uploadFile(file, directory);
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
            DeleteObjectRequest request = new DeleteObjectRequest(bucketName, objectKey);
            obsClient.deleteObject(request);
            log.info("File deleted successfully: {}", fileUrl);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete file from OBS: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Delete multiple files from OBS
     */
    public Map<String, Boolean> deleteFiles(List<String> fileUrls) {
        Map<String, Boolean> results = new HashMap<>();
        
        if (fileUrls == null || fileUrls.isEmpty()) {
            return results;
        }
        
        for (String fileUrl : fileUrls) {
            results.put(fileUrl, deleteFile(fileUrl));
        }
        
        return results;
    }
    
    /**
     * List files in a directory
     */
    public List<String> listFiles(String directory) {
        if (obsClient == null) {
            throw new IllegalStateException("Huawei OBS client is not initialized");
        }

        try {
            ListObjectsRequest request = new ListObjectsRequest(bucketName);
            request.setPrefix(directory + "/");
            request.setMaxKeys(1000);
            
            ObjectListing result = obsClient.listObjects(request);
            List<String> fileUrls = new ArrayList<>();
            
            for (ObsObject obsObject : result.getObjects()) {
                String fileUrl = String.format("%s/%s/%s", endpoint, bucketName, obsObject.getObjectKey());
                fileUrls.add(fileUrl);
            }
            
            log.info("Listed {} files in directory: {}", fileUrls.size(), directory);
            return fileUrls;
        } catch (Exception e) {
            log.error("Failed to list files in directory {}: {}", directory, e.getMessage(), e);
            throw new PlentiException("Failed to list files: " + e.getMessage());
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
        // Extract object key from full URL using proper URI parsing
        // Example: https://obs.af-south-1.myhuaweicloud.com/bucket/path/file.jpg -> path/file.jpg
        try {
            java.net.URI uri = new java.net.URI(fileUrl);
            String path = uri.getPath();
            // Remove leading slash and bucket name if present
            if (path.startsWith("/" + bucketName + "/")) {
                return path.substring(("/" + bucketName + "/").length());
            } else if (path.startsWith("/")) {
                return path.substring(1);
            }
            return path;
        } catch (Exception e) {
            log.warn("Failed to parse URL, using fallback method: {}", e.getMessage());
            // Fallback to original logic
            if (fileUrl.contains(bucketName + "/")) {
                return fileUrl.substring(fileUrl.indexOf(bucketName + "/") + bucketName.length() + 1);
            }
            return fileUrl;
        }
    }
}
