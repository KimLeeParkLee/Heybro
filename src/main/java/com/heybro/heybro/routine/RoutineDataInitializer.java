package com.heybro.heybro.routine;

import com.heybro.heybro.routine.domain.*;
import com.heybro.heybro.routine.repository.RoutineTemplateRepository;
import com.heybro.heybro.user.domain.UserType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class RoutineDataInitializer implements CommandLineRunner {

    private final RoutineTemplateRepository routineTemplateRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (routineTemplateRepository.count() > 0) {
            return;
        }

        for (UserType userType : UserType.values()) {
            RoutineTemplate routineTemplate = RoutineTemplate.builder()
                    .type(userType)
                    .build();

            List<Routine> routines = getRoutinesForUserType(userType);

            for (Routine routine : routines) {
                routine.updateRoutineTemplate(routineTemplate);
                setBidirectionalReferences(routine);
            }

            routineTemplate.updateElementList(routines);
            routineTemplateRepository.save(routineTemplate);
        }
    }

    private List<Routine> getRoutinesForUserType(UserType userType) {
        List<Routine> routines = new ArrayList<>();

        // switch 문을 사용하여 16개 타입별로 루틴을 명확하게 조합
        switch (userType) {
            case OILY_OFFICE:
                // Lv.1
                routines.add(createDrinkWaterRoutine());
                routines.add(createOilySkinTonerRoutine());
                routines.add(createToothpasteRoutine());
                routines.add(createMistRoutine());
                routines.add(createOilySkinCleansingRoutine());
                // Lv.2 ~ Lv.5
                routines.add(createOilySkinLv2Routine());
                routines.add(createOilySkinLv3Routine());
                routines.add(createOilySkinLv4Routine());
                routines.add(createOilySkinLv5Routine());
                routines.add(createOfficeLv2Routine());
                routines.add(createOfficeLv4Routine());
                routines.add(createOfficeLv5Routine());
                break;
            case OILY_OUTDOOR:
                // Lv.1
                routines.add(createOilySkinTonerRoutine());
                routines.add(createSunscreenRoutine());
                routines.add(createLipbalmRoutine());
                routines.add(createOilySkinCleansingRoutine());
                // Lv.2 ~ Lv.5
                routines.add(createOilySkinLv2Routine());
                routines.add(createOilySkinLv3Routine());
                routines.add(createOilySkinLv4Routine());
                routines.add(createOilySkinLv5Routine());
                routines.add(createOutdoorLv2Routine());
                routines.add(createOutdoorLv4Routine());
                routines.add(createOutdoorLv5Routine());
                break;
            case OILY_FASHIONABLE:
                // Lv.1
                routines.add(createOilySkinTonerRoutine());
                routines.add(createShavingRoutine());
                routines.add(createPerfumeRoutine());
                routines.add(createOilySkinCleansingRoutine());
                routines.add(createNailCareRoutine());
                // Lv.2 ~ Lv.5
                routines.add(createOilySkinLv2Routine());
                routines.add(createOilySkinLv3Routine());
                routines.add(createOilySkinLv4Routine());
                routines.add(createOilySkinLv5Routine());
                routines.add(createFashionableLv2Routine());
                routines.add(createFashionableLv4Routine());
                routines.add(createFashionableLv5Routine());
                break;
            case OILY_NORMAL:
                // Lv.1
                routines.add(createDrinkWaterRoutine());
                routines.add(createOilySkinTonerRoutine());
                routines.add(createCheckMirrorRoutine());
                routines.add(createOilySkinCleansingRoutine());
                routines.add(createStretchingRoutine());
                // Lv.2 ~ Lv.5
                routines.add(createOilySkinLv2Routine());
                routines.add(createOilySkinLv3Routine());
                routines.add(createOilySkinLv4Routine());
                routines.add(createOilySkinLv5Routine());
                routines.add(createNormalLv2Routine());
                routines.add(createNormalLv4Routine());
                routines.add(createNormalLv5Routine());
                break;

            case DRY_OFFICE:
                // Lv.1
                routines.add(createDrinkWaterRoutine());
                routines.add(createDrySkinTonerRoutine());
                routines.add(createToothpasteRoutine());
                routines.add(createMistRoutine());
                routines.add(createDrySkinCleansingRoutine());
                routines.add(createDrySkinCreamRoutine());
                // Lv.2 ~ Lv.5
                routines.add(createDrySkinLv2Routine());
                routines.add(createDrySkinLv3Routine());
                routines.add(createDrySkinLv4Routine());
                routines.add(createDrySkinLv5Routine());
                routines.add(createOfficeLv2Routine());
                routines.add(createOfficeLv4Routine());
                routines.add(createOfficeLv5Routine());
                break;
            case DRY_OUTDOOR:
                // Lv.1
                routines.add(createDrySkinTonerRoutine());
                routines.add(createSunscreenRoutine());
                routines.add(createLipbalmRoutine());
                routines.add(createHandcreamRoutine());
                routines.add(createDrySkinCleansingRoutine());
                routines.add(createDrySkinCreamRoutine());
                // Lv.2 ~ Lv.5
                routines.add(createDrySkinLv2Routine());
                routines.add(createDrySkinLv3Routine());
                routines.add(createDrySkinLv4Routine());
                routines.add(createDrySkinLv5Routine());
                routines.add(createOutdoorLv2Routine());
                routines.add(createOutdoorLv4Routine());
                routines.add(createOutdoorLv5Routine());
                break;
            case DRY_FASHIONABLE:
                // Lv.1
                routines.add(createDrySkinTonerRoutine());
                routines.add(createShavingRoutine());
                routines.add(createPerfumeRoutine());
                routines.add(createDrySkinCleansingRoutine());
                routines.add(createDrySkinCreamRoutine());
                routines.add(createNailCareRoutine());
                // Lv.2 ~ Lv.5
                routines.add(createDrySkinLv2Routine());
                routines.add(createDrySkinLv3Routine());
                routines.add(createDrySkinLv4Routine());
                routines.add(createDrySkinLv5Routine());
                routines.add(createFashionableLv2Routine());
                routines.add(createFashionableLv4Routine());
                routines.add(createFashionableLv5Routine());
                break;
            case DRY_NORMAL:
                // Lv.1
                routines.add(createDrinkWaterRoutine());
                routines.add(createDrySkinTonerRoutine());
                routines.add(createCheckMirrorRoutine());
                routines.add(createDrySkinCleansingRoutine());
                routines.add(createDrySkinCreamRoutine());
                routines.add(createStretchingRoutine());
                // Lv.2 ~ Lv.5
                routines.add(createDrySkinLv2Routine());
                routines.add(createDrySkinLv3Routine());
                routines.add(createDrySkinLv4Routine());
                routines.add(createDrySkinLv5Routine());
                routines.add(createNormalLv2Routine());
                routines.add(createNormalLv4Routine());
                routines.add(createNormalLv5Routine());
                break;

            case COMBINATION_OFFICE:
                // Lv.1
                routines.add(createDrinkWaterRoutine());
                routines.add(createCombinationSkinTonerRoutine());
                routines.add(createCombinationSkinCreamRoutine());
                routines.add(createToothpasteRoutine());
                routines.add(createMistRoutine());
                routines.add(createOilySkinCleansingRoutine());
                // Lv.2 ~ Lv.5
                routines.add(createCombinationSkinLv2Routine());
                routines.add(createCombinationSkinLv3Routine());
                routines.add(createCombinationSkinLv4Routine());
                routines.add(createCombinationSkinLv5Routine());
                routines.add(createOfficeLv2Routine());
                routines.add(createOfficeLv4Routine());
                routines.add(createOfficeLv5Routine());
                break;
            case COMBINATION_OUTDOOR:
                // Lv.1
                routines.add(createCombinationSkinTonerRoutine());
                routines.add(createCombinationSkinCreamRoutine());
                routines.add(createSunscreenRoutine());
                routines.add(createLipbalmRoutine());
                routines.add(createOilySkinCleansingRoutine());
                // Lv.2 ~ Lv.5
                routines.add(createCombinationSkinLv2Routine());
                routines.add(createCombinationSkinLv3Routine());
                routines.add(createCombinationSkinLv4Routine());
                routines.add(createCombinationSkinLv5Routine());
                routines.add(createOutdoorLv2Routine());
                routines.add(createOutdoorLv4Routine());
                routines.add(createOutdoorLv5Routine());
                break;
            case COMBINATION_FASHIONABLE:
                // Lv.1
                routines.add(createCombinationSkinTonerRoutine());
                routines.add(createCombinationSkinCreamRoutine());
                routines.add(createShavingRoutine());
                routines.add(createPerfumeRoutine());
                routines.add(createOilySkinCleansingRoutine());
                routines.add(createNailCareRoutine());
                // Lv.2 ~ Lv.5
                routines.add(createCombinationSkinLv2Routine());
                routines.add(createCombinationSkinLv3Routine());
                routines.add(createCombinationSkinLv4Routine());
                routines.add(createCombinationSkinLv5Routine());
                routines.add(createFashionableLv2Routine());
                routines.add(createFashionableLv4Routine());
                routines.add(createFashionableLv5Routine());
                break;
            case COMBINATION_NORMAL:
                // Lv.1
                routines.add(createDrinkWaterRoutine());
                routines.add(createCombinationSkinTonerRoutine());
                routines.add(createCombinationSkinCreamRoutine());
                routines.add(createCheckMirrorRoutine());
                routines.add(createOilySkinCleansingRoutine());
                routines.add(createStretchingRoutine());
                // Lv.2 ~ Lv.5
                routines.add(createCombinationSkinLv2Routine());
                routines.add(createCombinationSkinLv3Routine());
                routines.add(createCombinationSkinLv4Routine());
                routines.add(createCombinationSkinLv5Routine());
                routines.add(createNormalLv2Routine());
                routines.add(createNormalLv4Routine());
                routines.add(createNormalLv5Routine());
                break;

            case SENSITIVE_OFFICE:
                // Lv.1
                routines.add(createDrinkWaterRoutine());
                routines.add(createSensitiveSkinTonerRoutine());
                routines.add(createSensitiveSkinCreamRoutine());
                routines.add(createToothpasteRoutine());
                routines.add(createMistRoutine());
                routines.add(createSensitiveSkinCleansingRoutine());
                // Lv.2 ~ Lv.5
                routines.add(createSensitiveSkinLv2Routine());
                routines.add(createSensitiveSkinLv3Routine());
                routines.add(createSensitiveSkinLv4Routine());
                routines.add(createSensitiveSkinLv5Routine());
                routines.add(createOfficeLv2Routine());
                routines.add(createOfficeLv4Routine());
                routines.add(createOfficeLv5Routine());
                break;
            case SENSITIVE_OUTDOOR:
                // Lv.1
                routines.add(createSensitiveSkinTonerRoutine());
                routines.add(createSensitiveSkinCreamRoutine());
                routines.add(createSunscreenRoutine());
                routines.add(createLipbalmRoutine());
                routines.add(createSensitiveSkinCleansingRoutine());
                // Lv.2 ~ Lv.5
                routines.add(createSensitiveSkinLv2Routine());
                routines.add(createSensitiveSkinLv3Routine());
                routines.add(createSensitiveSkinLv4Routine());
                routines.add(createSensitiveSkinLv5Routine());
                routines.add(createOutdoorLv2Routine());
                routines.add(createOutdoorLv4Routine());
                routines.add(createOutdoorLv5Routine());
                break;
            case SENSITIVE_FASHIONABLE:
                // Lv.1
                routines.add(createSensitiveSkinTonerRoutine());
                routines.add(createSensitiveSkinCreamRoutine());
                routines.add(createShavingRoutine());
                routines.add(createPerfumeRoutine());
                routines.add(createSensitiveSkinCleansingRoutine());
                routines.add(createNailCareRoutine());
                // Lv.2 ~ Lv.5
                routines.add(createSensitiveSkinLv2Routine());
                routines.add(createSensitiveSkinLv3Routine());
                routines.add(createSensitiveSkinLv4Routine());
                routines.add(createSensitiveSkinLv5Routine());
                routines.add(createFashionableLv2Routine());
                routines.add(createFashionableLv4Routine());
                routines.add(createFashionableLv5Routine());
                break;
            case SENSITIVE_NORMAL:
                // Lv.1
                routines.add(createDrinkWaterRoutine());
                routines.add(createSensitiveSkinTonerRoutine());
                routines.add(createSensitiveSkinCreamRoutine());
                routines.add(createCheckMirrorRoutine());
                routines.add(createSensitiveSkinCleansingRoutine());
                routines.add(createStretchingRoutine());
                // Lv.2 ~ Lv.5
                routines.add(createSensitiveSkinLv2Routine());
                routines.add(createSensitiveSkinLv3Routine());
                routines.add(createSensitiveSkinLv4Routine());
                routines.add(createSensitiveSkinLv5Routine());
                routines.add(createNormalLv2Routine());
                routines.add(createNormalLv4Routine());
                routines.add(createNormalLv5Routine());
                break;
        }
        return routines;
    }

    // --- LV.1 기존 루틴 (상세 이미지 포함) ---

    private Routine createDrinkWaterRoutine() {
        return Routine.builder().level(1).name("일어나서 물 한 잔 마시기").timeOfDay(TimeOfDay.MORNING).iconImage("https://drive.google.com/file/d/1Vp03pqKmtYfFeLqkncbf_dTpP73brpgT/view?usp=drive_link").tipList(createTips("천천히 마시면서 위장이 놀라지 않게 하세요.", "찬물보다는 미지근한 물이 소화에 더 좋아요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("물 한 잔 마시기").content("기상 후 상온의 물 한 잔을 마셔 몸을 깨워주세요.").detailImage("https://drive.google.com/uc?export=view&id=12xxgmmAq96igwIujCzjGl-n8wdQKHPk-").build())).build();
    }
    private Routine createToothpasteRoutine() {
        return Routine.builder().level(1).name("점심 식사 후 양치하기").timeOfDay(TimeOfDay.LUNCH).iconImage("https://drive.google.com/file/d/1dfr1pTE-eEpXPKyi3FEUeWl040cSmBBY/view?usp=sharing").tipList(createTips("칫솔질 전 가볍게 물로 헹구면 더 효과적입니다.", "치실이나 가글을 함께 사용하면 좋습니다.")).elementList(Arrays.asList(RoutineElement.builder().step(1).name("식후 30분 뒤 양치").content("식사 후 30분 뒤 양치하세요.").build(), RoutineElement.builder().step(2).name("혀 닦아 마무리하기").content("혀까지 부드럽게 닦아 마무리합니다.").build())).build();
    }
    private Routine createStretchingRoutine() {
        return Routine.builder().level(1).name("취침 전 스트레칭 하기").timeOfDay(TimeOfDay.EVENING).iconImage("https://drive.google.com/file/d/1j5NQU-lNrf7nW2dCRaA6Ul03ni-Afk6p/view?usp=sharing").tipList(createTips("스트레칭은 10분 이내가 적당합니다.", "무리하지 말고 호흡에 맞춰 하세요.")).elementList(Arrays.asList(RoutineElement.builder().step(1).name("목과 어깨 풀기").content("가볍게 목과 어깨를 돌려 풀어줍니다.").build(), RoutineElement.builder().step(2).name("다리 근육 늘리기").content("허리를 숙여 다리 근육을 늘려줍니다.").build(), RoutineElement.builder().step(3).name("전신 스트레칭").content("누운 상태에서 전신 스트레칭을 합니다.").build())).build();
    }
    private Routine createOilySkinTonerRoutine() {
        return Routine.builder().level(1).name("피지 조절 토너로 정돈하기").timeOfDay(TimeOfDay.MORNING).iconImage("https://drive.google.com/file/d/1klmt34jWh1hDssErZRXPeLU1oa6OhcRK/view?usp=sharing").tipList(createTips("BHA, 티트리 성분은 피지 조절에 도움을 줍니다.", "화장솜으로 T존을 중심으로 닦아내세요.")).elementList(Arrays.asList(RoutineElement.builder().step(1).name("토너 덜어내기").content("화장솜에 BHA/티트리 토너를 충분히 적셔주세요.").build(), RoutineElement.builder().step(2).name("T존 중심으로 닦아내기").content("피지 분비가 많은 이마, 코 주변을 중심으로 닦아냅니다.").build())).build();
    }
    private Routine createCombinationSkinTonerRoutine() {
        return Routine.builder().level(1).name("T존/U존 맞춤 토너 바르기").timeOfDay(TimeOfDay.MORNING).iconImage("https://drive.google.com/file/d/1klmt34jWh1hDssErZRXPeLU1oa6OhcRK/view?usp=sharing").tipList(createTips("부위별로 다른 제품을 사용하는 것이 가장 효과적입니다.", "유수분 밸런스를 맞춰주는 제품을 선택하세요.")).elementList(Arrays.asList(RoutineElement.builder().step(1).name("T존 정돈하기").content("산뜻한 토너로 T존(이마, 코)의 유분기를 닦아냅니다.").build(), RoutineElement.builder().step(2).name("U존 보습하기").content("보습 토너를 U존(볼, 턱)에 발라 수분을 공급합니다.").build())).build();
    }
    private Routine createCombinationSkinCreamRoutine() {
        return Routine.builder().level(1).name("부위별 로션/크림 바르기").timeOfDay(TimeOfDay.MORNING).iconImage("https://drive.google.com/file/d/17G9fijMamsQy8XZVG2Rqp8XAvJMuo8DY/view?usp=sharing").tipList(createTips("하나의 제품을 바를 땐 건조한 볼부터 바르세요.", "T존에는 가볍게, U존에는 한번 더 덧바르는 것을 추천해요.")).elementList(Arrays.asList(RoutineElement.builder().step(1).name("T존에 로션 바르기").content("피지 분비가 많은 T존에는 가벼운 젤 타입 로션을 바릅니다.").build(), RoutineElement.builder().step(2).name("U존에 크림 바르기").content("건조한 U존에는 좀 더 보습감 있는 크림을 바릅니다.").build())).build();
    }
    private Routine createSensitiveSkinTonerRoutine() {
        return Routine.builder().level(1).name("진정 토너로 피부 달래기").timeOfDay(TimeOfDay.MORNING).iconImage("https://drive.google.com/file/d/1klmt34jWh1hDssErZRXPeLU1oa6OhcRK/view?usp=sharing").tipList(createTips("시카, 알란토인, 판테놀 성분은 피부 진정에 도움을 줍니다.", "화장솜 마찰은 피하고 손으로 흡수시켜주세요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("손으로 두드려 흡수").content("시카 성분의 진정 토너를 손으로 가볍게 두드려 흡수시킵니다.").build())).build();
    }
    private Routine createSensitiveSkinCreamRoutine() {
        return Routine.builder().level(1).name("피부 장벽 크림으로 보호하기").timeOfDay(TimeOfDay.EVENING).iconImage("https://drive.google.com/file/d/1mKJhBfjPlFgz89Ck3hNEQ5Cc0_0fT2BW/view?usp=sharing").tipList(createTips("무향, 무색소의 저자극 제품을 선택하세요.", "새로운 제품은 반드시 패치 테스트를 진행하세요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("장벽 강화 크림 바르기").content("피부 장벽 강화에 도움을 주는 크림으로 마무리합니다.").build())).build();
    }
    private Routine createSensitiveSkinCleansingRoutine() {
        return Routine.builder().level(1).name("약산성 클렌저로 세안하기").timeOfDay(TimeOfDay.EVENING).iconImage("https://drive.google.com/file/d/1J6vOsP4wPlHgnD5guVENqYFCp1_IKM5V/view?usp=sharing").tipList(createTips("뜨거운 물은 피부에 자극을 줄 수 있으니 미온수를 사용하세요.", "세안 후 수건으로 문지르지 말고 톡톡 두드려 물기를 제거하세요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("저자극 젤 클렌저로 세안").content("약산성 젤 클렌저를 사용하여 부드럽게 세안합니다.").build())).build();
    }
    private Routine createOilySkinCleansingRoutine() {
        return Routine.builder().level(1).name("모공 딥클렌징하기").timeOfDay(TimeOfDay.EVENING).iconImage("https://drive.google.com/file/d/1J6vOsP4wPlHgnD5guVENqYFCp1_IKM5V/view?usp=sharing").tipList(createTips("주 1~2회 클레이 마스크를 사용하면 효과적입니다.", "피지 분비가 많은 코 주변을 꼼꼼히 롤링해주세요.")).elementList(Arrays.asList(RoutineElement.builder().step(1).name("클렌징 오일로 1차 세안").content("클렌징 오일로 피지와 노폐물을 녹여냅니다.").build(), RoutineElement.builder().step(2).name("폼 클렌저로 2차 세안").content("약산성 폼 클렌저로 개운하게 마무리합니다.").build())).build();
    }
    private Routine createDrySkinTonerRoutine() {
        return Routine.builder().level(1).name("보습 토너로 수분 채우기").timeOfDay(TimeOfDay.MORNING).iconImage("https://drive.google.com/file/d/1klmt34jWh1hDssErZRXPeLU1oa6OhcRK/view?usp=sharing").tipList(createTips("히알루론산, 판테놀 성분은 보습에 좋습니다.", "손에 덜어 여러 번 레이어링하면 더 촉촉해요.")).elementList(Arrays.asList(RoutineElement.builder().step(1).name("토너 덜어내기").content("콧물 제형의 보습 토너를 손바닥에 덜어주세요.").build(), RoutineElement.builder().step(2).name("두드려 흡수시키기").content("얼굴 전체를 부드럽게 두드려 수분감을 채워줍니다.").build())).build();
    }
    private Routine createDrySkinCreamRoutine() {
        return Routine.builder().level(1).name("보습 장벽 크림 바르기").timeOfDay(TimeOfDay.EVENING).iconImage("https://drive.google.com/file/d/1mKJhBfjPlFgz89Ck3hNEQ5Cc0_0fT2BW/view?usp=sharing").tipList(createTips("세라마이드, 시어버터 성분은 보습 장벽 강화에 도움을 줍니다.", "저녁에는 수면팩처럼 도톰하게 바르는 것을 추천해요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("크림으로 보습막 씌우기").content("세라마이드 성분의 크림을 발라 수분 보호막을 형성합니다.").build())).build();
    }
    private Routine createDrySkinCleansingRoutine() {
        return Routine.builder().level(1).name("저자극 클렌징하기").timeOfDay(TimeOfDay.EVENING).iconImage("https://drive.google.com/file/d/1J6vOsP4wPlHgnD5guVENqYFCp1_IKM5V/view?usp=sharing").tipList(createTips("강한 세정력의 제품은 피부를 더 건조하게 만들 수 있어요.", "클렌징 밀크나 로션 타입을 추천합니다.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("부드럽게 롤링하여 세안").content("클렌징 밀크나 크림으로 부드럽게 세안하여 자극을 최소화합니다.").build())).build();
    }
    private Routine createMistRoutine() {
        return Routine.builder().level(1).name("미스트 뿌리기").timeOfDay(TimeOfDay.LUNCH).iconImage("https://drive.google.com/file/d/15YJ5DluvK9upjKenT4Hwlbw8T3caLnb-/view?usp=sharing").tipList(createTips("건조한 사무실 환경에서는 수시로 수분을 공급해주세요.", "뿌린 뒤 가볍게 두드려 흡수시키는 것이 중요합니다.")).elementList(Arrays.asList(RoutineElement.builder().step(1).name("거리 두고 분사하기").content("얼굴에서 20cm 정도 거리를 두고 미스트를 분사하세요.").build(), RoutineElement.builder().step(2).name("두드려 흡수시키기").content("가볍게 손으로 두드려 흡수시킵니다.").build())).build();
    }
    private Routine createCheckMirrorRoutine() {
        return Routine.builder().level(1).name("거울 보고 상태 확인하기").timeOfDay(TimeOfDay.LUNCH).iconImage("https://drive.google.com/file/d/1pHcsbjGfSPVAc5Ozd_on0gEZy8_4si9e/view?usp=sharing").tipList(createTips("중요한 회의 전 습관화하면 깔끔한 이미지를 유지할 수 있어요.", "표정까지 확인하면 자신감이 더 생겨요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("얼굴과 옷매무새 체크").content("얼굴, 머리카락, 옷매무새를 빠르게 체크하세요.").build())).build();
    }
    private Routine createPerfumeRoutine() {
        return Routine.builder().level(1).name("외출 전 향수 뿌리기").timeOfDay(TimeOfDay.MORNING).iconImage("https://drive.google.com/file/d/1nohQ6AnD_HFh9PqjnFjoBBL6rAndQZxk/view?usp=sharing").tipList(createTips("은은한 향은 좋은 인상을 줍니다.", "계절과 스타일에 맞는 향을 선택하세요.")).elementList(Arrays.asList(RoutineElement.builder().step(1).name("몸과 옷에 분사하기").content("손목, 귀 뒤 등 맥박이 뛰는 곳에 가볍게 분사합니다.").build(), RoutineElement.builder().step(2).name("공중에 뿌려 흡수시키기").content("공중에 뿌린 뒤 걸어가며 은은하게 연출합니다.").build())).build();
    }
    private Routine createNailCareRoutine() {
        return Routine.builder().level(1).name("손톱 관리하기").timeOfDay(TimeOfDay.EVENING).iconImage("https://drive.google.com/file/d/18mX5LE-Eox8Jg9qamyIIF5OwOZDH2QbD/view?usp=sharing").tipList(createTips("깔끔한 손은 좋은 인상을 줍니다.", "취침 전 관리하면 흡수가 잘돼요.")).elementList(Arrays.asList(RoutineElement.builder().step(1).name("길이 정돈하기").content("손톱깎이나 파일로 길이를 정돈합니다.").build(), RoutineElement.builder().step(2).name("큐티클 보습하기").content("큐티클 오일이나 로션으로 보습합니다.").build())).build();
    }
    private Routine createSunscreenRoutine() {
        return Routine.builder().level(1).name("자외선 차단제 바르기").timeOfDay(TimeOfDay.MORNING).iconImage("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.flaticon.com%2Fkr%2Ffree-icon%2Fsunscreen_1003460&psig=AOvVaw2e_i3X4x...").tipList(createTips("자외선은 피부 노화의 주범입니다.", "야외 활동 30분 전에 바르고 2~3시간마다 덧발라주세요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("꼼꼼하게 펴 바르기").content("얼굴, 목, 손등 등 노출되는 모든 부위에 자외선 차단제를 바릅니다.").build())).build();
    }
    private Routine createLipbalmRoutine() {
        return Routine.builder().level(1).name("립밤 바르기").timeOfDay(TimeOfDay.LUNCH).iconImage("https://drive.google.com/file/d/1zup8mc8jcGqDsFs0wXm3wuIsAGh-Y9rs/view?usp=sharing").tipList(createTips("자외선 차단 기능이 있는 제품을 추천합니다.", "무향·무색 제품을 쓰면 덧바르기 편해요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("입술에 골고루 바르기").content("입술 중앙부터 바깥쪽으로 골고루 바르세요.").build())).build();
    }
    private Routine createHandcreamRoutine() {
        return Routine.builder().level(1).name("핸드크림 바르기").timeOfDay(TimeOfDay.LUNCH).iconImage("https://drive.google.com/file/d/1zabKYnzrq7_SM4o-ZdiOSORnqWDQE6T5/view?usp=sharing").tipList(createTips("손은 피부가 얇아 노화가 빠릅니다.", "끈적임이 적은 제형은 낮에 쓰기 좋아요.")).elementList(Arrays.asList(RoutineElement.builder().step(1).name("손등에 덜어 문지르기").content("적당량을 손등에 짜서 양손으로 문질러 주세요.").build(), RoutineElement.builder().step(2).name("손가락과 큐티클에 바르기").content("손가락 사이와 손톱 큐티클까지 꼼꼼히 발라주세요.").build())).build();
    }
    private Routine createShavingRoutine() {
        return Routine.builder().level(1).name("면도하기").timeOfDay(TimeOfDay.MORNING).iconImage("https://drive.google.com/file/d/1aQ_O3PPuHEhiY0k6iWFJ3TvsQSb30O2C/view?usp=sharing").tipList(createTips("쉐이빙 폼을 사용해 피부 자극을 줄여주세요.", "면도 후에는 애프터쉐이브 제품으로 피부를 진정시키세요.")).elementList(Arrays.asList(RoutineElement.builder().step(1).name("쉐이빙 폼 바르기").content("쉐이빙 폼을 거품을 잘내서 얼굴에 발라주세요.").build(), RoutineElement.builder().step(2).name("결대로 면도 후 역방향 마무리").content("수염 결대로 면도하고, 역방향으로 한번 더 마무리 합니다.").build())).build();
    }

    // --- LV.2 ~ LV.5 신규 루틴 (이미지 없음) ---

    // 지성 피부
    private Routine createOilySkinLv2Routine() { return Routine.builder().level(2).name("아침 오일프리 보습제").timeOfDay(TimeOfDay.MORNING).tipList(createTips("유분이 많아도 속은 건조할 수 있습니다.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("가볍게 보습하기").content("젤 타입의 오일프리 수분크림을 발라 유분 걱정 없이 속건조를 잡아줍니다.").build())).build(); }
    private Routine createOilySkinLv3Routine() { return Routine.builder().level(3).name("주 1회 클레이 마스크").timeOfDay(TimeOfDay.EVENING).tipList(createTips("모공 속 노폐물과 피지를 흡착해줘요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("T존 중심으로 팩하기").content("피지 분비가 많은 T존과 나비존 위주로 클레이 마스크를 10분간 사용합니다.").build())).build(); }
    private Routine createOilySkinLv4Routine() { return Routine.builder().level(4).name("BHA 토너로 각질 관리").timeOfDay(TimeOfDay.EVENING).tipList(createTips("BHA(살리실산)는 지용성 각질 제거 성분입니다.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("닦아내는 토너 사용").content("주 1~2회 저녁, BHA 성분 토너를 화장솜에 묻혀 피부결을 따라 닦아냅니다.").build())).build(); }
    private Routine createOilySkinLv5Routine() { return Routine.builder().level(5).name("모공/피지 조절 세럼 사용").timeOfDay(TimeOfDay.MORNING).tipList(createTips("나이아신아마이드 성분은 피지 조절과 모공 개선에 효과적입니다.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("기능성 세럼 바르기").content("토너 다음 단계에서 나이아신아마이드 세럼을 발라 과잉 피지를 컨트롤합니다.").build())).build(); }

    // 건성 피부
    private Routine createDrySkinLv2Routine() { return Routine.builder().level(2).name("아침 물세안 & 보습 토너").timeOfDay(TimeOfDay.MORNING).tipList(createTips("밤새 쌓인 먼지만 가볍게 씻어내 유분막을 보호하세요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("가볍게 세안 후 보습").content("미온수로만 세안 후, 히알루론산 성분의 보습 토너를 발라 수분을 채웁니다.").build())).build(); }
    private Routine createDrySkinLv3Routine() { return Routine.builder().level(3).name("주 1회 보습 시트 마스크").timeOfDay(TimeOfDay.EVENING).tipList(createTips("15분 이내로 사용하는 것이 좋습니다.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("마스크로 집중 보습").content("보습 앰플이 듬뿍 담긴 시트 마스크를 활용해 피부에 깊은 수분감을 전달합니다.").build())).build(); }
    private Routine createDrySkinLv4Routine() { return Routine.builder().level(4).name("페이스 오일 활용하기").timeOfDay(TimeOfDay.EVENING).tipList(createTips("크림에 1~2방울 섞어 사용하면 보습력이 극대화됩니다.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("오일로 보습막 코팅").content("스킨케어 마지막 단계에서 페이스 오일을 손바닥에 비벼 얼굴 전체를 감싸듯 발라줍니다.").build())).build(); }
    private Routine createDrySkinLv5Routine() { return Routine.builder().level(5).name("탄력 아이크림 사용").timeOfDay(TimeOfDay.EVENING).tipList(createTips("눈가 피부는 얇아서 주름이 생기기 쉬워요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("네 번째 손가락으로 바르기").content("약지 손가락에 소량 덜어 눈가에 가볍게 두드리며 흡수시킵니다.").build())).build(); }

    // 복합성 피부
    private Routine createCombinationSkinLv2Routine() { return Routine.builder().level(2).name("T존/U존 다르게 보습").timeOfDay(TimeOfDay.MORNING).tipList(createTips("부위별 피부 상태에 맞는 보습이 중요합니다.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("부위별 맞춤 보습").content("T존에는 가벼운 젤 로션을, 건조한 U존에는 보습 크림을 발라 유수분 밸런스를 맞춥니다.").build())).build(); }
    private Routine createCombinationSkinLv3Routine() { return Routine.builder().level(3).name("주 1회 T존 부분 팩").timeOfDay(TimeOfDay.EVENING).tipList(createTips("U존은 건조해질 수 있으니 T존에만 사용하세요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("머드팩/클레이 마스크 활용").content("피지 흡착 기능이 있는 팩을 T존과 나비존에만 사용하여 모공을 관리합니다.").build())).build(); }
    private Routine createCombinationSkinLv4Routine() { return Routine.builder().level(4).name("밸런싱 에센스 사용").timeOfDay(TimeOfDay.MORNING).tipList(createTips("피부 전체의 유수분 균형을 맞춰줘요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("에센스로 균형 맞추기").content("토너 다음 단계에서 유수분 밸런스를 조절해주는 에센스를 얼굴 전체에 발라줍니다.").build())).build(); }
    private Routine createCombinationSkinLv5Routine() { return Routine.builder().level(5).name("부위별 기능성 앰플").timeOfDay(TimeOfDay.EVENING).tipList(createTips("T존 모공엔 나이아신아마이드, U존 칙칙함엔 비타민C를 추천해요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("고민 부위에 앰플 사용").content("피부 고민에 맞는 기능성 앰플을 T존과 U존에 각각 다르게 사용하여 집중 케어합니다.").build())).build(); }

    // 민감성 피부
    private Routine createSensitiveSkinLv2Routine() { return Routine.builder().level(2).name("약산성 클렌저 사용").timeOfDay(TimeOfDay.MORNING).tipList(createTips("알칼리성 클렌저는 피부 장벽을 손상시킬 수 있어요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("순한 클렌저로 세안").content("건강한 피부의 pH와 유사한 약산성 클렌저로 부드럽게 세안합니다.").build())).build(); }
    private Routine createSensitiveSkinLv3Routine() { return Routine.builder().level(3).name("주 2회 진정 토너 팩").timeOfDay(TimeOfDay.EVENING).tipList(createTips("화장솜이 자극적이라면 거즈를 사용해보세요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("토너 팩으로 집중 진정").content("진정 토너를 화장솜에 듬뿍 적셔 붉어진 부위에 5분간 올려둡니다.").build())).build(); }
    private Routine createSensitiveSkinLv4Routine() { return Routine.builder().level(4).name("매일 자외선 차단제").timeOfDay(TimeOfDay.MORNING).tipList(createTips("화학적 차단제보다 자극이 적은 물리적 차단제(무기자차)를 추천해요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("순한 선크림 바르기").content("외출 전, 자극이 적은 무기자차 선크림을 발라 피부를 보호합니다.").build())).build(); }
    private Routine createSensitiveSkinLv5Routine() { return Routine.builder().level(5).name("피부 장벽 강화 세럼").timeOfDay(TimeOfDay.EVENING).tipList(createTips("세라마이드, 판테놀 성분은 피부 장벽 강화에 도움을 줍니다.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("장벽 세럼으로 기초 다지기").content("토너 다음 단계에서 피부 장벽 강화 세럼을 사용하여 피부 기초 체력을 기릅니다.").build())).build(); }

    // 오피스형
    private Routine createOfficeLv2Routine() { return Routine.builder().level(2).name("틈틈이 목 스트레칭").timeOfDay(TimeOfDay.LUNCH).tipList(createTips("거북목과 어깨 결림을 예방할 수 있어요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("목 돌리기").content("1시간에 한 번씩, 의자에 앉은 채로 목을 천천히 돌려 긴장을 풀어줍니다.").build())).build(); }
    private Routine createOfficeLv4Routine() { return Routine.builder().level(4).name("저녁 온열 안대 사용").timeOfDay(TimeOfDay.EVENING).tipList(createTips("눈의 피로를 풀면 숙면에도 도움이 됩니다.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("눈 휴식").content("잠들기 전, 온열 안대를 15분간 착용하여 지친 눈의 피로를 풀어줍니다.").build())).build(); }
    private Routine createOfficeLv5Routine() { return Routine.builder().level(5).name("블루라이트 차단").timeOfDay(TimeOfDay.MORNING).tipList(createTips("블루라이트는 피부 노화와 안구 피로의 원인이 될 수 있습니다.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("차단 제품 사용").content("블루라이트 차단 기능이 있는 안경, 모니터 필름, 또는 선크림을 사용합니다.").build())).build(); }

    // 아웃도어형
    private Routine createOutdoorLv2Routine() { return Routine.builder().level(2).name("활동 후 피부 진정").timeOfDay(TimeOfDay.EVENING).tipList(createTips("햇볕에 달아오른 피부는 즉시 진정시켜야 해요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("알로에 젤 바르기").content("야외 활동 후, 샤워하고 알로에 젤을 발라 자극받은 피부를 진정시킵니다.").build())).build(); }
    private Routine createOutdoorLv4Routine() { return Routine.builder().level(4).name("바디 피부 진정/보습").timeOfDay(TimeOfDay.EVENING).tipList(createTips("몸 피부도 얼굴만큼 소중해요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("바디로션 바르기").content("샤워 후, 수딩 기능이 있는 바디로션을 몸 전체에 발라줍니다.").build())).build(); }
    private Routine createOutdoorLv5Routine() { return Routine.builder().level(5).name("족욕/풋 케어").timeOfDay(TimeOfDay.EVENING).tipList(createTips("발의 피로를 풀면 전신이 개운해져요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("발 피로 풀기").content("따뜻한 물에 10분간 족욕을 하거나, 풋 크림을 발라 지친 발의 피로를 풀어줍니다.").build())).build(); }

    // 패셔너블형
    private Routine createFashionableLv2Routine() { return Routine.builder().level(2).name("헤어 스타일링 하기").timeOfDay(TimeOfDay.MORNING).tipList(createTips("헤어스타일은 첫인상의 70%를 결정해요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("머리 손질").content("헤어 에센스를 바른 후, 왁스나 스프레이를 사용하여 원하는 스타일을 연출합니다.").build())).build(); }
    private Routine createFashionableLv4Routine() { return Routine.builder().level(4).name("두피 케어/트리트먼트").timeOfDay(TimeOfDay.EVENING).tipList(createTips("건강한 두피에서 건강한 머릿결이 나와요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("헤어 집중 관리").content("주 1~2회 두피 스케일링 제품으로 딥클렌징하고, 헤어 트리트먼트로 영양을 공급합니다.").build())).build(); }
    private Routine createFashionableLv5Routine() { return Routine.builder().level(5).name("치아 미백/베이스 활용").timeOfDay(TimeOfDay.MORNING).tipList(createTips("밝은 미소와 깨끗한 피부 표현은 스타일의 완성입니다.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("완벽한 마무리").content("미백 치약으로 꾸준히 관리하고, 외출 전 프라이머나 톤업크림으로 피부결을 정돈합니다.").build())).build(); }

    // 노멀형
    private Routine createNormalLv2Routine() { return Routine.builder().level(2).name("자기 전 립밤 바르기").timeOfDay(TimeOfDay.EVENING).tipList(createTips("자는 동안 입술 각질을 잠재워줘요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("입술 보습").content("잠들기 전, 보습 립밤을 도톰하게 발라 다음날 아침 촉촉한 입술을 만듭니다.").build())).build(); }
    private Routine createNormalLv4Routine() { return Routine.builder().level(4).name("주 1회 각질 제거").timeOfDay(TimeOfDay.EVENING).tipList(createTips("자극이 적은 고마쥬나 효소 타입을 추천해요.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("피부결 정돈").content("주 1회, 자극이 적은 필링 젤로 묵은 각질을 제거하여 맑은 피부톤을 유지합니다.").build())).build(); }
    private Routine createNormalLv5Routine() { return Routine.builder().level(5).name("비타민C 항산화 관리").timeOfDay(TimeOfDay.MORNING).tipList(createTips("피부 노화를 예방하는 가장 쉬운 방법입니다.")).elementList(Collections.singletonList(RoutineElement.builder().step(1).name("미래를 위한 투자").content("아침 스킨케어에 비타민C 세럼을 추가하여 피부 방어력을 높이고 노화를 예방합니다.").build())).build(); }


    // --- 유틸리티 메소드 ---
    private List<RoutineTip> createTips(String... contents) {
        if (contents == null || contents.length == 0) {
            return new ArrayList<>();
        }
        return Stream.of(contents)
                .map(content -> RoutineTip.builder().content(content).build())
                .collect(Collectors.toList());
    }

    private void setBidirectionalReferences(Routine routine) {
        if (routine.getElementList() != null) {
            for (RoutineElement element : routine.getElementList()) {
                element.updateRoutine(routine);
            }
        }
        if (routine.getTipList() != null) {
            for (RoutineTip tip : routine.getTipList()) {
                tip.updateRoutine(routine);
            }
        }
        if (routine.getRecommendedProductList() != null) {
            for (RecommendedProduct product : routine.getRecommendedProductList()) {
                product.updateRoutine(routine);
            }
        }
    }
}