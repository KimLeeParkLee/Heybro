package com.heybro.heybro.voice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GeminiRequestDto(
        List<Content> contents,
        @JsonProperty("system_instruction")
        Content systemInstruction
) {
    public record Content(List<Part> parts) {}
    public record Part(String text) {}

    public static GeminiRequestDto createWithSystemPrompt(String systemPrompt, String userText) {
        var systemContent = new Content(List.of(new Part(systemPrompt)));
        var userContent = new Content(List.of(new Part(userText)));
        return new GeminiRequestDto(List.of(userContent), systemContent);
    }
}
