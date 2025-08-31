package com.heybro.heybro.voice.controller;

import com.heybro.heybro.voice.dto.request.TextRequestDto;
import com.heybro.heybro.voice.dto.response.TextResponseDto;
import com.heybro.heybro.voice.service.VoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/voice")
public class VoiceController {

    private final VoiceService voiceService;

    public VoiceController(VoiceService voiceService) {
        this.voiceService = voiceService;
    }

    @PostMapping("/chat-text")
    public ResponseEntity<TextResponseDto> handleTextChat(@RequestBody TextRequestDto requestDto) {
        String aiResponse = voiceService.processTextCommand(requestDto.userText());
        return ResponseEntity.ok(new TextResponseDto(aiResponse));
    }
}
