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
public class InsphotoCrawlService extends EtcBrandCrawlService {

    public InsphotoCrawlService(ShopRepository shopRepository, BrandRepository brandRepository) {
        super(shopRepository, brandRepository);
    }

    @Override
    public void crawl() {
        log.info("=======Start InsPhoto Crawling=======");
        try {
            String url = CrawlTarget.INS_PHOTO.getUrl();
            Document doc = connectToUrl(url);
            Elements elements = selectElements(doc, "div.container div p:has(span:first-child)", url);

            for (Element element : elements) {
                String placeName = element.selectFirst("span").text();
                if (isBranchNameWithSuffix(placeName)) {
                    String roadAddressName = element.selectFirst("span:last-child").text();

                    placeName = formatPlaceName(placeName);
                    roadAddressName = formatAddress(roadAddressName);

                    saveOrUpdateShop(placeName, roadAddressName);
                }
            }
            log.info("=======End InsPhoto Crawling=======");
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    @Override
    protected String formatPlaceName(String placeName) {
        return "인스포토 " + placeName;
    }
}
