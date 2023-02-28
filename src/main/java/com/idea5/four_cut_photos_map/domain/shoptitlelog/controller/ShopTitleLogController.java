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

    // Shop이 보유한 칭호 조회
    @GetMapping("/{shopId}")
    public ResponseEntity<RsData<List<ShopTitleDto>>> getShopTitles(@PathVariable Long shopId) {

        // 상점 칭호 보유 여부 체크
        boolean existShopTitles = shopTitleLogService.existShopTitles(shopId);
        if (!existShopTitles) {
            return ResponseEntity.ok(new RsData<>(
                    true, "상점이 보유한 칭호는 없습니다."
            ));
        }
        List<ShopTitleDto> responseList = shopTitleLogService.findShopTitlesByShopId(shopId);

        return ResponseEntity.ok(new RsData<>(
                true, "상점 타이틀 조회 성공", responseList
        ));
    }

    // Shop 칭호 추가 (포스트맨용 임시 api)
    @PostMapping("/{shopId}/{shopTitleId}")
    public ResponseEntity<RsData> addShopTitleLog(@PathVariable Long shopId, @PathVariable Long shopTitleId) {
        shopTitleLogService.save(shopId, shopTitleId);

        return ResponseEntity.ok(new RsData<>(
                true, "해당 상점 타이틀 추가 성공"
        ));
    }

    // Shop 칭호 삭제 (포스트맨용 임시 api)
    @DeleteMapping("/{shopTitleLogId}")
    public ResponseEntity<RsData> deleteShopTitleLog(@PathVariable Long shopTitleLogId) {
        shopTitleLogService.delete(shopTitleLogId);

        return ResponseEntity.ok(new RsData<>(
                true, "해당 상점 타이틀 삭제 성공"
        ));
    }
}
