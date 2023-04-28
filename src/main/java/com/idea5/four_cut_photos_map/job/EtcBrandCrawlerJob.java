package com.idea5.four_cut_photos_map.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.idea5.four_cut_photos_map.job.EtcBrandCrawlerService.*;

@Component
@Slf4j
@Transactional
@RequiredArgsConstructor
public class EtcBrandCrawlerJob {
    private final EtcBrandCrawlerService etcBrandCrawlerService;

    @Scheduled(cron = "0 0 3 2 5,11 *") // 매년 5월과 11월 2일 새벽 3시 실행
    public void crawlShopInfo() {
        crawlInsPhotoShopInfo(INS_PHOTO_URL);

        crawlSelpixShopInfo(SELPIX_URL);

        crawlPhotoStreetShopInfo(PHOTO_STREET_URL, PHOTO_STREET_TOTAL_PAGE);

        crawlPhotoDrinkShopInfo(PHOTO_DRINK_URL);

        crawlPhotoLapPlusShopInfo(PHOTO_LAP_PLUS_URL, PHOTO_LAP_PLUS_TOTAL_PAGE);

        crawlPlayInTheBoxShopInfo(PLAY_IN_THE_BOX_URL, PLAY_IN_THE_BOX_TOTAL_PAGE);

        crawlHarryPhotoShopInfo(HARRY_PHOTO_URL);
    }

    private void crawlInsPhotoShopInfo(String url) {
        log.info("====Start InsPhoto Crawling===");
        try {
            Document doc = etcBrandCrawlerService.connectToUrl(url);
            Elements elements = etcBrandCrawlerService.selectElements(doc, "div.container div p:has(span:first-child)", url);

            for (Element element : elements) {
                String placeName = element.selectFirst("span").text();
                if (placeName.endsWith("점")) {
                    String roadAddressName = element.selectFirst("span:last-child").text();

                    placeName = etcBrandCrawlerService.formatPlaceName(placeName, url);
                    roadAddressName = etcBrandCrawlerService.formatAddress(roadAddressName);

                    etcBrandCrawlerService.saveOrUpdateShop(placeName, roadAddressName);
                }
            }
            log.info("====End InsPhoto Crawling===");
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    private void crawlSelpixShopInfo(String url) {
        log.info("====Start Selpix Crawling===");
        try {
            Document doc = etcBrandCrawlerService.connectToUrl(url);
            Elements elements = etcBrandCrawlerService.selectElements(doc, "span.subject", url);

            for (Element element : elements) {
                String placeName = element.text();
                if (placeName.endsWith("점")) {
                    placeName = etcBrandCrawlerService.formatPlaceName(placeName, url);
                    etcBrandCrawlerService.createShopIfNotExists(placeName);
                }
            }
            log.info("====End Selpix Crawling===");
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    private void crawlPhotoStreetShopInfo(String baseUrl, int maxPageNum) {
        log.info("====Start PhotoStreet Crawling===");
        try {
            for (int pageNum = 1; pageNum <= maxPageNum; pageNum++) {
                String url = baseUrl + pageNum;
                Document doc = etcBrandCrawlerService.connectToUrl(url);
                Elements elements = etcBrandCrawlerService.selectElements(doc, "td.text-left a span", url);

                for (Element element : elements) {
                    String placeName = element.text();
                    etcBrandCrawlerService.formatPlaceName(placeName, baseUrl);
                }
            }
            log.info("====End PhotoStreet Crawling===");
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    private void crawlPhotoDrinkShopInfo(String url) {
        log.info("====Start PhotoDrink Crawling===");
        try {
            Document doc = etcBrandCrawlerService.connectToUrl(url);
            Elements elements = etcBrandCrawlerService.selectElements(doc, "div.text-table div", url);

            for (Element element : elements) {
                Elements pElement = element.select("p");
                String placeName = pElement.get(0).select("span").text();

                if (placeName.endsWith("점")) {
                    String roadAddressName = pElement.get(1).select("span").text();

                    placeName = etcBrandCrawlerService.formatPlaceName(placeName, url);
                    roadAddressName = etcBrandCrawlerService.formatAddress(roadAddressName);

                    etcBrandCrawlerService.saveOrUpdateShop(placeName, roadAddressName);
                }
            }
            log.info("====End PhotoDrink Crawling===");
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    private void crawlPhotoLapPlusShopInfo(String baseUrl, int maxPageNum) {
        log.info("====Start PhotoLapPlus Crawling===");
        try {
            for (int pageNum = 1; pageNum <= maxPageNum; pageNum++) {
                String url = baseUrl + pageNum;
                Document doc = etcBrandCrawlerService.connectToUrl(url);
                Elements elements = etcBrandCrawlerService.selectElements(doc,"div.Zc7IjY", url);

                for (Element element : elements) {
                    String placeName = element.select("h2 span.wixui-rich-text__text").text();
                    if (placeName.endsWith("점")) {
                        String roadAddressName = element.select("p span.wixui-rich-text__text").text();
                        roadAddressName = etcBrandCrawlerService.formatAddress(roadAddressName);

                        etcBrandCrawlerService.saveOrUpdateShop(placeName, roadAddressName);
                    }
                }
            }
            log.info("====End PhotoLapPlus Crawling===");
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    private void crawlPlayInTheBoxShopInfo(String baseUrl, int maxPageNum) {
        log.info("====Start PlayInTheBox Crawling===");
        try {
            for (int pageNum = 1; pageNum <= maxPageNum; pageNum++) {
                String url = baseUrl + pageNum;
                Document doc = etcBrandCrawlerService.connectToUrl(url);
                Elements elements = etcBrandCrawlerService.selectElements(doc, "div.map_contents.inline-blocked", url);

                for (Element element : elements) {
                    String placeName = element.select("a.map_link.blocked div.head div.tit").text();
                    if (placeName.endsWith("점")) {
                        String roadAddressName = element.selectFirst("div.p_group p").text();
                        roadAddressName = etcBrandCrawlerService.formatAddress(roadAddressName);

                        etcBrandCrawlerService.saveOrUpdateShop(placeName, roadAddressName);
                    }
                }
            }
            log.info("====End PlayInTheBox Crawling===");
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    private void crawlHarryPhotoShopInfo(String url) {
        log.info("====Start HarryPhoto Crawling===");
        try {
            Document  doc = etcBrandCrawlerService.connectToUrl(url);
            Elements elements = etcBrandCrawlerService.selectElements(doc, "dl", url);

            for (Element element : elements) {
                String placeName = element.select("dt").text();
                if (placeName.endsWith("점")) {
                    String roadAddressName = element.selectFirst("dd").text();
                    etcBrandCrawlerService.saveOrUpdateShop(placeName, roadAddressName);
                }
            }
            log.info("====End HarryPhoto Crawling===");
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }
}