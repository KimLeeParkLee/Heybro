package com.heybro.heybro.voice.dto.response;

import java.util.List;

public record GeminiResponseDto(List<Candidate> candidates) {
    public static record Candidate(Content content) {}
    public static record Content(List<Part> parts) {}
    public static record Part(String text) {}
}
