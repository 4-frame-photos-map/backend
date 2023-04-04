package com.idea5.four_cut_photos_map.domain.brand.service;

import com.idea5.four_cut_photos_map.domain.brand.dto.response.ResponseBrandDto;
import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.global.error.ErrorCode;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {
    private final BrandRepository brandRepository;

    public ResponseBrandDto getBrandById(Long id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.INVALID_BRAND));

        return ResponseBrandDto.from(brand);
    }
}
