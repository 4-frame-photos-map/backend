package com.idea5.four_cut_photos_map.global.common.data;

import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseMarker;
import com.idea5.four_cut_photos_map.domain.shop.dto.response.ResponseShop;

import java.util.ArrayList;
import java.util.List;

public class TempKaKaO {
    public static List<ResponseShop> tempDataBySearch(String keyword){
        List<ResponseShop> shops = new ArrayList<>();
        for (ResponseShop shop : sampleData()) {
            if(shop.getBrand().equals(keyword)){
                shops.add(shop);
            }
        }
        return shops;
    }

    public static List<ResponseMarker> tempDataByBrand(String keyword){
        List<ResponseMarker> shops = new ArrayList<>();
        for (ResponseShop shop : sampleData()) {
            if(shop.getBrand().equals(keyword)){
                shops.add(new ResponseMarker(shop.getName(), shop.getLatitude(), shop.getLongitude(), shop.getDistance()));
            }
        }
        return shops;
    }

    static List<ResponseShop> sampleData(){
        List<ResponseShop> shops = new ArrayList<>();
        shops.add(new ResponseShop("인생네컷","인생네컷 강남점", "서울 강남구~", 12.3, 15.2, 100));
        shops.add(new ResponseShop("하루필름", "하루필름 강남점", "서울 강남구", 13.23, 18.2, 200));
        shops.add(new ResponseShop("하루필름", "하루필름 홍대점", "서울 강남구", 13.23, 18.2, 200));
        shops.add(new ResponseShop("포토이즘", "포토이즘 강남점", "서울 강남구", 19.23, 11.32, 300));
        shops.add(new ResponseShop("브랜드1", "브랜드1 강남점", "서울 강남구", 19.23, 11.32, 300));
        shops.add(new ResponseShop("브랜드2", "브랜드2 강남점", "서울 강남구", 20.23, 11.32, 300));
        return shops;
    }
}
