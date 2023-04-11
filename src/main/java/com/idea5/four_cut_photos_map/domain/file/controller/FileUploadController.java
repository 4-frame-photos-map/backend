package com.idea5.four_cut_photos_map.domain.file.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.idea5.four_cut_photos_map.domain.file.dto.response.UploadImageResp;
import com.idea5.four_cut_photos_map.global.error.ErrorCode;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @PostMapping
    public ResponseEntity<UploadImageResp> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 이미지 파일이 아닌 경우 예외처리
            if(file.getContentType().startsWith("image") == false) {
                log.error("this file is not image type");
                throw new BusinessException(ErrorCode.NOT_IMAGE_FILE);
            }
            String fileName = file.getOriginalFilename();
            System.out.println(fileName);
            String fileUrl = "https://" + bucket + "/test" + fileName;
            ObjectMetadata metadata= new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            // s3 파일 업로드
            amazonS3Client.putObject(bucket,fileName, file.getInputStream(), metadata);
            // s3 객체 URL 조회
            String uploadImageUrl = amazonS3Client.getUrl(bucket, fileName).toString();
            return ResponseEntity.ok(new UploadImageResp(uploadImageUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
