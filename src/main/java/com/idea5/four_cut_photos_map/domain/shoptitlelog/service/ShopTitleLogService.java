package com.idea5.four_cut_photos_map.domain.shoptitlelog.service;

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

    /**
     * 칭호가 없을 때)
     *  orElseThrow;
     *  orElse(null);
     */
    public List<ShopTitleDto> findShopTitle(Long shopId){
        List<ShopTitleLog> shopTitleLogs = shopTitleLogRepository.findAllByShopId(shopId);
//        List<ShopTitleLog> shopTitleLogs = shopTitleLogRepository.findAllByShopId(shopId).orElseThrow(() -> new BusinessException(SHOP_TITLE_LOGS_NOT_FOUND));

        // 조회 결과, 빈 컬렉션인 경우 예외 발생
        if (shopTitleLogs.isEmpty())
            throw new BusinessException(SHOP_TITLE_LOGS_NOT_FOUND);

        List<ShopTitleLogDto> shopTitleLogDtoList = shopTitleLogs.stream()
                .map(shopTitlelog -> ShopTitleLogDto.of(shopTitlelog))
                .collect(Collectors.toList());


        List<ShopTitleDto> responseList = new ArrayList<>();
        for (ShopTitleLogDto shopTitleLog : shopTitleLogDtoList) {
            ShopTitle shopTitle = shopTitleLog.getShopTitle();
            responseList.add(ShopTitleDto.of(shopTitle));
        }

        return responseList;
    }
}
