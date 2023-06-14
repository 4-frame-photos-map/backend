package com.idea5.four_cut_photos_map.global.util;

import com.google.common.base.CaseFormat;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 테스트 격리를 위해 모든 DB 테이블 truncate
 * @See <a href="https://hyeonic.github.io/woowacourse/dallog/test-isolation.html">TRUNCATE 방법</a>
 * @See <a href="https://dev-monkey-dugi.tistory.com/133#1.3.1.%20DatabaseCleanup">TRUNCATE 방법</a>
 */
@Component
@Profile("test")
public class DatabaseCleaner {
    private final EntityManager entityManager;
    private final List<String> tableNames;      // 영속성 컨텍스트에 등록된 모든 엔티티의 테이블명 목록

    public DatabaseCleaner(final EntityManager entityManager) {
        this.entityManager = entityManager;
        this.tableNames = entityManager.getMetamodel()
                .getEntities()  // 영속성 컨텍스트의 모든 엔티티 조회
                .stream()
                .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)   // @Entity 달린 클래스 필터링
                .map(e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getName())) // DB 테이블명 생성 규칙에 따라 LOWER_UNDERSCORE 로 변환
                .collect(Collectors.toList());
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        // 1. foreign key 제약 조건 off(foreign key 설정이 된 테이블 truncate 허용을 위해)
        entityManager.createNativeQuery("SET foreign_key_checks = 0").executeUpdate();
        // 2. 모든 테이블 truncate
        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
        }
        // 3. foreign key 제약 조건 on
        entityManager.createNativeQuery("SET foreign_key_checks = 1").executeUpdate();
    }
}
