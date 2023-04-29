package com.idea5.four_cut_photos_map.domain.crawl.service;

import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@Transactional
public class HarryphotoCrawlService extends EtcBrandCrawlService {

    public HarryphotoCrawlService(ShopRepository shopRepository, BrandRepository brandRepository) {
        super(shopRepository, brandRepository);
    }


    @Override
    public void crawl() {
        log.info("====Start HarryPhoto Crawling===");
        try {
            String url = HARRY_PHOTO_URL;
            Document doc = connectToUrl(url);
            Elements elements = selectElements(doc, "dl", url);
            for (Element element : elements) {
                String placeName = element.select("dt").text();
                if (isBranchNameWithSuffix(placeName)) {
                    String roadAddressName = element.selectFirst("dd").text();
                    saveOrUpdateShop(placeName, roadAddressName);
                }
                log.info("====End HarryPhoto Crawling===");
            }
        } catch(Exception e){
            log.error("An error occurred during the crawling process: {}", e.getMessage());
            }
        }

    @Override
    protected String formatPlaceName(String placeName) {
        return placeName;
    }
}
