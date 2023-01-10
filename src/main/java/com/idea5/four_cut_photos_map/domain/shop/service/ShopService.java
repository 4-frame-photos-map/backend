package com.idea5.four_cut_photos_map.domain.shop.service;

import com.idea5.four_cut_photos_map.domain.shop.dto.ShopDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseMarker;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShop;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShopDetail;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import com.idea5.four_cut_photos_map.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.stream.Collectors;

import static com.idea5.four_cut_photos_map.global.error.ErrorCode.SHOP_NOT_Found;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;

    public List<ResponseShop> findShops(List<ShopDto> apiShops, String keyword) {

        // DB 조회 -> Dto 변환
        List<Shop> dbShops = shopRepository.findByBrand(keyword).orElseThrow(() -> new BusinessException(SHOP_NOT_Found));
        if(dbShops.isEmpty())
            throw new BusinessException(SHOP_NOT_Found);

        List<ResponseShop> responseShops = new ArrayList<>();
        for (Shop dbShop : dbShops) {
            responseShops.add(ResponseShop.from(dbShop));
        }

        // 카카오 맵 api로 부터 받아온 Shop 리스트와 db에 저장된 Shop 비교
        for (ShopDto apiShop : apiShops) {
            for (ResponseShop responseShop : responseShops) {
                if(apiShop.getName().equals(responseShop.getName())){
                    responseShop.setDistance(apiShop.getDistance());
                }
            }
        }

        return responseShops;
    }

    public Map<String, List<ResponseMarker>> findMaker(Map<String, List<ShopDto>> apiShopMaps) {
        Map<String, List<ResponseMarker>> temp = new HashMap<>();

        // 브랜드별로, 카카오 맵 api로 부터 받아온 Shop 리스트와 db에 저장된 Shop 비교
        for (String brandName : apiShopMaps.keySet()) {

            // 브랜드명으로 map에 저장된 shop List 얻기
            List<ShopDto> apiShops = apiShopMaps.get(brandName);

            // 브랜드명으로 실제 DB에 저장되어있는 shop List 얻기
            List<Shop> dbShops = shopRepository.findByBrand(brandName).orElseThrow(() -> new BusinessException(SHOP_NOT_Found));
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

            temp.put(brandName, list);
        }
        return temp;
    }


    // todo : Review, 찜 추가
    public ResponseShopDetail findShopById(Long id, String distance) {
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new BusinessException(SHOP_NOT_Found));
        ResponseShopDetail shopDto = ResponseShopDetail.of(shop, distance);
        return shopDto;

    }
}
