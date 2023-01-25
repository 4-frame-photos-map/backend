package com.idea5.four_cut_photos_map.domain.shop.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.*;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.domain.shop.service.kakao.KeywordSearchKakaoApi;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.SHOP_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopService {
    private final ShopRepository shopRepository;
    private final KeywordSearchKakaoApi keywordSearchKakaoApi;
    private final ObjectMapper objectMapper;

    public List<ResponseShop> findShops(List<KaKaoSearchResponseDto.Document> apiShops, String keyword) {
        List<ResponseShop> responseShops = new ArrayList<>();

        // 카카오 맵 api로 부터 받아온 Shop과 db에 저장된 Shop 비교
        for (KaKaoSearchResponseDto.Document apiShop: apiShops) {
            //log.info("장소명="+apiShop.getPlace_name());

            // db에서 장소명으로 shop 조회
            Shop dbShop = shopRepository.findByName(apiShop.getPlace_name()).orElse(null);

            // entity -> dto 변환
            if(dbShop != null) {
                ResponseShop responseShop = ResponseShop.from(dbShop);

                // Api Shop과 비교 후 저장
                if (apiShop.getPlace_name().equals(responseShop.getName())
                        && Double.parseDouble(apiShop.getX()) == responseShop.getLongitude()
                        && Double.parseDouble(apiShop.getY()) == responseShop.getLatitude()) {
                    responseShops.add(responseShop);
                }

            }
        }

        if(responseShops.isEmpty())
            throw new BusinessException(SHOP_NOT_FOUND);

        return responseShops;
    }
/*
    public Map<String, List<ResponseMarker>> findMaker(Map<String, List<ShopDto>> apiShopMaps) {
        Map<String, List<ResponseMarker>> temp = new HashMap<>();

        // 브랜드별로, 카카오 맵 api로 부터 받아온 Shop 리스트와 db에 저장된 Shop 비교
        for (String name : apiShopMaps.keySet()) {

            // 브랜드명으로 map에 저장된 shop List 얻기
            List<ShopDto> apiShops = apiShopMaps.get(name);

            // 브랜드명으로 실제 DB에 저장되어있는 shop List 얻기
            List<Shop> dbShops = shopRepository.findByName(name).orElseThrow(() -> new BusinessException(SHOP_NOT_FOUND));
            // entity -> dto
            List<ResponseMarker> responseMarkers = new ArrayList<>();
            for (Shop dbShop : dbShops) {
                responseMarkers.add(ResponseMarker.of(dbShop));
            }


            List<ResponseMarker> list = new ArrayList<>();
            for (ShopDto apiShop : apiShops) {
                for (ResponseMarker responseMarker : responseMarkers) {
                    if(apiShop.getName().equals(responseMarker.getName())){
                        responseMarker.setDistance(apiShop.getDistance());
                        list.add(responseMarker);
                    }
                }
            }

            temp.put(name, list);
        }
        return temp;
    }
*/

    // todo : Review, 찜 추가
    public ResponseShopDetail findShopById(Long id, String distance) {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new BusinessException(SHOP_NOT_FOUND));
        ResponseShopDetail shopDto = ResponseShopDetail.of(shop, distance);
        return shopDto;

    }

    public KaKaoSearchResponseDto searchByKeyword(String keyword) {
        return keywordSearchKakaoApi.searchByKeyword(keyword);
    }
}
