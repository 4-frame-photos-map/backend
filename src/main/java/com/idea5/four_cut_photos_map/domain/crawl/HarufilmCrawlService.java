package com.idea5.four_cut_photos_map.domain.crawl;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class HarufilmCrawlService implements CrawlService {
    public void crawl() {
        int cnt = 0;
        int[] codes = {202, 203, 204, 205, 206, 207, 208, 209};
        for(int code : codes) {
            String url = "http://harufilm.com/" + code;
            Connection conn = Jsoup.connect(url);

            try {
                Document document = conn.get();
                Elements titles = document.select("p.title");

                for (Element element : titles) {
                    String address = element.select("span.body").text().trim();
                    String placeNameAddress = element.text();
//                    String placeName = (placeNameAddress.substring(0, placeNameAddress.length() - address.length()));
                    String placeName = "하루필름 " + element.text().replace(address, "").trim();
                    System.out.println(address);
                    System.out.println(placeName);
                    cnt++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("cnt = " + cnt);
    }
}
