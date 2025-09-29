package com.heybro.heybro.voice.service;

import com.heybro.heybro.voice.dto.request.GeminiRequestDto;
import com.heybro.heybro.voice.dto.response.GeminiResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GeminiService {

    private final WebClient webClient;
    private final String geminiApiKey;
    private final String systemPrompt; // 시스템 프롬프트를 주입받을 필드

    /**
     * 생성자에서 WebClient, API 키, 시스템 프롬프트를 주입받습니다.
     * @param webClientBuilder WebClient 생성을 위한 빌더
     * @param geminiApiKey application.yml에 설정된 Gemini API 키
     * @param systemPrompt application.yml에 설정된 시스템 프롬프트
     */
    public GeminiService(WebClient.Builder webClientBuilder,
                         @Value("${google.gemini.api-key}") String geminiApiKey,
                         @Value("${google.gemini.prompt.system}") String systemPrompt) {
        this.webClient = webClientBuilder.baseUrl("https://generativelanguage.googleapis.com").build();
        this.geminiApiKey = geminiApiKey;
        this.systemPrompt = systemPrompt; // 주입받은 프롬프트를 필드에 저장
    }

    /**
     * 사용자 텍스트를 받아 Gemini API에 질의하고 "브로" 스타일의 답변을 반환합니다.
     * @param userText 사용자의 질문
     * @return AI가 생성한 답변 문자열
     */
    public String getAiResponse(String userText) {
        // 시스템 프롬프트와 사용자 텍스트를 포함한 요청 DTO를 생성합니다.
        var requestDto = GeminiRequestDto.createWithSystemPrompt(systemPrompt, userText);

        // API 호출
        GeminiResponseDto response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/gemini-2.5-flash:generateContent")
                        .queryParam("key", geminiApiKey)
                        .build())
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(GeminiResponseDto.class)
                .block(); // 동기적으로 결과를 기다림

        // 응답 유효성 검사
        if (response == null || response.candidates() == null || response.candidates().isEmpty() ||
                response.candidates().get(0).content() == null || response.candidates().get(0).content().parts().isEmpty()) {
            throw new RuntimeException("Gemini API로부터 유효한 응답을 받지 못했습니다.");
        }

        // 최종 텍스트 답변 반환
        return response.candidates().get(0).content().parts().get(0).text();
    }
}
