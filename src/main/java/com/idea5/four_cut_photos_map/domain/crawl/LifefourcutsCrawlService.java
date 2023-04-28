package com.idea5.four_cut_photos_map.domain.crawl;

import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
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
public class LifefourcutsCrawlService implements CrawlService {
    private final ShopRepository shopRepository;

    @Transactional
    public void crawl() {
        int page = 49;
        Brand brand = Brand.builder().id(1L).build();
        for(int i = 1; i <= page; i++) {
            String url = "https://lifefourcuts.com/Store01/?sort=TIME&keyword_type=all&page=" + i;
            Connection conn = Jsoup.connect(url);

            try {
                Document document = conn.get();
                Elements titles = document.select("div.map_contents.inline-blocked");

                for (Element e : titles) {
                    String placeName = "인생네컷 " + e.select("div.tit").text().trim();
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
