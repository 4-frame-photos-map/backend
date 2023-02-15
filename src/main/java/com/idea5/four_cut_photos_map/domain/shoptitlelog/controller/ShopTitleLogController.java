package com.idea5.four_cut_photos_map.domain.shoptitlelog.controller;

import com.idea5.four_cut_photos_map.domain.shoptitle.dto.ShopTitleDto;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.service.ShopTitleLogService;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/shop-title-logs")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ShopTitleLogController {

    private final ShopTitleLogService shopTitleLogService;

    @GetMapping("/{shopId}")
    public ResponseEntity<RsData<List<ShopTitleDto>>> getShopTitles(@PathVariable Long shopId) {
        List<ShopTitleDto> responseList = shopTitleLogService.findShopTitleByShopId(shopId);

        return ResponseEntity.ok(new RsData<>(
                true, "상점 타이틀 조회 성공", responseList
        ));
    }

    @PostMapping("/{shopId}/{shopTitleId}")
    public ResponseEntity<RsData> addShopTitleLog(@PathVariable Long shopId, @PathVariable Long shopTitleId) {
        shopTitleLogService.save(shopId, shopTitleId);

        return null;
    }
}
