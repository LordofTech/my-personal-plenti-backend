package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.ResponseDTO;
import com.plenti.plentibackend.service.HuaweiObsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for file upload operations
 */
@RestController
@RequestMapping("/api/files")
@Tag(name = "Files", description = "File upload and management endpoints")
@Slf4j
public class FileUploadController {

    @Autowired
    private HuaweiObsService huaweiObsService;

    @PostMapping("/upload")
    @Operation(summary = "Upload file", description = "Upload a file to cloud storage")
    public ResponseEntity<ResponseDTO<Map<String, String>>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "directory", defaultValue = "general") String directory) {
        try {
            String fileUrl = huaweiObsService.uploadFile(file, directory);
            Map<String, String> result = new HashMap<>();
            result.put("fileUrl", fileUrl);
            result.put("fileName", file.getOriginalFilename());
            return ResponseEntity.ok(ResponseDTO.success("File uploaded successfully", result));
        } catch (Exception e) {
            log.error("File upload failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.error("File upload failed: " + e.getMessage()));
        }
    }

    @PostMapping("/product/{productId}/image")
    @Operation(summary = "Upload product image", description = "Upload an image for a specific product")
    public ResponseEntity<ResponseDTO<Map<String, String>>> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = huaweiObsService.uploadProductImage(file, productId);
            Map<String, String> result = new HashMap<>();
            result.put("fileUrl", fileUrl);
            result.put("productId", productId.toString());
            return ResponseEntity.ok(ResponseDTO.success("Product image uploaded successfully", result));
        } catch (Exception e) {
            log.error("Product image upload failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.error("Product image upload failed: " + e.getMessage()));
        }
    }

    @PostMapping("/user/profile-image")
    @Operation(summary = "Upload user profile image", description = "Upload a profile image for the current user")
    public ResponseEntity<ResponseDTO<Map<String, String>>> uploadUserProfileImage(
            @RequestParam("userId") Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = huaweiObsService.uploadUserProfileImage(file, userId);
            Map<String, String> result = new HashMap<>();
            result.put("fileUrl", fileUrl);
            result.put("userId", userId.toString());
            return ResponseEntity.ok(ResponseDTO.success("Profile image uploaded successfully", result));
        } catch (Exception e) {
            log.error("Profile image upload failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.error("Profile image upload failed: " + e.getMessage()));
        }
    }

    @PostMapping("/category/{categoryId}/image")
    @Operation(summary = "Upload category image", description = "Upload an image for a specific category")
    public ResponseEntity<ResponseDTO<Map<String, String>>> uploadCategoryImage(
            @PathVariable Long categoryId,
            @RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = huaweiObsService.uploadCategoryImage(file, categoryId);
            Map<String, String> result = new HashMap<>();
            result.put("fileUrl", fileUrl);
            result.put("categoryId", categoryId.toString());
            return ResponseEntity.ok(ResponseDTO.success("Category image uploaded successfully", result));
        } catch (Exception e) {
            log.error("Category image upload failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.error("Category image upload failed: " + e.getMessage()));
        }
    }

    @DeleteMapping
    @Operation(summary = "Delete file", description = "Delete a file from cloud storage")
    public ResponseEntity<ResponseDTO<String>> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        try {
            boolean deleted = huaweiObsService.deleteFile(fileUrl);
            if (deleted) {
                return ResponseEntity.ok(ResponseDTO.success("File deleted successfully", fileUrl));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ResponseDTO.error("Failed to delete file"));
            }
        } catch (Exception e) {
            log.error("File deletion failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseDTO.error("File deletion failed: " + e.getMessage()));
        }
    }
}
