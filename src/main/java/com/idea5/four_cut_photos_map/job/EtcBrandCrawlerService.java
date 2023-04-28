package com.idea5.four_cut_photos_map.job;

import com.idea5.four_cut_photos_map.domain.brand.repository.BrandRepository;
import com.idea5.four_cut_photos_map.domain.shop.entity.Shop;
import com.idea5.four_cut_photos_map.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EtcBrandCrawlerService {
    private final ShopRepository shopRepository;
    private final BrandRepository brandRepository;
    public static final int PHOTO_STREET_TOTAL_PAGE = 5;
    public static final int PHOTO_LAP_PLUS_TOTAL_PAGE = 8;
    public static final int PLAY_IN_THE_BOX_TOTAL_PAGE = 3;
    public static final String INS_PHOTO_URL = "https://insphoto.co.kr/locations/";
    public static final String SELPIX_URL = "http://m.selpix.co.kr/shop_add_page/index.htm?page_code=page16&me_popup=1";
    public static final String PHOTO_STREET_URL = "https://photostreet.co.kr/?page_id=930&mode=list&board_page=";
    public static final String PHOTO_DRINK_URL = "https://photodrink.com/LOCATION";
    public static final String PHOTO_LAP_PLUS_URL = "https://www.photolabplus.co.kr/location1-";
    public static final String PLAY_IN_THE_BOX_URL = "https://www.playinthebox.co.kr/39/?sort=NAME&keyword_type=all&page=";
    public static final String HARRY_PHOTO_URL = "http://www.harryphoto.co.kr/";


    public Document connectToUrl(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            if (doc == null) {
                log.error("Failed to parse the document from the URL: {}", url);
            }
        } catch (HttpStatusException e) {
            try {
                if (url.contains("photolapplus")) {
                    url = url.replaceFirst("/location\\d+-", "/1-");
                    doc = Jsoup.connect(url).get();
                } else {
                    log.error("Failed to connect to the URL: {}", e.getMessage());
                }
            } catch (IOException ex) {
                log.error("Failed to connect to the URL: {}", ex.getMessage());
            }
        } catch (IOException e) {
            log.error("Failed to connect to the URL: {}", e.getMessage());
        }
        return doc;
    }

    public Elements selectElements(Document doc, String selector, String url) {
        Elements elements = doc.select(selector);
        if (elements == null || elements.isEmpty()) {
            log.warn("No elements were found with the specified selector: {}", url);
        }
        return elements;
    }

    public String formatPlaceName(String placeName, String baseUrl) {
        switch (baseUrl) {
            case INS_PHOTO_URL -> placeName = "인스포토 " + placeName;
            case SELPIX_URL -> {
                // 지역명 분리
                placeName = placeName.contains(" ") ? placeName.split(" ")[1] : placeName;
                placeName = "셀픽스 " + placeName;
            }
            case PHOTO_STREET_URL -> {
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
            }
            case PHOTO_DRINK_URL -> {
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
                }
            }
        }
        return placeName;
    }

    public String formatAddress(String roadAddressName) {
        if (roadAddressName.contains("대한민국 ")) {
            roadAddressName.replace("대한민국 ", "");
        }

        if (roadAddressName.contains(",")) {
            roadAddressName = roadAddressName.split(",")[0];
        }


        if (roadAddressName.matches(".*\\d.*")) {
            // 마지막 숫자 이후 문자열 제거
            int lastIndex = roadAddressName.replaceAll("[^\\d]+$", "").length();
            roadAddressName = roadAddressName.substring(0, lastIndex);
        }

        return roadAddressName;
    }

    public void saveOrUpdateShop(String placeName, String roadAddressName) {
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
            if (!oldShop.getRoadAddressName().equals(roadAddressName)) {
                oldShop.setRoadAddressName(roadAddressName);
                log.info("merge >> id:{}, changed fields: {}", oldShop.getId(), roadAddressName);
            }
        }
    }

    public void createShopIfNotExists(String placeName) {
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
