package com.idea5.four_cut_photos_map.domain.shoptitlelog.service;

import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.domain.shoptitle.dto.ShopTitleDto;
import com.idea5.four_cut_photos_map.domain.shoptitle.entity.ShopTitle;
import com.idea5.four_cut_photos_map.domain.shoptitle.repository.ShopTitleRepository;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.dto.ShopTitleLogDto;
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

    /**
     * 칭호가 없을 때)
     * orElseThrow;
     * orElse(null);
     */

    // 상점이 보유한 칭호 조회
    public List<String> getShopTitles(Long shopId) {
        // 상점이 보유한 칭호 엔티티 조회
        List<ShopTitleDto> shopTitleByShopId = findShopTitlesByShopId(shopId);

        // 칭호 이름만 get
        List<String> resultList = shopTitleByShopId.stream()
                .map(shopTitle -> shopTitle.getName())
                .collect(Collectors.toList());

        return resultList;
    }

    // Shop이 보유한 칭호 조회
    public List<ShopTitleDto> findShopTitlesByShopId(Long shopId){
        List<ShopTitleDto> responseList = new ArrayList<>();

        // shopId를 통해 ShopTitleLogs 데이터 조회
        List<ShopTitleLog> shopTitleLogs = shopTitleLogRepository.findAllByShopId(shopId);

        // 조회 결과, 빈 컬렉션인 경우 -> 보유한 칭호가 없음.
        if (shopTitleLogs.isEmpty())
            throw new BusinessException(SHOP_TITLE_NOT_FOUND);

       // entity -> Dto 변환
        List<ShopTitleLogDto> allShopTitleLogs = shopTitleLogs.stream()
                .map(shopTitlelog -> ShopTitleLogDto.of(shopTitlelog))
                .collect(Collectors.toList());


        // 응답 데이터(상점이 보유한 칭호) 작업 [ShopTitleLogDto -> ShopTileDto]
        for (ShopTitleLogDto shopTitleLog : allShopTitleLogs) {
            ShopTitle shopTitle = shopTitleLog.getShopTitle();
            responseList.add(ShopTitleDto.of(shopTitle));
        }

        return responseList;
    }


    // 상점이 칭호가 아예 없는 상태인지 아닌지 체크 (상점 상세보기 api용)
    public boolean existShopTitles(Long shopId){
        return shopTitleLogRepository.existsByShopId(shopId);
    }



    // Shop 칭호 부여
    @Transactional
    public void save(Long shopId, Long shopTitleId) {

        // 상점에 칭호 부여전, 중복된 데이터가 있는지 체크
        if (!validateDuplicate(shopId, shopTitleId)) {

            // 엔티티 조회
            Shop shop = shopRepository.findById(shopId).orElseThrow(() -> new BusinessException(SHOP_NOT_FOUND));
            ShopTitle shopTitle = shopTitleRepository.findById(shopTitleId).orElseThrow(() -> new BusinessException(ErrorCode.SHOP_TITLE_NOT_FOUND));

            ShopTitleLog shopTitleLog = ShopTitleLog.builder()
                    .shop(shop)
                    .shopTitle(shopTitle)
                    .build();

            // 저장
            shopTitleLogRepository.save(shopTitleLog);
        }

    }

    public List<ShopTitleLogDto> findAllShopTitleLogs(){
        List<ShopTitleLog> shopTitleLogs = shopTitleLogRepository.findAll();

        List<ShopTitleLogDto> responseList = shopTitleLogs.stream()
                .map(shopTitleLog -> ShopTitleLogDto.of(shopTitleLog))
                .collect(Collectors.toList());

        return responseList;

    }

    // 칭호 삭제
    @Transactional
    public void delete(Long id) {
        // 제거 전, DB에 존재하는지 체크
        ShopTitleLog entity = shopTitleLogRepository.findById(id).orElseThrow(() -> new BusinessException(SHOP_TITLE_NOT_FOUND));
        shopTitleLogRepository.delete(entity);
    }

    // log 조회
    public ShopTitleLog findShopTitleLog(Long shopId, Long shopTitleId){
        return shopTitleLogRepository.findByShopIdAndShopTitleId(shopId, shopTitleId).orElse(null);
    }

    // 칭호 중복여부 체크, false : 중복 x (칭호 부여 가능)
    public boolean validateDuplicate(Long shopId, Long shopTitleId){

        Optional<ShopTitleLog> shopTitleLog = shopTitleLogRepository.findByShopIdAndShopTitleId(shopId, shopTitleId);
        // 널값이 아니면 값이 있다는 의미 -> 중복
        if (shopTitleLog.isPresent()) {
            throw new BusinessException(DUPLICATE_SHOP_TITLE);
        }

        // 널값이면 save 가능
        return false;
    }
}
