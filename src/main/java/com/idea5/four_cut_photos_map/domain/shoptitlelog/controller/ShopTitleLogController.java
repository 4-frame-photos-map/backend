package com.idea5.four_cut_photos_map.domain.shoptitlelog.controller;

import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopTitleLog;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.domain.shoptitle.dto.ShopTitleDto;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.service.ShopTitleLogService;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.SHOP_NOT_FOUND;

@RequestMapping("/shop-title-logs")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ShopTitleLogController {
    private final ShopTitleLogService shopTitleLogService;
    private final ShopRepository shopRepository;

    /**
     * 모든 지점 칭호 로그 조회
     */
    @GetMapping("")
    public ResponseEntity<Map<String, List<ResponseShopTitleLog>>> getAllShopTitleLogs() {

        Map<String, List<ResponseShopTitleLog>> responseMap = shopTitleLogService.getAllShopTitleLogsGroupedByTitle();

        return ResponseEntity.ok(responseMap);
    }

    /**
     * 지점 칭호 단건 조회
     */
    @GetMapping("/{shopId}")
    public ResponseEntity<List<ShopTitleDto>> getShopTitles(@PathVariable Long shopId) {

        if(!shopRepository.existsById(shopId)){
            throw new BusinessException(SHOP_NOT_FOUND);
        }

        List<ShopTitleDto> responseList = shopTitleLogService.findShopTitlesByShopId(shopId);

        return ResponseEntity.ok(responseList);
    }
}
