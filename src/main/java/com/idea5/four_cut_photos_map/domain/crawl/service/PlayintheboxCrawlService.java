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
public class PlayintheboxCrawlService extends EtcBrandCrawlService {

    public PlayintheboxCrawlService(ShopRepository shopRepository, BrandRepository brandRepository) {
        super(shopRepository, brandRepository);
    }

    @Override
    public void crawl() {
        log.info("=======Start PlayInTheBox Crawling=======");
        try {
            String baseUrl = CrawlTarget.PLAY_IN_THE_BOX.getUrl();
            int maxPageNum = CrawlTarget.PLAY_IN_THE_BOX.getTotalPage();

            for (int pageNum = 1; pageNum <= maxPageNum; pageNum++) {
                String url = baseUrl + pageNum;
                Document doc = connectToUrl(url);
                Elements elements = selectElements(doc, "div.map_contents.inline-blocked", url);

                for (Element element : elements) {
                    String placeName = element.select("a.map_link.blocked div.head div.tit").text();
                    if (isBranchNameWithSuffix(placeName)) {
                        String roadAddressName = element.selectFirst("div.p_group p").text();
                        roadAddressName = formatAddress(roadAddressName);

                        saveOrUpdateShop(placeName, roadAddressName);
                    }
                }
            }
            log.info("=======End PlayInTheBox Crawling=======");
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    @Override
    protected String formatPlaceName(String placeName) {
        return placeName;
    }
}
