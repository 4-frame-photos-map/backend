package com.idea5.four_cut_photos_map.domain.crawler.service.otherbrand;

import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.crawler.enums.CrawlTarget;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InsphotoCrawlService extends EtcBrandCrawlService {

    public InsphotoCrawlService(ShopRepository shopRepository, BrandRepository brandRepository) {
        super(shopRepository, brandRepository);
    }

    @Override
    public void crawl(){
        log.info("=======Start InsPhoto Crawling=======");
        runCrawler(
                CrawlTarget.INS_PHOTO.getUrl(),
                "section div.container div p",
                "span",
                "span:last-child"
        );
        log.info("=======End InsPhoto Crawling=======");
    }

    @Override
    protected String formatPlaceName(String placeName) {
        return "인스포토 " + placeName;
    }
}
