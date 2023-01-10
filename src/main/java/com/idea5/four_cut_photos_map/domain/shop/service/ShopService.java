package com.idea5.four_cut_photos_map.domain.shop.service;

import com.idea5.four_cut_photos_map.domain.shop.dto.ShopDto;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseMarker;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShop;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;

    public List<ResponseShop> findShops(List<ShopDto> apiShops, String keyword) {

        // DB 조회 -> Dto 변환
        List<Shop> dbShops = shopRepository.findByBrand(keyword).orElse(null);
        List<ResponseShop> responseShops = new ArrayList<>();
        for (Shop dbShop : dbShops) {
            responseShops.add(ResponseShop.from(dbShop));
        }

        // 카카오 맵 api로 부터 받아온 Shop 리스트와 db에 저장된 Shop 비교
        List<ResponseShop> list = new ArrayList<>();
        for (ShopDto apiShop : apiShops) {
            for (ResponseShop responseShop : responseShops) {
                if(apiShop.getName().equals(responseShop.getName())){
                    responseShop.setDistance(apiShop.getDistance());
                    list.add(responseShop);
                }
            }
        }

        return list;
    }

    public Map<String, List<ResponseMarker>> findMaker(Map<String, List<ShopDto>> maps) {
        Map<String, List<ResponseMarker>> temp = new HashMap<>();

        // 브랜드별로, 카카오 맵 api로 부터 받아온 Shop 리스트와 db에 저장된 Shop 비교
        for (String brandName : maps.keySet()) {

            // 브랜드명으로 map에 저장된 shop List 얻기
            List<ShopDto> apiShops = maps.get(brandName);

            // 브랜드명으로 실제 DB에 저장되어있는 shop List 얻기
            List<Shop> dbShops = shopRepository.findByBrand(brandName).orElse(null);

            // entity -> dto
            List<ResponseMarker> responseMarkers = new ArrayList<>();
            for (Shop dbShop : dbShops) {
                responseMarkers.add(ResponseMarker.from(dbShop));
            }

            // stream을 활용하여 지점명 비교
            List<ResponseMarker> list = responseMarkers.stream()
                    .filter(filter -> apiShops.stream()
                    .anyMatch(target->filter.getName().equals(target.getName())))
                    .collect(Collectors.toList());

            temp.put(brandName, list);
        }
        return temp;
    }
}
