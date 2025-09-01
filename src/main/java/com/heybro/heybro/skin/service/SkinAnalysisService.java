package com.heybro.heybro.skin.service;

import com.heybro.heybro.skin.domain.SkinDiagnosis;
import com.heybro.heybro.skin.dto.request.DeviceRequestDto;
import com.heybro.heybro.skin.dto.response.FaceppResponseDto;
import com.heybro.heybro.skin.dto.response.FaceppSkinTypeResponseDto;
import com.heybro.heybro.skin.dto.response.SkinAnalysisDataResponseDto;
import com.heybro.heybro.skin.dto.response.SkinTypeResponseDto;
import com.heybro.heybro.skin.repository.SkinDiagnosisRepository;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SkinAnalysisService {
    @Value("${facepp.api.key}")
    private String apiKey;

    @Value("${facepp.api.secret}")
    private String apiSecret;

    private final String FACEPP_API_URL = "https://api-us.faceplusplus.com/facepp/v1/skinanalyze";

    private final RestTemplate restTemplate;

    private final SkinDiagnosisRepository skinDiagnosisRepository;
    private final UserRepository userRepository;

    @Transactional
    public SkinAnalysisDataResponseDto analyzeWithFacePlusPlus(MultipartFile image, DeviceRequestDto device) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails) principal;
        String username = userDetails.getUsername();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("api_key", apiKey);
        body.add("api_secret", apiSecret);

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
        ResponseEntity<FaceppResponseDto> responseEntity = restTemplate.postForEntity(FACEPP_API_URL, requestEntity, FaceppResponseDto.class);
        FaceppResponseDto faceppResponse = responseEntity.getBody();

        if (faceppResponse == null || faceppResponse.getResult() == null) {
            throw new RuntimeException("Face++ API에서 분석 결과를 받지 못했습니다.");
        }

        SkinAnalysisDataResponseDto skinAnalysisData = buildSkinAnalysisData(faceppResponse.getResult());

        SkinDiagnosis skinDiagnosis = SkinDiagnosis.builder()
                .user(user)
                .diagnosisDate(LocalDateTime.now())
                .finalScore(skinAnalysisData.getFinal_score())
                .oilinessScore(skinAnalysisData.getMetrics().getOiliness().getScore())
                .hydrationScore(skinAnalysisData.getMetrics().getHydration().getScore())
                .poreScore(skinAnalysisData.getMetrics().getPoreVisibility().getScore())
                .acneSocre(skinAnalysisData.getMetrics().getAcne().getScore())
                .skinType(String.join(",", skinAnalysisData.getSkin_type()))
                .build();

        skinDiagnosisRepository.save(skinDiagnosis);

        return skinAnalysisData;
    }

    private SkinAnalysisDataResponseDto buildSkinAnalysisData(FaceppResponseDto.ResultData resultData) {
        // 1. Get raw values for core metrics
        double acneRaw = resultData.getAcne() != null ? resultData.getAcne().getValue() : 0;
        double blackheadRaw = resultData.getBlackhead() != null ? resultData.getBlackhead().getValue() : 0;
        double skinSpotRaw = resultData.getSkinSpot() != null ? resultData.getSkinSpot().getValue() : 0;
        double darkCircleRaw = resultData.getDarkCircle() != null ? resultData.getDarkCircle().getValue() : 0;

        double poresLeftCheek = resultData.getPores_left_cheek() != null ? resultData.getPores_left_cheek().getValue() : 0;
        double poresForehead = resultData.getPores_forehead() != null ? resultData.getPores_forehead().getValue() : 0;
        double poresJaw = resultData.getPores_jaw() != null ? resultData.getPores_jaw().getValue() : 0;
        double poresRightCheek = resultData.getPores_right_cheek() != null ? resultData.getPores_right_cheek().getValue() : 0;
        double averagePoreRaw = (poresLeftCheek + poresForehead + poresJaw + poresRightCheek) / 4.0;

        double foreheadWrinkle = resultData.getForehead_wrinkle() != null ? resultData.getForehead_wrinkle().getValue() : 0;
        double crowsFeet = resultData.getCrows_feet() != null ? resultData.getCrows_feet().getValue() : 0;
        double eyeFinelines = resultData.getEye_finelines() != null ? resultData.getEye_finelines().getValue() : 0;
        double glabellaWrinkle = resultData.getGlabella_wrinkle() != null ? resultData.getGlabella_wrinkle().getValue() : 0;
        double nasolabialFold = resultData.getNasolabial_fold() != null ? resultData.getNasolabial_fold().getValue() : 0;
        double averageWrinkleRaw = (foreheadWrinkle + crowsFeet + eyeFinelines + glabellaWrinkle + nasolabialFold) / 5.0;

        // 2. Determine oiliness and hydration based on skin type
        int apiSkinType = resultData.getSkinType() != null ? resultData.getSkinType().getSkinType() : 2; // Default to normal
        double oilinessRaw = 0;
        double hydrationRaw = 0; // Represents lack of hydration

        switch (apiSkinType) {
            case 0: // oily
                oilinessRaw = 0.8;
                hydrationRaw = 0.4;
                break;
            case 1: // dry
                oilinessRaw = 0.2;
                hydrationRaw = 0.8;
                break;
            case 3: // combination
                oilinessRaw = 0.6;
                hydrationRaw = 0.6;
                break;
            case 2: // normal
            default:
                oilinessRaw = 0.4;
                hydrationRaw = 0.4;
                break;
        }

        // 3. Create MetricDetail objects with severity scores (higher = worse)
        SkinAnalysisDataResponseDto.Metrics metrics = SkinAnalysisDataResponseDto.Metrics.builder()
                .acne(createMetricDetail(acneRaw))
                .poreVisibility(createMetricDetail(averagePoreRaw))
                .blackhead(createMetricDetail(blackheadRaw))
                .wrinkle(createMetricDetail(averageWrinkleRaw))
                .oiliness(createMetricDetail(oilinessRaw))
                .hydration(createMetricDetail(hydrationRaw))
                .redness(createMetricDetail(0)) // Still no data for redness
                .build();

        // 4. Calculate Final Score (higher = better)
        int finalScore = ( (100 - metrics.getAcne().getScore()) +
                           (100 - metrics.getBlackhead().getScore()) +
                           (100 - metrics.getPoreVisibility().getScore()) +
                           (100 - (int)Math.round(skinSpotRaw * 100)) +
                           (100 - metrics.getWrinkle().getScore()) +
                           (100 - (int)Math.round(darkCircleRaw * 100)) +
                           (100 - metrics.getOiliness().getScore()) +
                           (100 - metrics.getHydration().getScore()) ) / 8;

        // 5. Build and return DTO
        return SkinAnalysisDataResponseDto.builder()
                .measurement_id("msr_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .provider("facepp")
                .model_version("facepp-v1-skinanalyze")
                .quality(new SkinAnalysisDataResponseDto.Quality())
                .metrics(metrics)
                .skin_type(determineSkinType(apiSkinType, metrics))
                .by_region(null)
                .cached(false)
                .final_score(finalScore)
                .build();
    }

    private SkinAnalysisDataResponseDto.MetricDetail createMetricDetail(double score) {
        int roundedScore = (int) Math.round(score * 100);
        return SkinAnalysisDataResponseDto.MetricDetail.builder()
                .score(roundedScore)
                .level(calculateLevel(roundedScore))
                .confidence(0.9)
                .build();
    }

    private String calculateLevel(int score) {
        if (score < 34) return "low";
        if (score < 67) return "medium";
        return "high";
    }

    private List<String> determineSkinType(int apiSkinType, SkinAnalysisDataResponseDto.Metrics metrics) {
        String primarySkinType;

        switch (apiSkinType) {
            case 0: primarySkinType = "oily"; break;
            case 1: primarySkinType = "dry"; break;
            case 3: primarySkinType = "combination"; break;
            case 2: default: primarySkinType = "normal"; break;
        }
        List<String> finalSkinTypes = new ArrayList<>();
        finalSkinTypes.add(primarySkinType);
        if (metrics.getAcne().getScore() > 50) {
            finalSkinTypes.add("acne-prone");
        }
        return finalSkinTypes;
    }

    public SkinTypeResponseDto analyzeSkinTypeOnly(MultipartFile image) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("api_key", apiKey);
        body.add("api_secret", apiSecret);

        try {
            body.add("image_file", new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() { return image.getOriginalFilename(); }
            });
        } catch (IOException e) {
            throw new RuntimeException("이미지 파일 처리 중 오류 발생", e);
        }

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<FaceppSkinTypeResponseDto> responseEntity = restTemplate.postForEntity(FACEPP_API_URL, requestEntity, FaceppSkinTypeResponseDto.class);
        FaceppSkinTypeResponseDto faceppResponse = responseEntity.getBody();

        if (faceppResponse == null || faceppResponse.getResult() == null || faceppResponse.getResult().getSkinType() == null) {
            throw new RuntimeException("Face++ API에서 피부 타입 분석 결과를 받지 못했습니다.");
        }

        FaceppSkinTypeResponseDto.SkinTypeObject apiSkinTypeData = faceppResponse.getResult().getSkinType();

        String primarySkinType;
        switch (apiSkinTypeData.getSkinType()) {
            case 0:
                primarySkinType = "oily";
                break;
            case 1:
                primarySkinType = "dry";
                break;
            case 3:
                primarySkinType = "combination";
                break;
            case 2:
            default:
                primarySkinType = "normal";
                break;
        }

        SkinTypeResponseDto.SkinTypeResponseDtoBuilder responseDtoBuilder = SkinTypeResponseDto.builder()
                .skinType(primarySkinType);

        Map<String, SkinTypeResponseDto.Detail> detailsMap = new java.util.LinkedHashMap<>();
        apiSkinTypeData.getDetails().forEach((key, value) -> {
            SkinTypeResponseDto.Detail detail = SkinTypeResponseDto.Detail.builder()
                    .value(value.getValue())
                    .confidence(value.getConfidence())
                    .build();
            detailsMap.put(key, detail);
        });
        responseDtoBuilder.details(detailsMap);

        return responseDtoBuilder.build();
    }
}
