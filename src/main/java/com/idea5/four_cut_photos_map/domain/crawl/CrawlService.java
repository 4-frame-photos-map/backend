package com.idea5.four_cut_photos_map.domain.crawl;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CrawlService {
    public void htmlCrawl() {
        String url = "http://harufilm.com/202";
        Connection conn = Jsoup.connect(url);

        try {
            Document document = conn.get();
            Elements titles = document.select("p.title");

            for (Element element : titles) {
                System.out.println(element.text());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    public void lifefourcutsCrawl() {
        int cnt = 0;
        for(int i = 1; i <= 49; i++) {
            String url = "https://lifefourcuts.com/Store01/?sort=UPDATE&keyword_type=all&page=" + i;
            Connection conn = Jsoup.connect(url);

            try {
                Document document = conn.get();
                Elements titles = document.select(".head > div.tit");

                for (Element element : titles) {
                    System.out.println(element.text());
                    cnt++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("cnt = " + cnt);
    }
}
