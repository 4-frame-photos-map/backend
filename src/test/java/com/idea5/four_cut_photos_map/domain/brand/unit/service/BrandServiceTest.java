package com.idea5.four_cut_photos_map.domain.brand.unit.service;

import com.idea5.four_cut_photos_map.domain.brand.dto.response.ResponseBrandDto;
import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.brand.entity.MajorBrand;
import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;

import com.idea5.four_cut_photos_map.domain.brand.service.BrandService;
import com.idea5.four_cut_photos_map.global.error.ErrorCode;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class BrandServiceTest {
    @InjectMocks
    private BrandService brandService;

    @Mock
    private BrandRepository brandRepository;

    @Nested
    @DisplayName("브랜드 아이디로 특정 브랜드 조회")
    class searchBrand {
        private Long id;
        private String brandName;
        private String filePath;

        @BeforeEach
        void setUp() {
            id = 1L;
            brandName = MajorBrand.LIFEFOURCUTS.getBrandName();
            filePath = MajorBrand.LIFEFOURCUTS.getFilePath();
        }

        @Nested
        @DisplayName("정상 케이스")
        class SuccessCase {
            @Test
            @DisplayName("해당 id를 가진 브랜드 존재")
            void getBrandByIdSuccess1() {
                // given
                Brand brand = Brand.builder().brandName(brandName).filePath(filePath).build();
                brand.setId(id);

                // mocking
                when(brandRepository.findById(id)).thenReturn(Optional.of(brand));

                // when
                ResponseBrandDto responseBrandDto = brandService.getBrandById(id);

                // then
                assertEquals(responseBrandDto.getBrandName(), brandName);
                assertEquals(responseBrandDto.getFilePath(), filePath);
            }
        }

        @Nested
        @DisplayName("실패 케이스")
        class FailCase {
            @Test
            @DisplayName("해당 id를 가진 브랜드 존재하지 않음")
            void getBrandByIdFail1() {
                // given
                BusinessException exception = new BusinessException(ErrorCode.BRAND_NOT_FOUND);

                // when
                when(brandRepository.findById(id)).thenThrow(exception);

                // then
                BusinessException e = assertThrows(exception.getClass(), () -> brandService.getBrandById(id));
                assertEquals(ErrorCode.BRAND_NOT_FOUND.getMessage(), e.getMessage());
            }
        }
    }
}
