package com.idea5.four_cut_photos_map.domain.shoptitle.service;

import com.idea5.four_cut_photos_map.domain.shoptitle.dto.ShopTitleDto;
import com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitle;
import com.idea5.four_cut_photos_map.domain.shoptitle.repository.ShopTitleRepository;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.service.ShopTitleLogService;
import com.idea5.four_cut_photos_map.global.error.ErrorCode;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopTitleService {

    private final ShopTitleRepository shopTitleRepository;

    public ShopTitleDto findShopTitle(Long id) {
        ShopTitle shopTitle = shopTitleRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.SHOP_TITLE_NOT_FOUND));
        return ShopTitleDto.of(shopTitle);
    }

    public ShopTitle findById(Long shopTitleId) {
        return shopTitleRepository.findById(shopTitleId).orElseThrow(() -> new BusinessException(ErrorCode.SHOP_TITLE_NOT_FOUND));
    }


}
