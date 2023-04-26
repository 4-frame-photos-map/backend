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
    }
}
