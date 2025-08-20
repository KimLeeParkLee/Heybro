package com.heybro.heybro.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringdocConfig {

    @Bean
    public OpenAPI openAPI() {
        // 1. API 문서의 기본 정보 설정
        Info info = new Info()
                .title("Heybro Project API")
                .version("v1.0.0")
                .description("Heybro 프로젝트의 API 명세서입니다.");

        // 2. JWT 인증 설정을 위한 SecurityScheme 설정
        String jwtSchemeName = "JWT-Auth";
        SecurityScheme securityScheme = new SecurityScheme()
                .name(jwtSchemeName)
                .type(SecurityScheme.Type.HTTP) // HTTP 방식
                .scheme("bearer") // Bearer 토큰 방식
                .bearerFormat("JWT"); // 토큰 형식은 JWT

        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, securityScheme);

        // 3. Swagger UI에 "Authorize" 버튼과 자물쇠 아이콘 추가
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        // 4. 개발/운영 서버 URL 설정
        Server devServer = new Server().url("http://localhost:8080").description("개발 서버");
        Server prodServer = new Server().url("https://kimleeparklee.shop").description("운영 서버");


        return new OpenAPI()
                .addServersItem(devServer)
                .addServersItem(prodServer)
                .info(info)
                .components(components)
                .addSecurityItem(securityRequirement);
    }
}