package com.heybro.heybro.voice.service;

import com.heybro.heybro.voice.dto.request.GeminiRequestDto;
import com.heybro.heybro.voice.dto.response.GeminiResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class GeminiService {

    private final WebClient webClient;
    private final String geminiApiKey;

    public GeminiService(WebClient.Builder webClientBuilder, @Value("${google.gemini.api-key}") String geminiApiKey) {
        this.webClient = webClientBuilder.baseUrl("https://generativelanguage.googleapis.com").build();
        this.geminiApiKey = geminiApiKey;
    }

    public String getAiResponse(String userText) {
        // Gemini API 요청 본문 생성
        var requestDto = new GeminiRequestDto(
                List.of(new GeminiRequestDto.Content(
                        List.of(new GeminiRequestDto.Part(userText))
                ))
        );

        // API 호출
        GeminiResponseDto response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-pro:generateContent")
                        .queryParam("key", geminiApiKey)
                        .build())
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(GeminiResponseDto.class)
                .block(); // 동기적으로 결과를 기다림

        if (response == null || response.candidates() == null || response.candidates().isEmpty() ||
                response.candidates().get(0).content() == null || response.candidates().get(0).content().parts().isEmpty()) {
            throw new RuntimeException("Failed to get a valid response from Gemini API.");
        }

        return response.candidates().get(0).content().parts().get(0).text();
    }
}
