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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class FileUploadController {
    private final S3Service s3Service;

    // 단일 이미지 업로드
    @PostMapping("/image")
    public ResponseEntity<UploadImageResp> uploadImage(@RequestParam String category, @RequestParam MultipartFile file) {
        UploadImageResp uploadImageResp = s3Service.uploadImage(category, file);
        return ResponseEntity.ok(uploadImageResp);
    }

    // 다중 이미지 업로드
    @PostMapping("/images")
    public ResponseEntity<List<UploadImageResp>> uploadImages(@RequestParam String category, @RequestParam List<MultipartFile> files) {
        List<UploadImageResp> uploadImageResps = s3Service.uploadImages(category, files);
        return ResponseEntity.ok(uploadImageResps);
    }
}
