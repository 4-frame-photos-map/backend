package com.idea5.four_cut_photos_map.global.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Slf4j
public class DistanceFormatTest {

    @DisplayName("km Distance 포멧")
    @Test
    void kmDistance() {
        // given
        String kmTest1 = "2000"; // 2km
        String kmTest2 = "2300"; // 2.3km
        String kmTest3 = "2320"; // 2.3km
        String kmTest4 = "2350"; // 2.3km
        String kmTest5 = "2480"; // 2.5km

        // when
        String test1 = Util.distanceFormatting(kmTest1);
        String test2 = Util.distanceFormatting(kmTest2);
        String test3 = Util.distanceFormatting(kmTest3);
        String test4 = Util.distanceFormatting(kmTest4);
        String test5 = Util.distanceFormatting(kmTest5);
        // then
        assertAll(
                ()->assertThat(test1).isEqualTo("2km"),
                ()->assertThat(test2).isEqualTo("2.3km"),
                ()->assertThat(test3).isEqualTo("2.3km"),
                ()->assertThat(test4).isEqualTo("2.3km"),
                ()->assertThat(test5).isEqualTo("2.5km")
        );
    }

    @DisplayName("m Distance 포멧")
    @Test
    void mDistance() {
        // given
        String kmTest1 = "200"; // 200m
        String kmTest2 = "210"; // 210m
        String kmTest3 = "220"; // 220m
        String kmTest4 = "230"; // 230m
        String kmTest5 = "123"; // 123m
        String kmTest6= "1"; // 1m
        String kmTest7= "23"; // 23m
        String kmTest8= "0"; // 20

        // when
        String test1 = Util.distanceFormatting(kmTest1);
        String test2 = Util.distanceFormatting(kmTest2);
        String test3 = Util.distanceFormatting(kmTest3);
        String test4 = Util.distanceFormatting(kmTest4);
        String test5 = Util.distanceFormatting(kmTest5);
        String test6 = Util.distanceFormatting(kmTest6);
        String test7 = Util.distanceFormatting(kmTest7);
        String test8 = Util.distanceFormatting(kmTest8);
        // then
        assertAll(
                ()->assertThat(test1).isEqualTo("200m"),
                ()->assertThat(test2).isEqualTo("210m"),
                ()->assertThat(test3).isEqualTo("220m"),
                ()->assertThat(test4).isEqualTo("230m"),
                ()->assertThat(test5).isEqualTo("123m"),
                ()->assertThat(test6).isEqualTo("1m"),
                ()->assertThat(test7).isEqualTo("23m"),
                ()->assertThat(test8).isEqualTo("0m")
        );
    }
    @DisplayName("distance가 5자리 이상일 경우")
    @Test
    void distanceLength_is_greater_than_5() {
        // given
        String distance1 = "23200"; // 23km
        String distance2 = "232000"; // 232km
        // when

        // 변수명 = Util.distanceFormatting(변수명)을 안하는 이유 : https://madplay.github.io/post/effectively-final-in-java
        String firstDistance = Util.distanceFormatting(distance1);
        String secondDistance = Util.distanceFormatting(distance2);
        // then
        assertAll(
                () -> assertThat(firstDistance).isEqualTo("23km"),
                () -> assertThat(secondDistance).isEqualTo("232km")
                );
    }
}
