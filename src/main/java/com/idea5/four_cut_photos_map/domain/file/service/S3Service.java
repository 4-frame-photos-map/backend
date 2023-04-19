package com.idea5.four_cut_photos_map.domain.file.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.idea5.four_cut_photos_map.domain.file.dto.response.UploadImageResp;
import com.idea5.four_cut_photos_map.global.error.ErrorCode;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import com.idea5.four_cut_photos_map.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 이미지 파일 업로드
    public UploadImageResp uploadImageFile(String category, List<MultipartFile> files) {
        List<String> imageUrls = new ArrayList<>();
        for(MultipartFile file : files) {
            // 1. 이미지 파일이 아닌 경우 예외처리
            validImageFile(file);
            // 2. 객체 키 생성(키 이름 중복 방지)
            String key = Util.generateS3ObjectKey(category, file.getOriginalFilename());
            // 3. 파일 업로드
            String imageUrl = putS3(key, file);
            imageUrls.add(imageUrl);
        }
        return new UploadImageResp(imageUrls);
    }

    // 이미지 파일인지 검사
    public void validImageFile(MultipartFile file) {
        if(file.getContentType().startsWith("image") == false) {
            throw new BusinessException(ErrorCode.NOT_IMAGE_FILE);
        }
    }

    // S3 객체 생성
    public String putS3(String key, MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        // 1. s3 파일 업로드
        try {
            amazonS3Client.putObject(bucket, key, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드에 실패했습니다.");
        }
        // 2. s3 객체 URL 조회
        return amazonS3Client.getUrl(bucket, key).toString();
    }
}
