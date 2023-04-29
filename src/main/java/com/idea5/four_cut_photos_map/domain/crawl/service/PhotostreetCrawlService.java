package com.idea5.four_cut_photos_map.domain.crawl.service;

import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.crawl.Entity.CrawlTarget;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
public class PhotostreetCrawlService extends EtcBrandCrawlService {

    public PhotostreetCrawlService(ShopRepository shopRepository, BrandRepository brandRepository) {
        super(shopRepository, brandRepository);
    }

    @Override
    public void crawl() {
        log.info("=======Start PhotoStreet Crawling=======");
        try {
            String baseUrl = CrawlTarget.PHOTO_STREET.getUrl();
            int maxPageNum = CrawlTarget.PHOTO_STREET.getTotalPage();

            for (int pageNum = 1; pageNum <= maxPageNum; pageNum++) {
                String url = baseUrl + pageNum;
                Document doc = connectToUrl(url);
                Elements elements = selectElements(doc, "td.text-left a span", url);

                for (Element element : elements) {
                    String placeName = element.text();
                    formatPlaceName(placeName);
                }
            }
            log.info("=======End PhotoStreet Crawling=======");
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }

    }

    @Override
    protected String formatPlaceName(String placeName) {
        placeName = placeName.contains("(") ? placeName.split("\\(")[0] : placeName;

        // 지역명과 점포번호 분리
        if (!placeName.contains("오픈예정")) {
            Pattern[] regexes = {
                    Pattern.compile("\\d+호\\s+(.*)"),
                    Pattern.compile("\\d+호점\\s+(.*)")
            };
            for (Pattern regex : regexes) {
                Matcher matcher = regex.matcher(placeName);
                if (matcher.find()) {
                    placeName = "포토스트리트 " + matcher.group(1);
                    createShopIfNotExists(placeName);
                    break;
                }
            }
        }
        return placeName;
    }
}
