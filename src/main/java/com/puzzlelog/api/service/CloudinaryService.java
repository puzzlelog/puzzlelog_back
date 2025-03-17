package com.puzzlelog.api.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.puzzlelog.api.dto.response.piece.CloudinaryUploadResponse;


@Service
public class CloudinaryService {
    
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // 이미지 전용 업로드 (프로필 이미지 등)
    public CloudinaryUploadResponse uploadImageToCloud(MultipartFile file, String publicId) throws IOException {
        Map<String, Object> params = new HashMap<>();
        params.put("use_filename", true);
        params.put("unique_filename", false);
        params.put("overwrite", true);
        params.put("resource_type", "image");
        params.put("public_id", publicId);

        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(), params);

        String url = uploadResult.get("secure_url").toString();
        String returnedPublicId = uploadResult.get("public_id").toString();

        return new CloudinaryUploadResponse(url, returnedPublicId);
    }
    
    // 파일 업로드
    public CloudinaryUploadResponse uploadToCloud(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        String fileNameWithoutExt = originalFilename.substring(0, originalFilename.lastIndexOf("."));

        Set<String> supportedExtensions = new HashSet<>(Arrays.asList(
            "aac", "aiff", "amr", "flac", "m4a", "mp3", "ogg", "opus", "wav",
            "3g2", "3gp", "avi", "flv", "m3u8", "ts", "m2ts", "mts", "mov",
            "mkv", "mp4", "mpeg", "mpd", "mxf", "ogv", "webm", "wmv"
        ));

        String resourceType = supportedExtensions.contains(fileExtension) ? "video" : "image";
        String publicId = fileNameWithoutExt + "_" + UUID.randomUUID().toString();

        Map<String, Object> params = new HashMap<>();
        params.put("use_filename", true);
        params.put("unique_filename", false);
        params.put("overwrite", true);
        params.put("resource_type", resourceType);
        params.put("public_id", publicId);

        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(), params);

        String url = uploadResult.get("secure_url").toString();
        String returnedPublicId = uploadResult.get("public_id").toString();

        return new CloudinaryUploadResponse(url, returnedPublicId);
    }

    // 파일 삭제 메서드
    public boolean deleteFromCloud(String publicId, String resourceType) {
        try {
        	@SuppressWarnings("unchecked")
        	Map<String, Object> result = (Map<String, Object>) cloudinary.uploader().destroy(
        	    publicId,
        	    ObjectUtils.asMap("resource_type", resourceType, "invalidate", true)
        	);
            return "ok".equals(result.get("result"));
        } catch (Exception e) {
            throw new RuntimeException("Cloudinary 파일 삭제 실패: " + e.getMessage(), e);
        }
    }
}