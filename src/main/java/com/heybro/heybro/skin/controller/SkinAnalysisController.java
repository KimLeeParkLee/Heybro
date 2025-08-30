package com.heybro.heybro.skin.controller;

import com.heybro.heybro.common.response.ApiResponse;
import com.heybro.heybro.skin.dto.request.DeviceRequestDto;
import com.heybro.heybro.skin.dto.response.SkinAnalysisDataResponseDto;
import com.heybro.heybro.skin.service.SkinAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/skins")
@RequiredArgsConstructor
public class SkinAnalysisController {

    private final SkinAnalysisService skinAnalysisService;

    @PostMapping(path = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "AI 피부 진단 실행")
    public ResponseEntity<ApiResponse<SkinAnalysisDataResponseDto>> analyzeSkin(
            @RequestPart("image") MultipartFile image,
            @RequestPart("device") DeviceRequestDto device) {

        SkinAnalysisDataResponseDto analysisData = skinAnalysisService.analyzeWithFacePlusPlus(image, device);

        // 표준 응답 DTO로 감싸서 반환
        ApiResponse<SkinAnalysisDataResponseDto> response = ApiResponse.success(analysisData);

        return ResponseEntity.ok(response);
    }
}