package com.idea5.four_cut_photos_map.domain.crawl;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PhotograyCrawlService implements CrawlService {
    public void crawl() {
        int cnt = 0;
        int page = 2;
        for(int i = 1; i <= page; i++) {
            String url = "http://photogray.com/bbs/board.php?bo_table=store&page=" + i;
            Connection conn = Jsoup.connect(url);

            try {
                Document document = conn.get();
                Elements titles = document.select("div.addr");

                for (Element e : titles) {
                    String placeName = "포토그레이 " + e.select("div.title").text().trim();
                    System.out.println(placeName);
                    String address = e.select("div.st_info").text().trim();
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
