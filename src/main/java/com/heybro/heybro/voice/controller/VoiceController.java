package com.heybro.heybro.voice.controller;

import com.heybro.heybro.common.advice.NoApiResponse;
import com.heybro.heybro.common.response.ApiResponse;
import com.heybro.heybro.voice.dto.request.TextRequestDto;
import com.heybro.heybro.voice.service.VoiceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@NoApiResponse
@RestController
@RequestMapping("/voice")
@RequiredArgsConstructor
@Tag(name = "톡봇", description = "톡봇 API")
public class VoiceController {

    private final VoiceService voiceService;

    @PostMapping("/chat-text")
    public ApiResponse<String> handleTextChat(@RequestBody TextRequestDto requestDto) {
        String aiResponse = voiceService.processTextCommand(requestDto.userText());
        return ApiResponse.success(aiResponse);
    }
}
