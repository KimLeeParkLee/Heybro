package com.heybro.heybro.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${fcm.service-account-key-path}")
    private String serviceAccountKeyPath;

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        // application.yml 에서 설정한 키 파일 경로를 읽어옵니다.
        ClassPathResource resource = new ClassPathResource(serviceAccountKeyPath);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
                .build();

        // 이미 초기화된 앱이 없는 경우에만 초기화를 수행합니다.
        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }
}