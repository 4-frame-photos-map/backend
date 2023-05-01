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
public class PhotosignatureCrawlService implements CrawlService {
    private final ShopRepository shopRepository;
    private final BrandRepository brandRepository;

    @Transactional
    public void crawl() {
        int page = 26;
        Brand brand = brandRepository.findById(5L).orElse(null);
        for(int i = 1; i <= page; i++) {
            String url = "http://photosignature.co.kr/muse/bbs/board.php?bo_table=store&page=" + i;
            Connection conn = Jsoup.connect(url);

            try {
                Document document = conn.get();
                Elements titles = document.select("#gallery tr tr:nth-child(2)>td>div");

                for (Element e : titles) {
                    String placeName = "포토시그니처 " + e.select("div:first-child>span>a").text();
                    String address = e.select("div:nth-child(2)").text()
                            .replace("주소 : ", "")
                            .replace("TEL :", "").trim();
//                    System.out.println("placeName = " + placeName);
//                    System.out.println("address = " + address);
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
