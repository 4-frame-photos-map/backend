package com.idea5.four_cut_photos_map.global.util;

import com.google.common.base.CaseFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest    // JPA 테스트
class DatabaseCleanerTest {

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("EntityManager 로 영속성 컨텍스트의 모든 엔티티의 타입 출력하기")
    void t1() {
        entityManager.getMetamodel()
                .getEntities()  // 영속성 컨텍스트의 모든 엔티티 조회
                .stream()
                .map(e -> e.getJavaType())   // 엔티티의 타입(자바클래스) 반환
                .forEach(System.out::println);
    }

    @Test
    @DisplayName("EntityManager 로 영속성 컨텍스트의 모든 엔티티가 가진 어노테이션 출력하기")
    void t2() {
        entityManager.getMetamodel()
                .getEntities()  // 영속성 컨텍스트의 모든 엔티티 조회
                .stream()
                .map(e -> e.getJavaType().getAnnotations())   // 엔티티 타입(자바클래스)의 persistence 어노테이션 반환
                .forEach(e -> System.out.println(Arrays.toString(e)));
    }

    @Test
    @DisplayName("EntityManager 로 영속성 컨텍스트의 모든 엔티티의 이름 조회하기")
    void t3() {
        List<String> tableNames = entityManager.getMetamodel()
                .getEntities()  // 영속성 컨텍스트의 모든 엔티티 조회
                .stream()
                .map(EntityType::getName)   // 엔티티의 이름 반환
                .collect(Collectors.toList());

        assertThat(tableNames.size()).isEqualTo(7);
        assertThat(tableNames).contains("Member");
        assertThat(tableNames).contains("MemberTitle");
        assertThat(tableNames).contains("MemberTitleLog");
        assertThat(tableNames).contains("Shop");
        assertThat(tableNames).contains("ShopTitle");
        assertThat(tableNames).contains("ShopTitleLog");
        assertThat(tableNames).contains("Favorite");
    }

    @Test
    @DisplayName("EntityManager 로 영속성 컨텍스트의 모든 엔티티 조회하여 테이블명으로 변환하기1")
    void t4() {
        List<String> tableNames = entityManager.getMetamodel()
                .getEntities()  // 영속성 컨텍스트의 모든 엔티티 조회
                .stream()
                .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)   // @Entity 붙은 클래스 필터링(반드시 있어야하는지 의문)
                .map(e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getName())) // 클래스 이름을 DB 테이블명 생성 규칙에 따라 LOWER_UNDERSCORE 로 변환
                .collect(Collectors.toList());

        assertThat(tableNames.size()).isEqualTo(7);
        assertThat(tableNames).contains("member");
        assertThat(tableNames).contains("member_title");
        assertThat(tableNames).contains("member_title_log");
        assertThat(tableNames).contains("shop");
        assertThat(tableNames).contains("shop_title");
        assertThat(tableNames).contains("shop_title_log");
        assertThat(tableNames).contains("favorite");
    }

    @Test
    @DisplayName("EntityManager 로 영속성 컨텍스트의 모든 엔티티 조회하여 테이블명으로 변환하기2")
    void t5() {
        List<String> tableNames = entityManager.getMetamodel()
                .getEntities()  // 영속성 컨텍스트의 모든 엔티티 조회
                .stream()
                .map(e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getName())) // 엔티티의 이름을 DB 테이블명 생성 규칙에 따라 LOWER_UNDERSCORE 로 변환
                .collect(Collectors.toList());

        assertThat(tableNames.size()).isEqualTo(7);
        assertThat(tableNames).contains("member");
        assertThat(tableNames).contains("member_title");
        assertThat(tableNames).contains("member_title_log");
        assertThat(tableNames).contains("shop");
        assertThat(tableNames).contains("shop_title");
        assertThat(tableNames).contains("shop_title_log");
        assertThat(tableNames).contains("favorite");
    }
}