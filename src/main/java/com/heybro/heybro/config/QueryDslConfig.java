package com.heybro.heybro.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 이 클래스가 스프링의 설정 파일임을 나타냅니다.
public class QueryDslConfig {

    @PersistenceContext // JPA의 EntityManager를 주입받습니다.
    private EntityManager entityManager;

    @Bean // 이 메서드가 반환하는 객체를 스프링 빈으로 등록합니다.
    public JPAQueryFactory jpaQueryFactory() {
        // EntityManager를 사용하여 JPAQueryFactory를 생성하고 빈으로 반환합니다.
        return new JPAQueryFactory(entityManager);
    }
}