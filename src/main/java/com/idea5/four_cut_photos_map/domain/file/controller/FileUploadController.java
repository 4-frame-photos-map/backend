package com.idea5.four_cut_photos_map.domain.file.controller;

import com.idea5.four_cut_photos_map.domain.file.dto.response.UploadImageResp;
import com.idea5.four_cut_photos_map.domain.file.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class FileUploadController {
    private final S3Service s3Service;

    @PostMapping
    public ResponseEntity<UploadImageResp> uploadFile(@RequestParam String category, @RequestParam MultipartFile file) {
//        try {
//            // 1. 이미지 파일이 아닌 경우 예외처리
//            if(file.getContentType().startsWith("image") == false) {
//                log.error("this file is not image type");
//                throw new BusinessException(ErrorCode.NOT_IMAGE_FILE);
//            }
//            String fileName = file.getOriginalFilename();
//            String fileUrl = "https://" + bucket + "/test" + fileName;
//            String fileFormatName = file.getContentType().substring(file.getContentType().lastIndexOf("/") + 1);
//
//            //
//            MultipartFile resizedFile = awsS3Service.resizeImage(fileName, fileFormatName, file, 768);
//
//            ObjectMetadata metadata= new ObjectMetadata();
//            metadata.setContentType(file.getContentType());
//            metadata.setContentLength(resizedFile.getSize());
//
//            // s3 파일 업로드
//            amazonS3Client.putObject(bucket, fileName, resizedFile.getInputStream(), metadata);
//            // s3 객체 URL 조회
//            String uploadImageUrl = amazonS3Client.getUrl(bucket, fileName).toString();
//            return ResponseEntity.ok(new UploadImageResp(uploadImageUrl));
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
        String uploadImageUrl = s3Service.uploadImageFile(category, file);
        return ResponseEntity.ok(new UploadImageResp(uploadImageUrl));
    }
}
