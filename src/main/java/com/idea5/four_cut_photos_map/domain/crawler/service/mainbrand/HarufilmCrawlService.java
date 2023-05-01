package com.idea5.four_cut_photos_map.domain.crawler.service.mainbrand;

import com.idea5.four_cut_photos_map.domain.brand.entity.Brand;
import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.crawler.service.mainbrand.CrawlService;
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
public class HarufilmCrawlService implements CrawlService {
    private final ShopRepository shopRepository;
    private final BrandRepository brandRepository;

    @Transactional
    public void crawl() {
        int[] codes = {202, 203, 204, 205, 206, 207, 208, 209};
        Brand brand = brandRepository.findById(2L).orElse(null);

        for(int code : codes) {
            String url = "http://harufilm.com/" + code;
            Connection conn = Jsoup.connect(url);

            try {
                Document document = conn.get();
                Elements titles = document.select("p.title");

                for (Element element : titles) {
                    String address = element.select("span.body").text().trim();
                    String placeName = "하루필름 " + element.text().replace(address, "").trim();
                    // 지점명으로 중복 검사
                    if (shopRepository.existsByPlaceName(placeName)) continue;
                    Shop shop = Shop.builder()
                            .placeName(placeName)
                            .address(address)
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
