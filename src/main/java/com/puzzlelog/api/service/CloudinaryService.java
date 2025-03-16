package com.puzzlelog.api.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;


@Service
public class CloudinaryService {
    
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadToCloud(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename(); // 원본 파일명 가져오기
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase(); // 확장자 가져오기
        String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf(".")); // 확장자 제거

        // Cloudinary에서 지원하는 모든 오디오 & 비디오 확장자 목록
        Set<String> supportedExtensions = new HashSet<>(Arrays.asList(
            // 오디오 확장자
            "aac", "aiff", "amr", "flac", "m4a", "mp3", "ogg", "opus", "wav",
            // 비디오 확장자
            "3g2", "3gp", "avi", "flv", "m3u8", "ts", "m2ts", "mts", "mov", "mkv", "mp4", 
            "mpeg", "mpd", "mxf", "ogv", "webm", "wmv"
        ));

        // 파일 확장자에 따라 Cloudinary의 resource_type 설정
        String resourceType = "image"; // 기본값: 이미지
        if (supportedExtensions.contains(fileExtension)) {
            resourceType = "video"; // ✅ 모든 오디오 & 비디오 파일을 video로 설정
        }

        Map<String, Object> params = new HashMap<>();
        params.put("use_filename", true);
        params.put("unique_filename", false);
        params.put("overwrite", true);
        params.put("resource_type", resourceType); // ✅ 모든 오디오 & 비디오를 video로 업로드
        params.put("public_id", fileNameWithoutExt); // ✅ 확장자 제거한 파일명 사용

        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = (Map<String, Object>) cloudinary.uploader().upload(file.getBytes(), params);
        return uploadResult.get("secure_url").toString();
    }
}