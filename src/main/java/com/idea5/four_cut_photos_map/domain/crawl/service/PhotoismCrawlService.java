package com.idea5.four_cut_photos_map.domain.crawl.service;

import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PhotoismCrawlService implements CrawlService {
    private final ShopRepository shopRepository;
    private final BrandRepository brandRepository;

    @Transactional
    public void crawl() {
        // [지점코드, 페이지]
        int[][] infos = {
                {279, 32}, // 포토이즘 박스
                {280, 6},   // 포토이즘 컬러드
        };
        Brand brand = brandRepository.findById(3L).orElse(null);
        for(int[] info : infos) {
            for(int i = 1; i <= info[1]; i++) {
                String url = "https://photoism.co.kr/" + info[0] + "/?sort=TIME&keyword_type=all&page=" + i;
                Connection conn = Jsoup.connect(url);

                try {
                    Document document = conn.get();
                    Elements titles = document.select("div.map_container.clearfix.map-inner._map_container");

                    for (Element e : titles) {
                        String placeName = e.select("div.tit").text()
                                .replace("포토이즘 박스", "포토이즘박스")
                                .replace("포토이즘 컬러드", "포토이즘컬러드").trim();
                        String address = e.select("p.adress").text().trim();

                        // 지점명으로 중복 검사
                        if (shopRepository.existsByPlaceName(placeName)) continue;
                        Shop shop = Shop.builder()
                                .placeName(placeName)
                                .roadAddressName(address)
                                .brand(brand)
                                .favoriteCnt(0)
                                .reviewCnt(0)
                                .starRatingAvg(0.0)
                                .build();
                        shopRepository.save(shop);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
