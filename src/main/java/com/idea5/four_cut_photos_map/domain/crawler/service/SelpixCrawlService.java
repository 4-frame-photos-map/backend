package com.idea5.four_cut_photos_map.domain.crawler.service;

import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.crawler.enums.CrawlTarget;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.extern.slf4j.Slf4j;
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
        runCrawler(
                CrawlTarget.SELPIX.getUrl(),
                "dt",
                "span.subject"
        );
        log.info("=======End Selpix Crawling=======");
    }

    @Override
    protected String formatPlaceName(String placeName) {
        placeName = placeName.contains(" ") ? placeName.split(" ")[1] : placeName;
        return "셀픽스 " + placeName;
    }
}
