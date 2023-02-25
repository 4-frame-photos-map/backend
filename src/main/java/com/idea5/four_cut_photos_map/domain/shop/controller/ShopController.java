package com.idea5.four_cut_photos_map.domain.shop.controller;


import com.idea5.four_cut_photos_map.domain.favorite.entity.Favorite;
import com.idea5.four_cut_photos_map.domain.favorite.service.FavoriteService;

import com.idea5.four_cut_photos_map.domain.shop.dto.KakaoResponseDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.ShopDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestBrandSearch;
import com.idea5.four_cut_photos_map.domain.shop.dto.request.RequestShop;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.*;
import com.idea5.four_cut_photos_map.domain.shop.service.ShopService;
import com.idea5.four_cut_photos_map.domain.shoptitlelog.service.ShopTitleLogService;
import com.idea5.four_cut_photos_map.global.common.data.Brand;
import com.idea5.four_cut_photos_map.global.common.response.RsData;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import com.idea5.four_cut_photos_map.security.jwt.dto.MemberContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.*;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.*;


@RequestMapping("/shop")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ShopController {

    private final ShopService shopService;
    private final FavoriteService favoriteService;
    private final ShopTitleLogService shopTitleLogService;

    @GetMapping("/brand/search")
    public ResponseEntity<RsData<List<ResponseShopBrand>>> showBrandListBySearch(@ModelAttribute @Valid RequestBrandSearch requestBrandSearch) {
        // api 검색전, DB에서 먼저 있는지 확인하는게 더 효율적
        List<ShopDto> shopDtos = shopService.findByBrand(requestBrandSearch.getBrand());
        if(shopDtos.isEmpty())
            throw new BusinessException(BRAND_NOT_FOUND);


        List<KakaoResponseDto> kakaoApiResponse = shopService.searchBrand(requestBrandSearch);

        List<ResponseShopBrand> resultShops = new ArrayList<>(); // 응답값 리스트

        // 카카오 맵 api로 부터 받아온 Shop 리스트와 db에 저장된 Shop 비교
        for (KakaoResponseDto apiShop : kakaoApiResponse) {
            for (ShopDto shopDto : shopDtos) {
                if (apiShop.getPlaceName().equals(shopDto.getPlaceName())) {
                    resultShops.add(ResponseShopBrand.of(apiShop));
                }
            }
        }


        // 검색 결과, 근처에 원하는 브랜드가 없을 때
        if(resultShops.isEmpty()){
            return ResponseEntity.ok(new RsData<>(
                            true, String.format("근처에 %s이(가) 없습니다.", requestBrandSearch.getBrand()), resultShops
                    ));
        }

        return ResponseEntity.ok(new RsData<>(
                true, "brand 검색 성공", resultShops
        ));
    }


    /**
     * 키워드 검색 (리스트 조회)
     * ex)q
     * 하루필름, 인생네컷, 포토이즘, 포토그레이
     */

    @GetMapping(value = "/search")
    public ResponseEntity<RsData<List<ResponseShop>>> showKeywordSearchList(@RequestParam(defaultValue = "즉석사진") String keyword){
        // 1. 카카오맵 api 응답 데이터 받아오기
        KaKaoSearchResponseDto apiShopJson = shopService.searchByKeyword(keyword);

        // 2. 카카오 api 응답 DTO 에서 List<DTO.Document>로 변환
        List<KaKaoSearchResponseDto.Document> dtos = new ArrayList<>();

        for (int i = 0; i < apiShopJson.getDocuments().length; i++) {

            //log.info("name="+apiShopJson.getDocuments()[i].getPlace_name());
            //log.info("distance="+apiShopJson.getDocuments()[i].getDistance());

            /** TODO:
             - 1차: builder로 수정 [v]
             - 2차: 현업에서 사용하는 방식(Jackson으로 처리)으로 수정 [ ]
            */

            String name = apiShopJson.getDocuments()[i].getPlace_name();
            String address = apiShopJson.getDocuments()[i].getRoad_address_name();
            String longitude = apiShopJson.getDocuments()[i].getX();
            String latitude = apiShopJson.getDocuments()[i].getY();

            KaKaoSearchResponseDto.Document dto = KaKaoSearchResponseDto.Document.builder()
                    .place_name(name)
                    .road_address_name(address)
                    .x(longitude)
                    .y(latitude)
                    .build();

            dtos.add(dto);
        }

        // 3. db 데이터와 비교
        List<ResponseShop> shops = shopService.findShops(dtos);

        return ResponseEntity.ok(
                new RsData<List<ResponseShop>>(true, "Shop 조회 성공", shops)
        );
    }


    //현재 위치 기준, 반경 2km
    @GetMapping("/search/marker")
    public ResponseEntity<RsData<Map<String, List<ResponseShopMarker>>>> currentLocationSearch(@ModelAttribute @Valid RequestShop requestShop){

        String[] names = Brand.Names; // 브랜드명 ( 하루필름, 인생네컷 ... )

        Map<String, List<ResponseShopMarker>> maps = new HashMap<>();
        for (String brandName : names) {
            List<ResponseShopMarker> list = shopService.searchMarkers(requestShop, brandName);
            maps.put(brandName, list);
        }

        return ResponseEntity.ok(
                new RsData<Map<String, List<ResponseShopMarker>>>(true, "Shop 마커 성공", maps)
        );
    }

    // todo : @Validated 유효성 검사 시, httpstatus code 전달하는 방법
    @GetMapping("/detail/{shopId}")
    public ResponseEntity<ResponseShopDetail> detail(@PathVariable(name = "shopId") Long id,
                                                     @RequestParam(name = "distance", required = false, defaultValue = "") String distance,
                                                     @AuthenticationPrincipal MemberContext memberContext) {
        if (distance.isEmpty()){
            throw new BusinessException(DISTANCE_IS_EMPTY);
        }
        ResponseShopDetail shopDetailDto = shopService.findShopById(id, distance);

        // 비로그인 회원일 시
        if(memberContext == null){
            shopDetailDto.setCanBeAddedToFavorites(false);

        // 로그인 회원일 시
        } else {
            Favorite favorite = favoriteService.findByShopIdAndMemberId(id, memberContext.getId());
            if(favorite == null){
                shopDetailDto.setCanBeAddedToFavorites(true);
            } else {
                shopDetailDto.setCanBeAddedToFavorites(false);
            }
        }

        List<String> shopTitles = shopTitleLogService.getShopTitles(id);
        shopDetailDto.setShopTitles(shopTitles);


        return ResponseEntity.ok(shopDetailDto);
    }
}
