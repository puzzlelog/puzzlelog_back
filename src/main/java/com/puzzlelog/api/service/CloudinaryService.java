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


@Service
public class CloudinaryService {
    
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    // 파일 업로드
    public String uploadToCloud(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename(); // 원본 파일명
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase(); // 확장자
        String fileNameWithoutExt = originalFilename.substring(0, originalFilename.lastIndexOf(".")); // 확장자 제외한 파일명

        // ✅ Cloudinary에서 지원하는 모든 오디오 & 비디오 확장자 목록
        Set<String> supportedExtensions = new HashSet<>(Arrays.asList(
            "aac", "aiff", "amr", "flac", "m4a", "mp3", "ogg", "opus", "wav",
            "3g2", "3gp", "avi", "flv", "m3u8", "ts", "m2ts", "mts", "mov",
            "mkv", "mp4", "mpeg", "mpd", "mxf", "ogv", "webm", "wmv"
        ));

        String resourceType = supportedExtensions.contains(fileExtension) ? "video" : "image";

        // UUID를 붙여서 중복 방지
        String publicId = fileNameWithoutExt + "_" + UUID.randomUUID().toString();

        Map<String, Object> params = new HashMap<>();
        params.put("use_filename", true);
        params.put("unique_filename", false);
        params.put("overwrite", true);
        params.put("resource_type", resourceType);
        params.put("public_id", publicId); // UUID 기반으로 고유하게 생성된 파일명(public_id)

        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(), params);

        return uploadResult.get("secure_url").toString();
    }
    
    //  파일 삭제 (새로 추가!)
    public boolean deleteFromCloud(String publicId) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) cloudinary.uploader()
                .destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (Exception e) {
            throw new RuntimeException("Cloudinary 파일 삭제 실패", e);
        }
    }
}