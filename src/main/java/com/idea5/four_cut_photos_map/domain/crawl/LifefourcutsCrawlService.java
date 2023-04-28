package com.idea5.four_cut_photos_map.domain.crawl;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LifefourcutsCrawlService implements CrawlService {
    public void crawl() {
        int cnt = 0;
        int page = 49;
        for(int i = 1; i <= page; i++) {
            String url = "https://lifefourcuts.com/Store01/?sort=TIME&keyword_type=all&page=" + i;
            Connection conn = Jsoup.connect(url);

            try {
                Document document = conn.get();
                Elements titles = document.select("div.map_contents.inline-blocked");

                for (Element e : titles) {
                    String placeName = "인생네컷 " + e.select("div.tit").text().trim();
                    System.out.println(placeName);
                    String address = e.select("p.adress").text().trim();
                    System.out.println(address);
                    cnt++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("cnt = " + cnt);
    }
}
