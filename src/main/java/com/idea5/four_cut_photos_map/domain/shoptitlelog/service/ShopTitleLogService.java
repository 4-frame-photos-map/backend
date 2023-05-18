package com.idea5.four_cut_photos_map.domain.shoptitlelog.service;

import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopTitleLog;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.domain.shoptitle.dto.ShopTitleDto;
import com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitle;
import com.idea5.four_cut_photos_map.domain.shoptitle.repository.ShopTitleRepository;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.entity.ShopTitleLog;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.repository.ShopTitleLogRepository;
import com.idea5.four_cut_photos_map.global.error.ErrorCode;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ShopTitleLogService {
    private final ShopTitleLogRepository shopTitleLogRepository;
    private final ShopTitleRepository shopTitleRepository;
    private final ShopRepository shopRepository;


    public boolean existShopTitles(Long shopId){
        return shopTitleLogRepository.existsByShopId(shopId);
    }

    public boolean isDuplicateShopTitleExist(Long shopId, Long shopTitleId){
        return shopTitleLogRepository.existsByShopIdAndShopTitleId(shopId, shopTitleId);
    }

    // 지점이 보유한 칭호명 목록 조회
    public List<String> getShopTitleNames(Long shopId) {
        List<ShopTitleDto> shopTitleByShopId = findShopTitlesByShopId(shopId);

        return shopTitleByShopId.stream()
                .map(ShopTitleDto::getName)
                .collect(Collectors.toList());
    }

    // 지점이 보유한 칭호 목록 조회
    public List<ShopTitleDto> findShopTitlesByShopId(Long shopId){
        List<ShopTitleLog> shopTitleLogs = shopTitleLogRepository.findAllByShopId(shopId);

        List<ShopTitleDto> responseList = new ArrayList<>();
        if (!shopTitleLogs.isEmpty()) {
            responseList = shopTitleLogs.stream()
                    .map(shopTitleLog -> ShopTitleDto.of(shopTitleLog.getShopTitle()))
                    .collect(Collectors.toList());
        }

        return responseList;
    }

    // 모든 지점 칭호 로그를 조회하여 지점 칭호명 별로 그룹화
    @Transactional(readOnly = true)
    public Map<String, List<ResponseShopTitleLog>> getAllShopTitleLogsGroupedByTitle(){
        List<ShopTitleLog> shopTitleLogs = shopTitleLogRepository.findAll();

        Map<String, List<ResponseShopTitleLog>> responseMap = shopTitleLogs.stream()
                .collect(Collectors.groupingBy(shopTitleLog -> shopTitleLog.getShopTitle().getName(),
                        Collectors.mapping(shopTitleLog -> ResponseShopTitleLog.from(shopTitleLog.getShop()), Collectors.toList())));

        return responseMap;
    }

    public ShopTitleLog findShopTitleLog(Long shopId, Long shopTitleId){
        return shopTitleLogRepository.findByShopIdAndShopTitleId(shopId, shopTitleId).orElse(null);
    }

    // 지점 칭호 부여
    @Transactional
    public void save(Long shopId, Long shopTitleId) {
        if (!isDuplicateShopTitleExist(shopId, shopTitleId)) {

            Shop shop = shopRepository.findById(shopId)
                    .orElseThrow(() -> new BusinessException(SHOP_NOT_FOUND));
            ShopTitle shopTitle = shopTitleRepository.findById(shopTitleId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_TITLE_NOT_FOUND));

            ShopTitleLog shopTitleLog = ShopTitleLog.builder()
                    .shop(shop)
                    .shopTitle(shopTitle)
                    .build();

            shopTitleLogRepository.save(shopTitleLog);
        }
    }
}
