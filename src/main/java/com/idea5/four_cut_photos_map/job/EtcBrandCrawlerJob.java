package com.idea5.four_cut_photos_map.job;

import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@Transactional
@RequiredArgsConstructor
public class EtcBrandCrawlerJob {
    private final ShopRepository shopRepository;
    private final BrandRepository brandRepository;

    @Scheduled(cron = "0 0 3 2 5,11 *") // 매년 5월과 11월 2일 새벽 3시 실행
    public void crawlShopInfo() {
        // InsPhoto 크롤링
        String url = "https://insphoto.co.kr/locations/";
        crawlInsPhotoShopInfo(url);

        // Selpix 크롤링
        url = "http://m.selpix.co.kr/shop_add_page/index.htm?page_code=page16&me_popup=1";
        crawlSelpixShopInfo(url);

        // PhotoStreet 크롤링
        int maxPageNum = 5;
        String baseUrl = "https://photostreet.co.kr/?page_id=930&mode=list&board_page=";
        crawlPhotoStreetShopInfo(baseUrl, maxPageNum);

        // PhotoDrink 크롤링
        url = "https://photodrink.com/LOCATION";
        crawlPhotoDrinkShopInfo(url);

        // PhotoLapPlus 크롤링
        maxPageNum = 8;
        baseUrl = "https://www.photolabplus.co.kr/";
        crawlPhotoLapPlusShopInfo(baseUrl, maxPageNum);

        // PlayInTheBox 크롤링
        maxPageNum = 3;
        baseUrl = "https://www.playinthebox.co.kr/39/?sort=NAME&keyword_type=all&page=";
        crawlPlayInTheBoxShopInfo(baseUrl, maxPageNum);

        // HarryPhoto 크롤링
        url = "http://www.harryphoto.co.kr/";
        crawlHarryPhotoShopInfo(url);
    }

    private void crawlInsPhotoShopInfo(String url) {
        log.info("====Start InsPhoto Crawling===");
        try {
            Document doc = Jsoup.connect(url).get();
            if (doc == null) {
                log.error("Failed to parse the document from the URL: {}", url);
                return;
            }

            Elements elements = doc.select("div.container div p:has(span:first-child)");
            if (elements == null || elements.isEmpty()) {
                log.error("No elements were found with the specified selector: {}", url);
                return;
            }

            for (Element element : elements) {
                String placeName = element.selectFirst("span").text();
                if (placeName.endsWith("점")) {
                    placeName = "인스포토" + " " + placeName; // 브랜드명 추가
                    String roadAddressName = element.selectFirst("span:last-child").text();

                    saveOrUpdateShop(placeName, formatAddress(roadAddressName));
                }
            }
            log.info("====End InsPhoto Crawling===");
        } catch (IOException e) {
            log.error("Failed to connect to the URL: {}", e.getMessage());
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    private void crawlSelpixShopInfo(String url) {
        log.info("====Start Selpix Crawling===");
        try {
            Document doc = Jsoup.connect(url).get();
            if (doc == null) {
                log.error("Failed to parse the document from the URL: {}", url);
                return;
            }

            Elements elements = doc.select("span.subject");
            if (elements == null || elements.isEmpty()) {
                log.warn("No elements were found with the specified selector: {}", url);
                return;
            }

            for (Element element : elements) {
                String placeName = element.text();
                if (placeName.endsWith("점")) {
                    placeName = placeName.contains(" ") ? placeName.split(" ")[1] : placeName; // 지역명 분리
                    placeName = "셀픽스" + " " + placeName; // 브랜드명 추가

                    createShopIfNotExists(placeName);
                }
            }
            log.info("====End Selpix Crawling===");
        } catch (IOException e) {
            log.error("Failed to connect to the URL: {}", e.getMessage());
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    private void crawlPhotoStreetShopInfo(String baseUrl, int maxPageNum) {
        log.info("====Start PhotoStreet Crawling===");
        try {
            for (int pageNum = 1; pageNum <= maxPageNum; pageNum++) {
                String url = baseUrl + pageNum;
                Document doc = Jsoup.connect(url).get();
                if (doc == null) {
                    log.error("Failed to parse the document from the URL: {}", url);
                    return;
                }

                Elements elements = doc.select("td.text-left a span");
                if (elements == null || elements.isEmpty()) {
                    log.warn("No elements were found with the specified selector: {}", url);
                    continue;
                }

                for (Element element : elements) {
                    String placeName = element.text();
                    placeName = placeName.contains("(") ? placeName.split("\\(")[0] : placeName;

                    if (!placeName.contains("오픈예정")) {
                        // 지역명과 점포번호 분리
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
                }
            }
            log.info("====End PhotoStreet Crawling===");
        } catch (IOException e) {
            log.error("Failed to connect to the URL: {}", e.getMessage());
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    private void crawlPhotoDrinkShopInfo(String url) {
        log.info("====Start PhotoDrink Crawling===");
        try {
            Document doc = Jsoup.connect(url).get();
            if (doc == null) {
                log.error("Failed to parse the document from the URL: {}", url);
                return;
            }

            Elements elements = doc.select("div.text-table div");
            if (elements == null || elements.isEmpty()) {
                log.warn("No elements were found with the specified selector: {}", url);
                return;
            }

            for (Element element : elements) {
                Elements pElement = element.select("p");
                String placeName = pElement.get(0).select("span").text();

                if (placeName.endsWith("점")) {
                    String roadAddressName = pElement.get(1).select("span").text();

                    // 두번째 공백이 있다면 첫번째 공백 이후부터 두번째 공백 시작까지 제거
                    // 세번째 공백이 있다면 세번째 공백 제거
                    int firstIndex = placeName.indexOf(" ");
                    if (firstIndex != -1) {
                        int secondIndex = placeName.indexOf(" ", firstIndex + 1);
                        if (secondIndex != -1) {
                            placeName = placeName.substring(0, firstIndex + 1) + placeName.substring(secondIndex + 1);
                            int thirdIndex = placeName.indexOf(" ", secondIndex + 1);
                            if (thirdIndex != -1) {
                                placeName = placeName.substring(0, secondIndex + 1) + placeName.substring(thirdIndex + 1);
                            }
                        }

                        saveOrUpdateShop(placeName, formatAddress(roadAddressName));
                    }
                }
            }
            log.info("====End PhotoDrink Crawling===");
        } catch (IOException e) {
            log.error("Failed to connect to the URL: {}", e.getMessage());
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    private void crawlPhotoLapPlusShopInfo(String baseUrl, int maxPageNum) {
        log.info("====Start PhotoLapPlus Crawling===");
        try {
            for (int pageNum = 1; pageNum <= maxPageNum; pageNum++) {
                String url = baseUrl + "location1-" + pageNum;
                Document doc = null;
                try {
                    doc = Jsoup.connect(url).get();
                    if (doc == null) {
                        log.error("Failed to parse the document from the URL: {}", url);
                        return;
                    }
                } catch (HttpStatusException e) {
                    url = baseUrl + "1-" + pageNum;
                    doc = Jsoup.connect(url).get();
                }

                Elements elements = doc.select("div.Zc7IjY");
                if (elements == null || elements.isEmpty()) {
                    log.warn("No elements were found with the specified selector: {}", url);
                    continue;
                }

                for (Element element : elements) {
                    String placeName = element.select("h2 span.wixui-rich-text__text").text();
                    if (placeName.endsWith("점")) {
                        String roadAddressName = element.select("p span.wixui-rich-text__text").text();
                        saveOrUpdateShop(placeName, formatAddress(roadAddressName));
                    }
                }
            }
            log.info("====End PhotoLapPlus Crawling===");
        } catch (IOException e) {
            log.error("Failed to connect to the URL: {}", e.getMessage());
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    private void crawlPlayInTheBoxShopInfo(String baseUrl, int maxPageNum) {
        log.info("====Start PlayInTheBox Crawling===");
        try {
            for (int pageNum = 1; pageNum <= maxPageNum; pageNum++) {
                String url = baseUrl + pageNum;
                Document  doc = Jsoup.connect(url).get();
                if (doc == null) {
                    log.error("Failed to parse the document from the URL: {}", url);
                    return;
                }

                Elements elements = doc.select("div.map_contents.inline-blocked");
                if (elements == null || elements.isEmpty()) {
                    log.warn("No elements were found with the specified selector: {}", url);
                    continue;
                }

                for (Element element : elements) {
                    String placeName = element.select("a.map_link.blocked div.head div.tit").text();
                    if (placeName.endsWith("점")) {
                        String roadAddressName = element.selectFirst("div.p_group p").text();

                        saveOrUpdateShop(placeName, formatAddress(roadAddressName));
                    }
                }
            }
            log.info("====End PlayInTheBox Crawling===");
        } catch (IOException e) {
            log.error("Failed to connect to the URL: {}", e.getMessage());
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    private void crawlHarryPhotoShopInfo(String url) {
        log.info("====Start HarryPhoto Crawling===");
        try {
            Document  doc = Jsoup.connect(url).get();
            if (doc == null) {
                log.error("Failed to parse the document from the URL: {}", url);
                return;
            }

            Elements elements = doc.select("dl");
            if (elements == null || elements.isEmpty()) {
                log.warn("No elements were found with the specified selector: {}", url);
                return;
            }

            for (Element element : elements) {
                String placeName = element.select("dt").text();
                if (placeName.endsWith("점")) {
                    String roadAddressName = element.selectFirst("dd").text();
                    saveOrUpdateShop(placeName, roadAddressName);
                }
            }
            log.info("====End HarryPhoto Crawling===");
        } catch (IOException e) {
            log.error("Failed to connect to the URL: {}", e.getMessage());
        } catch (Exception e) {
            log.error("An error occurred during the crawling process: {}", e.getMessage());
        }
    }

    private String formatAddress(String roadAddressName) {
        if(roadAddressName.contains("대한민국 ")) {
            roadAddressName.replace("대한민국 ", "");
        }

        if(roadAddressName.contains(",")) {
            roadAddressName = roadAddressName.split(",")[0];
        }


        if (roadAddressName.matches(".*\\d.*")) {
            // 마지막 숫자 이후 문자열 제거
            int lastIndex = roadAddressName.replaceAll("[^\\d]+$", "").length();
            roadAddressName = roadAddressName.substring(0, lastIndex);
        }

        return roadAddressName;
    }

    private void saveOrUpdateShop(String placeName, String roadAddressName) {
        Shop oldShop = shopRepository.findByPlaceName(placeName).orElse(null);
        if (oldShop == null) {
            Shop shop = Shop.builder()
                    .brand(brandRepository.findByBrandName("기타").get())
                    .placeName(placeName)
                    .roadAddressName(roadAddressName)
                    .favoriteCnt(0)
                    .reviewCnt(0)
                    .starRatingAvg(0.0)
                    .build();
            shopRepository.save(shop);
            log.info("persist >> placeName:{}, roadAddressName:{}", placeName, roadAddressName);
        } else {
            String changedFields = "";
            if (!oldShop.getRoadAddressName().equals(roadAddressName)) {
                oldShop.setRoadAddressName(roadAddressName);
                changedFields += "roadAddressName, ";
            }
            if (!changedFields.equals("")) {
                changedFields = changedFields.substring(0, changedFields.length() - 2); // 마지막 쉼표 제거
                shopRepository.save(oldShop);
                log.info("merge >> id:{}, changed fields: {}", oldShop.getId(), changedFields);
            }
        }
    }

    private void createShopIfNotExists(String placeName) {
        if (!shopRepository.existsByPlaceName(placeName)) {
            Shop shop = Shop.builder()
                    .brand(brandRepository.findByBrandName("기타").get())
                    .placeName(placeName)
                    .roadAddressName(null)
                    .favoriteCnt(0)
                    .reviewCnt(0)
                    .starRatingAvg(0.0)
                    .build();
            shopRepository.save(shop);
            log.info("persist >> placeName:{}, roadAddressName:{}", placeName, null);
        }
    }
}