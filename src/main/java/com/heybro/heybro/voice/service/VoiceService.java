package com.heybro.heybro.voice.service;

import org.springframework.stereotype.Service;

@Service
public class VoiceService {

    private final GeminiService geminiService;

    public VoiceService(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    /**
     * 텍스트를 받아 AI의 텍스트 답변을 반환하는 메소드
     */
    public String processTextCommand(String userText) {
        System.out.println("사용자 텍스트 ->: " + userText);
        String aiResponseText = geminiService.getAiResponse(userText);
        System.out.println("AI 텍스트 답변: " + aiResponseText);
        return aiResponseText;
    }
}
