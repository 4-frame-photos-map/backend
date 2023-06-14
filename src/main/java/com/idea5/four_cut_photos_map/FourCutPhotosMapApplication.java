package com.idea5.four_cut_photos_map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing  // @CreatedDate, @LastModifiedDate 기능 사용
@EnableScheduling   // @Scheduled 스프링 스케줄러 사용
//@EnableCaching      //
public class FourCutPhotosMapApplication {

    public static void main(String[] args) {
        SpringApplication.run(FourCutPhotosMapApplication.class, args);
    }

}
