package com.heybro.heybro.voice.dto.request;

import java.util.List;

public record GeminiRequestDto(List<Content> contents) {
    public static record Content(List<Part> parts) {}
    public static record Part(String text) {}
}
