package com.heybro.heybro.skin.service;

import com.heybro.heybro.skin.dto.request.DeviceRequestDto;
import com.heybro.heybro.skin.dto.response.FaceppResponseDto;
import com.heybro.heybro.skin.dto.response.SkinAnalysisDataResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SkinAnalysisService {

    @Value("${facepp.api.key}")
    private String apiKey;

    @Value("${facepp.api.secret}")
    private String apiSecret;

    private final String FACEPP_API_URL = "https://api-us.faceplusplus.com/skinstatus/v2/skinstatus";

    private final RestTemplate restTemplate; // Bean으로 주입받음

    public SkinAnalysisDataResponseDto analyzeWithFacePlusPlus(MultipartFile image, DeviceRequestDto device) {
        // 1. Face++ API로 보낼 요청 준비
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("api_key", apiKey);
        body.add("api_secret", apiSecret);
        // 필요한 속성들을 명시적으로 요청 (문서 참고)
        body.add("return_attributes", "skinstatus");
        body.add("return_attributes", "skinstatus,beauty,facequality");

        try {
            body.add("image_file", new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("이미지 파일 처리 중 오류 발생", e);
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 2. Face++ API 호출 및 응답 받기
        ResponseEntity<FaceppResponseDto> responseEntity = restTemplate.postForEntity(FACEPP_API_URL, requestEntity, FaceppResponseDto.class);
        FaceppResponseDto faceppResponse = responseEntity.getBody();

        if (faceppResponse == null || faceppResponse.getFaces() == null || faceppResponse.getFaces().isEmpty()) {
            throw new RuntimeException("Face++ API에서 얼굴을 감지하지 못했습니다.");
        }

        FaceppResponseDto.Attributes attributes = faceppResponse.getFaces().get(0).getAttributes();

        return buildSkinAnalysisData(attributes);
    }

    // 매핑 로직을 별도 메서드로 분리
    private SkinAnalysisDataResponseDto buildSkinAnalysisData(FaceppResponseDto.Attributes attributes) {
        FaceppResponseDto.SkinStatus skinStatus = attributes.getSkinstatus();

        // Quality 객체 생성 및 값 설정
        SkinAnalysisDataResponseDto.Quality quality = new SkinAnalysisDataResponseDto.Quality();
        if (attributes.getFacequality() != null) {
            // Face++의 illumination 값을 0~100 스케일로 변환 (문서 기준: 0~255)
            quality.setLighting_score((int) (attributes.getFacequality().getOrDefault("illumination", 0.0) / 255.0 * 100.0));
        }
        if (attributes.getBeauty() != null) {
            // makeup 값이 일정 수준 이상이면 true로 판단
            quality.setMakeup_detected(attributes.getBeauty().getOrDefault("makeup", 0.0) > 50);
        }

        SkinAnalysisDataResponseDto.Metrics metrics = new SkinAnalysisDataResponseDto.Metrics();

        // 각 지표를 채우는 로직
        metrics.setOiliness(createMetricDetail(skinStatus.getOiliness()));
        metrics.setHydration(createMetricDetail(skinStatus.getHydration()));
        metrics.setRedness(createMetricDetail(skinStatus.getRedness()));
        metrics.setPore_visibility(createMetricDetail(skinStatus.getPore()));
        metrics.setAcne(createMetricDetail(skinStatus.getAcne()));
        metrics.setWrinkle(createMetricDetail(skinStatus.getWrinkle()));

        // 최종 응답 DTO 빌드
        return SkinAnalysisDataResponseDto.builder()
                .measurement_id("msr_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .provider("facepp")
                .model_version("facepp-2025-08")
                //.createdAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)) // 주석 해제
                .quality(quality) // 완성된 quality 객체 설정
                .metrics(metrics)
                .skin_type(determineSkinType(metrics))
                .by_region(null)
                .cached(false)
                .build();
    }

    // Face++ 점수(0~100)를 score, level, confidence로 변환하는 헬퍼 메서드
    private SkinAnalysisDataResponseDto.MetricDetail createMetricDetail(double score) {
        SkinAnalysisDataResponseDto.MetricDetail detail = new SkinAnalysisDataResponseDto.MetricDetail();
        detail.setScore((int) Math.round(score));
        detail.setLevel(calculateLevel((int) Math.round(score)));
        // Face++가 신뢰도를 제공하지 않는 경우, 고정값 또는 다른 로직으로 설정
        detail.setConfidence(0.85); // 예시 고정값
        return detail;
    }

    // 점수를 low/medium/high 구간으로 변환하는 헬퍼 메서드
    private String calculateLevel(int score) {
        if (score < 34) {
            return "low";
        } else if (score < 67) {
            return "medium";
        } else {
            return "high";
        }
    }

    // 지표를 기반으로 피부 타입을 추정하는 로직 (예시)
    private List<String> determineSkinType(SkinAnalysisDataResponseDto.Metrics metrics) {
        if (metrics.getOiliness().getScore() > 60 && metrics.getHydration().getScore() < 50) {
            return List.of("oily", "dehydrated");
        } else if (metrics.getOiliness().getScore() > 60) {
            return List.of("oily");
        } else {
            return List.of("normal");
        }
        // ... 더 정교한 로직 추가 가능
    }
}