package com.heybro.heybro.skin.controller;

import com.heybro.heybro.skin.dto.request.DeviceRequestDto;
import com.heybro.heybro.skin.dto.response.SkinAnalysisDataResponseDto;
import com.heybro.heybro.skin.dto.response.SkinTypeResponseDto;
import com.heybro.heybro.skin.service.SkinAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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

    @Operation(summary = "AI 피부 타입 분석")
    @PostMapping("/analyze-type")
    public SkinTypeResponseDto analyzeJustSkinType(@RequestPart("image") MultipartFile image) {
        return skinAnalysisService.analyzeSkinTypeOnly(image);
    }
}