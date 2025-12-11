# Profile Image Implementation Guide

This document describes the profile image upload feature implementation in the Plenti Backend.

## Overview

The profile image upload feature allows users to upload and manage their profile pictures using Huawei OBS (Object Storage Service). Images are stored in a cloud bucket with unique identifiers and served via CDN URLs.

## Architecture

### Components

1. **HuaweiObsService** - Handles file uploads to Huawei OBS
2. **UserController** - Exposes REST API endpoints for profile operations
3. **UserService** - Business logic for user profile management
4. **User Entity** - Stores profile image URL

### Flow Diagram

```
Client -> UserController -> UserService -> HuaweiObsService -> Huawei OBS
                                                                     |
                                                                     v
                                                            CDN URL returned
```

## API Endpoints

### Upload Profile Image

**Endpoint:** `POST /api/users/{userId}/profile-image`

**Request:**
- Method: POST
- Content-Type: multipart/form-data
- Body: Form data with "file" field containing image

**Example using cURL:**
```bash
curl -X POST http://localhost:8080/api/users/123/profile-image \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@/path/to/image.jpg"
```

**Example using JavaScript:**
```javascript
const formData = new FormData();
formData.append('file', imageFile);

fetch('http://localhost:8080/api/users/123/profile-image', {
  method: 'POST',
  headers: {
    'Authorization': 'Bearer ' + token
  },
  body: formData
})
.then(response => response.json())
.then(data => console.log('Image uploaded:', data.data.imageUrl));
```

**Response:**
```json
{
  "success": true,
  "message": "Profile image uploaded successfully",
  "data": {
    "imageUrl": "https://obs.af-south-1.myhuaweicloud.com/bucket/users/123/uuid.jpg"
  }
}
```

### Get User Profile

**Endpoint:** `GET /api/users/{userId}`

**Response includes profile image URL:**
```json
{
  "success": true,
  "data": {
    "id": 123,
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "+2348012345678",
    "profileImageUrl": "https://obs.af-south-1.myhuaweicloud.com/bucket/users/123/uuid.jpg",
    "trustScore": 85
  }
}
```

## Implementation Details

### File Upload Service

**Location:** `src/main/java/com/plenti/plentibackend/service/HuaweiObsService.java`

Key features:
- File validation (size, type)
- Unique filename generation using UUID
- Directory-based organization (`users/{userId}/`)
- Error handling and logging
- Support for signed URLs (temporary access)

```java
public String uploadUserProfileImage(MultipartFile file, Long userId) throws IOException {
    // Validate file
    validateImageFile(file);
    
    // Upload to OBS
    return uploadFile(file, "users/" + userId);
}

private void validateImageFile(MultipartFile file) {
    // Check file size (max 5MB)
    if (file.getSize() > 5 * 1024 * 1024) {
        throw new PlentiException("File size exceeds 5MB limit");
    }
    
    // Check file type
    String contentType = file.getContentType();
    if (!contentType.startsWith("image/")) {
        throw new PlentiException("File must be an image");
    }
}
```

### Controller Implementation

**Location:** `src/main/java/com/plenti/plentibackend/controller/UserController.java`

```java
@PostMapping("/{userId}/profile-image")
@Operation(summary = "Upload profile image")
public ResponseEntity<ResponseDTO<Map<String, String>>> uploadProfileImage(
        @PathVariable Long userId,
        @RequestParam("file") MultipartFile file) {
    try {
        String imageUrl = huaweiObsService.uploadUserProfileImage(file, userId);
        userService.updateProfileImage(userId, imageUrl);
        
        Map<String, String> response = Map.of("imageUrl", imageUrl);
        return ResponseEntity.ok(ResponseDTO.success("Profile image uploaded", response));
    } catch (IOException e) {
        return ResponseEntity.badRequest()
            .body(ResponseDTO.error("Failed to upload image: " + e.getMessage()));
    }
}
```

### Database Schema

The `users` table includes a `profile_image_url` column:

```sql
ALTER TABLE users ADD COLUMN profile_image_url VARCHAR(500);
```

Entity definition:
```java
@Entity
@Table(name = "users")
public class User {
    // ... other fields
    
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;
}
```

## Configuration

### Required Environment Variables

Set these in your `.env` file or environment:

```properties
HUAWEI_OBS_ACCESS_KEY_ID=your_access_key
HUAWEI_OBS_SECRET_ACCESS_KEY=your_secret_key
HUAWEI_OBS_BUCKET_NAME=plenti-storage
HUAWEI_OBS_REGION=af-south-1
HUAWEI_OBS_ENDPOINT=https://obs.af-south-1.myhuaweicloud.com
```

### Huawei OBS Setup

1. **Create Bucket:**
   - Log in to Huawei Cloud Console
   - Navigate to Object Storage Service (OBS)
   - Create a new bucket (e.g., `plenti-storage`)
   - Choose region: `af-south-1` (Africa - Johannesburg)
   - Set bucket policy to private
   - Enable CDN if needed

2. **Configure CORS:**
   - In bucket settings, add CORS rule:
   ```xml
   <CORSRule>
     <AllowedOrigin>*</AllowedOrigin>
     <AllowedMethod>GET</AllowedMethod>
     <AllowedMethod>POST</AllowedMethod>
     <AllowedMethod>PUT</AllowedMethod>
     <AllowedHeader>*</AllowedHeader>
     <MaxAgeSeconds>3000</MaxAgeSeconds>
   </CORSRule>
   ```

3. **Set Bucket Permissions:**
   - Create folder structure: `users/`, `products/`, `categories/`
   - Set read permissions on uploaded files

4. **Get Access Keys:**
   - Navigate to **My Credentials** > **Access Keys**
   - Create new access key
   - Download and securely store credentials

## File Validation

### Supported Formats
- JPEG (.jpg, .jpeg)
- PNG (.png)
- GIF (.gif)
- WebP (.webp)

### Size Limits
- Maximum file size: 5MB
- Recommended dimensions: 300x300 to 1000x1000 pixels
- Images are stored as-is (no server-side resizing)

### Validation Logic

```java
private void validateImageFile(MultipartFile file) {
    // Size check
    if (file.getSize() > 5 * 1024 * 1024) {
        throw new PlentiException("File size exceeds 5MB limit");
    }
    
    // Type check
    String contentType = file.getContentType();
    List<String> allowedTypes = Arrays.asList(
        "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    
    if (!allowedTypes.contains(contentType)) {
        throw new PlentiException("Invalid file type. Allowed: JPEG, PNG, GIF, WebP");
    }
    
    // Filename check
    String filename = file.getOriginalFilename();
    if (filename == null || filename.isEmpty()) {
        throw new PlentiException("Invalid filename");
    }
}
```

## Security Considerations

### 1. Authentication
- All upload endpoints require JWT authentication
- Users can only upload to their own profile

### 2. File Validation
- Strict file type checking
- Size limits to prevent abuse
- Filename sanitization

### 3. Access Control
- Files stored with unique UUIDs
- Private bucket with controlled access
- Optional signed URLs for temporary access

### 4. Rate Limiting
- Limit uploads to 5 per hour per user
- Implement in API Gateway or application layer

```java
@RateLimiter(name = "profileImage", fallbackMethod = "uploadRateLimitFallback")
@PostMapping("/{userId}/profile-image")
public ResponseEntity<ResponseDTO<Map<String, String>>> uploadProfileImage(...) {
    // Implementation
}
```

## Frontend Integration

### React Example

```jsx
import React, { useState } from 'react';
import axios from 'axios';

function ProfileImageUpload({ userId, token }) {
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [imageUrl, setImageUrl] = useState(null);

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
  };

  const handleUpload = async () => {
    if (!file) return;

    setUploading(true);
    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await axios.post(
        `http://localhost:8080/api/users/${userId}/profile-image`,
        formData,
        {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'multipart/form-data'
          }
        }
      );

      setImageUrl(response.data.data.imageUrl);
      alert('Profile image uploaded successfully!');
    } catch (error) {
      alert('Upload failed: ' + error.message);
    } finally {
      setUploading(false);
    }
  };

  return (
    <div>
      <input type="file" accept="image/*" onChange={handleFileChange} />
      <button onClick={handleUpload} disabled={!file || uploading}>
        {uploading ? 'Uploading...' : 'Upload'}
      </button>
      {imageUrl && <img src={imageUrl} alt="Profile" width="200" />}
    </div>
  );
}
```

### Angular Example

```typescript
import { Component } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-profile-image-upload',
  template: `
    <input type="file" (change)="onFileSelected($event)" accept="image/*">
    <button (click)="uploadImage()" [disabled]="!selectedFile || uploading">
      {{ uploading ? 'Uploading...' : 'Upload' }}
    </button>
    <img *ngIf="imageUrl" [src]="imageUrl" alt="Profile" width="200">
  `
})
export class ProfileImageUploadComponent {
  selectedFile: File | null = null;
  uploading = false;
  imageUrl: string | null = null;

  constructor(private http: HttpClient) {}

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  uploadImage() {
    if (!this.selectedFile) return;

    this.uploading = true;
    const formData = new FormData();
    formData.append('file', this.selectedFile);

    const token = localStorage.getItem('token');
    const userId = localStorage.getItem('userId');
    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });

    this.http.post(`http://localhost:8080/api/users/${userId}/profile-image`, 
      formData, { headers })
      .subscribe(
        (response: any) => {
          this.imageUrl = response.data.imageUrl;
          this.uploading = false;
          alert('Profile image uploaded successfully!');
        },
        error => {
          this.uploading = false;
          alert('Upload failed: ' + error.message);
        }
      );
  }
}
```

## Testing

### Manual Testing with Postman

1. **Setup:**
   - Import Plenti API collection
   - Set environment variables (base URL, token)

2. **Test Profile Image Upload:**
   - Method: POST
   - URL: `{{baseUrl}}/api/users/{{userId}}/profile-image`
   - Headers: `Authorization: Bearer {{token}}`
   - Body: form-data with key "file" and file value

3. **Verify:**
   - Check response for imageUrl
   - Open imageUrl in browser to verify image loads
   - Check user profile to see updated image

### Automated Testing

```java
@Test
public void testUploadProfileImage() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file",
        "profile.jpg",
        "image/jpeg",
        "test image content".getBytes()
    );

    mockMvc.perform(multipart("/api/users/1/profile-image")
            .file(file)
            .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.imageUrl").exists());
}
```

## Troubleshooting

### Issue: "OBS client not initialized"

**Cause:** Missing or invalid OBS credentials

**Solution:**
- Verify environment variables are set
- Check access key and secret key are correct
- Ensure application restarted after setting env vars

### Issue: "File size exceeds limit"

**Cause:** Image file too large

**Solution:**
- Compress image before uploading
- Use image optimization tools
- Current limit is 5MB (configurable)

### Issue: "Access denied" when viewing image

**Cause:** Bucket permissions not set correctly

**Solution:**
- Set bucket ACL to allow read access
- Or use signed URLs for private access
- Check CORS configuration

### Issue: Upload succeeds but image not displayed

**Cause:** CORS issue or incorrect URL

**Solution:**
- Configure CORS in OBS bucket settings
- Verify imageUrl format
- Check browser console for CORS errors

## Performance Optimization

### 1. Client-Side Image Compression

Compress images before upload to reduce bandwidth:

```javascript
async function compressImage(file) {
  return new Promise((resolve) => {
    const reader = new FileReader();
    reader.onload = (e) => {
      const img = new Image();
      img.onload = () => {
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        
        // Resize to max 800x800
        let width = img.width;
        let height = img.height;
        const maxSize = 800;
        
        if (width > height && width > maxSize) {
          height = (height / width) * maxSize;
          width = maxSize;
        } else if (height > maxSize) {
          width = (width / height) * maxSize;
          height = maxSize;
        }
        
        canvas.width = width;
        canvas.height = height;
        ctx.drawImage(img, 0, 0, width, height);
        
        canvas.toBlob(resolve, 'image/jpeg', 0.8);
      };
      img.src = e.target.result;
    };
    reader.readAsDataURL(file);
  });
}
```

### 2. CDN Integration

- Enable Huawei CDN for faster image delivery
- Configure cache headers for optimal performance
- Use edge locations close to users

### 3. Lazy Loading

Implement lazy loading for profile images:

```html
<img src="placeholder.jpg" data-src="actual-profile-url.jpg" 
     loading="lazy" alt="Profile">
```

## Future Enhancements

1. **Image Cropping**
   - Add server-side image cropping
   - Client-side crop before upload

2. **Multiple Sizes**
   - Generate thumbnail, medium, large versions
   - Serve appropriate size based on context

3. **Image Filters**
   - Apply filters/effects server-side
   - Face detection and centering

4. **Progressive Upload**
   - Upload low-res version first
   - Replace with high-res when ready

5. **Batch Upload**
   - Support uploading multiple images
   - Gallery management

## Support

For issues or questions:
- Check application logs: `logs/application.log`
- Review Huawei OBS documentation
- Contact backend team: backend@plenti.ng
