package com.idea5.four_cut_photos_map.domain.shop.service;

import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShop;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;

    public List<ResponseShop> findShops(List<ResponseShop> apiShops, String keyword) {
        List<Shop> dbShops = shopRepository.findByBrand(keyword).orElse(null);

        // 카카오 맵 api로 부터 받아온 Shop 리스트와 db에 저장된 Shop 비교
        List<ResponseShop> list = apiShops.stream()
                        .filter(filter -> dbShops.stream()
                        .anyMatch(target->filter.getName().equals(target.getName())))
                        .collect(Collectors.toList());

        return list;
    }
}
