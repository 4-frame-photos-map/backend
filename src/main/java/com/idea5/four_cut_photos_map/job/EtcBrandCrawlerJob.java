package com.idea5.four_cut_photos_map.job;

import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@Transactional
@RequiredArgsConstructor
public class EtcBrandCrawlerJob {
    private final ShopRepository shopRepository;
    private final BrandRepository brandRepository;

    @Scheduled(cron = "0 0 3 2 6,12 *") // 매년 6월과 12월 2일 새벽 3시 실행
    public void getInsphotoHubInfo() {
        log.info("====Start Insphoto Crawling===");
        try {
            String url = "https://insphoto.co.kr/locations/";
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select("div.container div p");

            for (Element element : elements) {
                Element spanElement = element.selectFirst("span");
                if (spanElement == null || spanElement.text().isEmpty()) {
                    break;
                }

                String placeName = spanElement.text();
                String roadAddressName = element.select("span:last-child").text();
                placeName = roadAddressName.split(",")[1] + " " + placeName; // 브랜드명 추가
                roadAddressName = roadAddressName.split(",")[0]; // 상세주소 제거

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
            log.info("====End Insphoto Crawling===");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 3 2 6,12 *") // 매년 6월과 12월 2일 새벽 3시 실행
    public void getSelpixHubInfo(){
        log.info("====Start Selpix Crawling===");
        try {
            String url = "http://m.selpix.co.kr/shop_add_page/index.htm?page_code=page16&me_popup=1";
            Document doc = Jsoup.connect(url).get();
            Elements elements = doc.select("span.subject");

            for (Element element : elements) {
                if (element.text().endsWith("점")) {
                    String placeName = element.text();
                    placeName = placeName.contains(" ")? placeName.split(" ")[1] : placeName; // 지역명 분리
                    placeName = "셀픽스" + " " + placeName; // 브랜드명 추가

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
            log.info("====End Selpix Crawling===");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Scheduled(cron = "0 * * * * *")
    @Scheduled(cron = "0 0 3 2 6,12 *") // 매년 6월과 12월 2일 새벽 3시 실행
    public void getPhotostreetHubInfo(){
        log.info("====Start Photostreet Crawling===");

        try {
            int maxPageNum = 5;
            String baseUrl = "https://photostreet.co.kr/?page_id=930&mode=list&board_page=";

            for (int pageNum = 1; pageNum <= maxPageNum; pageNum++) {
                String url = baseUrl + pageNum;
                Document doc = Jsoup.connect(url).get();
                Elements elements = doc.select("td.text-left a span");

                for (Element element : elements) {
                    String placeName = element.text();
                    if (placeName.endsWith("점") && !placeName.endsWith("호점")) {
                        // 지역명과 점포번호 분리
                        String[] patterns = {"\\d+호\\s+(.*)", "\\d+호점\\s+(.*)"};
                        Pattern[] regexes = new Pattern[patterns.length];
                        for (int i = 0; i < patterns.length; i++) {
                            regexes[i] = Pattern.compile(patterns[i]);
                        }
                        for (Pattern regex : regexes) {
                            Matcher matcher = regex.matcher(placeName);
                            if (matcher.find()) {
                                placeName = matcher.group(1);
                                break;
                            }
                        }
                        // 공백이나 대괄호 포함하는 경우 제외
                        if (placeName.matches("^[^\\[\\]\\s]+$")) {
                            placeName = "포토스트리트" + " " + placeName; // 브랜드명 추가

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
                }
            }
            log.info("====End Photostreet Crawling===");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
