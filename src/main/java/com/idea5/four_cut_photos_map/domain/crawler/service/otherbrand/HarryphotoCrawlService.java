package com.idea5.four_cut_photos_map.domain.crawler.service;

import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.crawler.enums.CrawlTarget;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.extern.slf4j.Slf4j;
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
        log.info("=======Start HarryPhoto Crawling=======");
        runCrawler(
                CrawlTarget.HARRY_PHOTO.getUrl(),
                "dl",
                "dt",
                "dd");
        log.info("=======End HarryPhoto Crawling=======");
    }

    @Override
    protected String formatPlaceName(String placeName) {
        return placeName;
    }
}
