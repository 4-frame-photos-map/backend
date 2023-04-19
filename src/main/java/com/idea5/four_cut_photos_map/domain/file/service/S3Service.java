package com.idea5.four_cut_photos_map.domain.file.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.idea5.four_cut_photos_map.global.error.ErrorCode;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import com.idea5.four_cut_photos_map.global.util.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 이미지 파일 업로드
    public String uploadImageFile(String category, MultipartFile file) {
        // 1. 이미지 파일이 아닌 경우 예외처리
        if(file.getContentType().startsWith("image") == false) {
            log.error("this file is not image type");
            throw new BusinessException(ErrorCode.NOT_IMAGE_FILE);
        }
        // 1. 객체 키 생성(키 이름 중복 방지)
        String key = Util.generateS3ObjectKey(category, file.getOriginalFilename());
        log.info("key=" + key);
        // 2.
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        // 3. s3 파일 업로드
        try {
            amazonS3Client.putObject(bucket, key, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 4. s3 객체 URL 조회
        String objectUrl = amazonS3Client.getUrl(bucket, key).toString();

        return objectUrl;
    }
}
