package com.heybro.heybro.skin.controller;

import com.heybro.heybro.skin.dto.request.DeviceRequestDto;
import com.heybro.heybro.skin.dto.response.SkinAnalysisDataResponseDto;
import com.heybro.heybro.skin.dto.response.SkinScoreResponseDto;
import com.heybro.heybro.skin.service.SkinAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/skins")
@Tag(name = "피부 타입 검사", description = "피부 타입 검사 API")
@RequiredArgsConstructor
public class SkinAnalysisController {
    private final SkinAnalysisService skinAnalysisService;

    @Operation(summary = "AI 피부 진단 실행")
    @PostMapping(path = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public SkinAnalysisDataResponseDto analyzeSkin(
            @RequestPart("image") MultipartFile image,
            @RequestPart("device") DeviceRequestDto device) {
        return skinAnalysisService.analyzeWithFacePlusPlus(image, device);
    }

    @Operation(summary = "AI 피부 진단 점수 조회")
    @GetMapping("/analyze")
    public SkinScoreResponseDto getSkinScore(@AuthenticationPrincipal UserDetails userDetails) {
        return skinAnalysisService.getSkinScore(userDetails.getUsername());
    }
}