package com.idea5.four_cut_photos_map.domain.base.integration;

import com.google.common.base.CaseFormat;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Profile("test")
public class DatabaseCleanUp implements InitializingBean {
    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() throws Exception {
        tableNames = entityManager.getMetamodel().getEntities().stream()
                .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
                .map(e -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, e.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void execute() {
        /**
         * 데이터베이스 종류에 따라 해당 데이터베이스 문법 사용해야 함
         * H2 데이터베이스 문법 != MySQL 문법
         */

        /**
         * -- H2 DB
         * SET REFERENTIAL_INTEGRITY FALSE
         * TRUNCATE TABLE table_name
         * ALTER TABLE table_name ALTER COLUMN ID RESTART WITH 1
         * SET REFERENTIAL_INTEGRITY TRUE
         */

        entityManager.flush();
        // 참조 무결성 해제
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            // 데이터 삭제 후 ID 값을 1부터 시작할 수 있도록 기본 값 초기화
            entityManager.createNativeQuery("ALTER TABLE " + tableName + " AUTO_INCREMENT = 1").executeUpdate();
        }

        // 참조 무결성 설정
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }
}
