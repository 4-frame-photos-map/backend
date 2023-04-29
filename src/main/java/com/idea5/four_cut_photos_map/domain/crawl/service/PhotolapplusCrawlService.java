package com.idea5.four_cut_photos_map.domain.crawl.service;

import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class PhotolapplusCrawlService extends EtcBrandCrawlService {

    public PhotolapplusCrawlService(ShopRepository shopRepository, BrandRepository brandRepository) {
        super(shopRepository, brandRepository);
    }

    @Override
    public void crawl() {
        log.info("====Start PhotoLapPlus Crawling===");
        try {
            String baseUrl = PHOTO_LAP_PLUS_URL;
            int maxPageNum = PHOTO_LAP_PLUS_TOTAL_PAGE;

            for (int pageNum = 1; pageNum <= maxPageNum; pageNum++) {
                String url = baseUrl + pageNum;
                Document doc = connectToUrl(url);
                Elements elements = selectElements(doc,"div.Zc7IjY", url);

                for (Element element : elements) {
                    String placeName = element.select("h2 span.wixui-rich-text__text").text();
                    if (isBranchNameWithSuffix(placeName)) {
                        String roadAddressName = element.select("p span.wixui-rich-text__text").text();
                        roadAddressName = formatAddress(roadAddressName);

                        saveOrUpdateShop(placeName, roadAddressName);
                    }
                }
            }
            log.info("====End PhotoLapPlus Crawling===");
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    @Override
    protected String formatPlaceName(String placeName) {
        return placeName;
    }
}
