package com.idea5.four_cut_photos_map.domain.shoptitlelog.service;

import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.domain.shoptitle.dto.ShopTitleDto;
import com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitle;
import com.idea5.four_cut_photos_map.domain.shoptitle.service.ShopTitleService;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.dto.ShopTitleLogDto;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.entity.ShopTitleLog;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.repository.ShopTitleLogRepository;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.SHOP_TITLE_LOGS_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopTitleLogService {

    private final ShopTitleLogRepository shopTitleLogRepository;
    private final ShopTitleService shopTitleService;
    private final ShopService shopService;

    /**
     * 칭호가 없을 때)
     *  orElseThrow;
     *  orElse(null);
     */
    public List<ShopTitleDto> findShopTitleByShopId(Long shopId){
        List<ShopTitleDto> responseList = new ArrayList<>();


        // 1. shopId를 통해 ShopTitleLogs 데이터 조회
        List<ShopTitleLogDto> allShopTitleLogs = findShopTitleLogsByShopId(shopId);


        // List, entity -> Dto 변환
        for (ShopTitleLogDto shopTitleLog : allShopTitleLogs) {
            ShopTitle shopTitle = shopTitleLog.getShopTitle();
            responseList.add(ShopTitleDto.of(shopTitle));
        }

        return responseList;
    }

    public List<ShopTitleLogDto> findShopTitleLogsByShopId(Long shopId){
        List<ShopTitleLog> shopTitleLogs = shopTitleLogRepository.findAllByShopId(shopId);

        // 조회 결과, 빈 컬렉션인 경우 예외 발생
        if (shopTitleLogs.isEmpty())
            throw new BusinessException(SHOP_TITLE_LOGS_NOT_FOUND);

        List<ShopTitleLogDto> shopTitleLogDtoList = shopTitleLogs.stream()
                .map(shopTitlelog -> ShopTitleLogDto.of(shopTitlelog))
                .collect(Collectors.toList());

        return shopTitleLogDtoList;
    }

    @Transactional
    public void save(Long shopId, Long shopTitleId) {
        Shop shop = shopService.findById(shopId);
        ShopTitle shopTitle = shopTitleService.findById(shopTitleId);

        ShopTitleLog shopTitleLog = ShopTitleLog.builder()
                .shop(shop)
                .shopTitle(shopTitle)
                .build();

        shopTitleLogRepository.save(shopTitleLog);
    }

    public List<ShopTitleLogDto> findAllShopTitleLogs(){
        List<ShopTitleLog> shopTitleLogs = shopTitleLogRepository.findAll();

        List<ShopTitleLogDto> responseList = shopTitleLogs.stream()
                .map(shopTitleLog -> ShopTitleLogDto.of(shopTitleLog))
                .collect(Collectors.toList());

        return responseList;

    }
}
