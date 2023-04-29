package com.idea5.four_cut_photos_map.domain.crawl.service;

import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.crawl.Entity.CrawlTarget;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SelpixCrawlService extends EtcBrandCrawlService {

    public SelpixCrawlService(ShopRepository shopRepository, BrandRepository brandRepository) {
        super(shopRepository, brandRepository);
    }

    @Override
    public void crawl() {
        log.info("=======Start Selpix Crawling=======");
        try {
            String url = CrawlTarget.SELPIX.getUrl();
            Document doc = connectToUrl(url);
            Elements elements = selectElements(doc, "span.subject", url);

            for (Element element : elements) {
                String placeName = element.text();
                if (isBranchNameWithSuffix(placeName)) {
                    placeName = formatPlaceName(placeName);
                    createShopIfNotExists(placeName);
                }
            }
            log.info("=======End Selpix Crawling=======");
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    @Override
    protected String formatPlaceName(String placeName) {
        placeName = placeName.contains(" ") ? placeName.split(" ")[1] : placeName;
        return "셀픽스 " + placeName;
    }
}
