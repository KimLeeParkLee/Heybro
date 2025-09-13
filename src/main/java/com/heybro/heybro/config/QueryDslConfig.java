package com.heybro.heybro.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryDslConfig {

    @PersistenceContext // JPA의 EntityManager를 주입 받기
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        // EntityManager를 사용하여 JPAQueryFactory를 생성하고 빈으로 반환
        return new JPAQueryFactory(entityManager);
    }
}