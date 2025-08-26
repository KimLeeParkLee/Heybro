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

    // RoutineTemplate을 저장해야 하므로 RoutineTemplateRepository를 주입받습니다.
    private final RoutineTemplateRepository routineTemplateRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // routineRepository 대신 routineTemplateRepository로 확인합니다.
        if (routineTemplateRepository.count() > 0) {
            return;
        }

        for (UserType userType : UserType.values()) {
            RoutineTemplate routineTemplate = RoutineTemplate.builder()
                    .type(userType)
                    .build();

            List<Routine> routines = createInitialRoutines(); // 메서드 이름 명확하게 변경
            for (Routine routine : routines) {
                routine.updateRoutine(routineTemplate);
                setBidirectionalReferences(routine); // 양방향 참조 설정
            }

            routineTemplate.setElementList(routines);
            routineTemplateRepository.save(routineTemplate); // RoutineTemplate을 저장
        }
    }

    private List<Routine> createInitialRoutines() {
        List<Routine> routines = new ArrayList<>();

        // MORNING 루틴
        routines.add(Routine.builder()
                .name("일어나서 물 한 잔 마시기")
                .timeOfDay(TimeOfDay.MORNING)
                .iconImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/routine/icon/MORNING/%E1%84%86%E1%85%AE%E1%86%AF+%E1%84%86%E1%85%A1%E1%84%89%E1%85%B5%E1%84%80%E1%85%B5.png")
                .tipList(createTips("천천히 마시면서 위장이 놀라지 않게 하세요.", "찬물보다는 미지근한 물이 소화에 더 좋아요."))
                .elementList(Collections.singletonList(
                        RoutineElement.builder()
                                .step(1)
                                .content("기상 후 상온의 물 한 잔을 마셔 몸을 깨워주세요.")
                                .detailImage("https://drive.google.com/uc?export=view&id=12xxgmmAq96igwIujCzjGl-n8wdQKHPk-")
                                .build()
                ))
                .build());

        routines.add(Routine.builder()
                .name("건강 보조제 챙겨 먹기")
                .timeOfDay(TimeOfDay.MORNING)
                .iconImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/routine/icon/MORNING/%E1%84%80%E1%85%A5%E1%86%AB%E1%84%80%E1%85%A1%E1%86%BC%E1%84%87%E1%85%A9%E1%84%8C%E1%85%A9%E1%84%8C%E1%85%A6.png")
                .tipList(createTips("매일 같은 시간대에 섭취하면 습관화에 좋아요.", "커피나 차와 함께 먹지 않도록 주의하세요."))
                .elementList(Collections.singletonList(
                        RoutineElement.builder()
                                .step(1)
                                .content("물과 함께 건강 보조제를 섭취하세요.")
                                .detailImage("https://drive.google.com/uc?export=view&id=12WVVEjTQtskpSCv75aIfSmQCsXvFXLd4")
                                .build()
                ))
                .build());

        routines.add(Routine.builder()
                .name("토너로 피부결 정돈하기")
                .timeOfDay(TimeOfDay.MORNING)
                .iconImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/routine/icon/MORNING/%E1%84%90%E1%85%A9%E1%84%82%E1%85%A5.png")
                .tipList(createTips("세안 직후 바로 발라야 수분 손실을 막을 수 있어요.", "화장솜 대신 손바닥을 쓰면 피부 자극이 줄어듭니다."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).content("세안 후 화장솜이나 손바닥에 토너를 적당히 덜어주세요.").detailImage("https://drive.google.com/uc?export=view&id=1Mn27Us_0_7gaZcywBGfn8TYLj-wPVHUG").build(),
                        RoutineElement.builder().step(2).content("피부결을 따라 부드럽게 닦아내거나 두드려 흡수시킵니다.").detailImage("https://drive.google.com/uc?export=view&id=1xmvvHTnfqgp1_dxwyuMf9sYuGIfttCnJ").build(),
                        RoutineElement.builder().step(3).content("남은 토너는 목이나 귀밑까지 발라주세요.").detailImage("https://drive.google.com/uc?export=view&id=1Qj-AAXxzjDUHXcxGEaepBtwvmbhBlJZz").build()
                ))
                .build());

        routines.add(Routine.builder()
                .name("에센스/세럼 바르기")
                .timeOfDay(TimeOfDay.MORNING)
                .iconImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/routine/icon/MORNING/%E1%84%8B%E1%85%A6%E1%84%89%E1%85%A6%E1%86%AB%E1%84%89%E1%85%B3%E1%84%89%E1%85%A6%E1%84%85%E1%85%A5%E1%86%B7.png")
                .tipList(createTips("세럼은 피부 고민에 맞는 제품을 선택하세요.", "여러 제품을 쓸 땐 점도가 묽은 것부터 바르세요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).content("적당량의 세럼을 손에 펌핑합니다.").build(),
                        RoutineElement.builder().step(2).content("피부 중심에서 바깥쪽으로 부드럽게 발라주세요.").build(),
                        RoutineElement.builder().step(3).content("손바닥으로 얼굴을 감싸며 흡수시킵니다.").build()
                ))
                .build());

        routines.add(Routine.builder()
                .name("로션/크림 바르기")
                .timeOfDay(TimeOfDay.MORNING)
                .iconImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/routine/icon/MORNING/%E1%84%85%E1%85%A9%E1%84%89%E1%85%A7%E1%86%AB%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%B7.png")
                .tipList(createTips("목까지 함께 발라야 관리가 완성돼요.", "아침에는 가벼운 로션, 저녁엔 영양 크림이 적합합니다."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).content("손등만한 크기의 양을 손바닥에 덜어주세요.").build(),
                        RoutineElement.builder().step(2).content("볼 → 이마 → 턱 순으로 바르며 고르게 펴 발라줍니다.").build(),
                        RoutineElement.builder().step(3).content("손바닥으로 얼굴을 지그시 눌러 흡수시킵니다.").build()
                ))
                .build());

        routines.add(Routine.builder()
                .name("면도하기")
                .timeOfDay(TimeOfDay.MORNING)
                .iconImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/routine/icon/MORNING/%E1%84%86%E1%85%A7%E1%86%AB%E1%84%83%E1%85%A9.png")
                .tipList(createTips("쉐이빙 폼을 잘 발라야 칼날이 피부에 밀착돼서 상처가 나지않아요.", "잘 밀리지 않는 부분은 피부를 반대 방향으로 당겨서 한번 더 밀어주세요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).content("쉐이빙 폼을 거품을 잘내서 얼굴에 발라주세요.").detailImage("https://drive.google.com/uc?export=view&id=1-wwxnGNwTDlbx-_NI59XcwhW6phCHMvA").build(),
                        RoutineElement.builder().step(2).content("처음엔 수염의 결대로 면도를 하고, 깔끔하게 역방향으로 한번 더 마무리 합니다.").detailImage("https://drive.google.com/uc?export=view&id=1j_JnkzKL5N8KxjAij2zdMW-kjaoVUvi6").build(),
                        RoutineElement.builder().step(3).content("미온수로 쉐이빙 폼이 남지 않게 꼼꼼히 행궈주세요.").detailImage("https://drive.google.com/uc?export=view&id=1cG6SbLuMcJjttnP7h0bZgCKuWJ66QY9H").build()
                ))
                .build());

        routines.add(Routine.builder()
                .name("외출 전 향수 / 섬유 탈취제 뿌리기")
                .timeOfDay(TimeOfDay.MORNING)
                .iconImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/routine/icon/MORNING/%E1%84%92%E1%85%A3%E1%86%BC%E1%84%89%E1%85%AE.png")
                .tipList(createTips("가까운 거리에서 직접 뿌리면 자극적일 수 있습니다.", "계절에 맞는 향을 선택하세요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).content("손목, 귀 뒤, 옷 위에 가볍게 분사합니다.").detailImage("https://drive.google.com/uc?export=view&id=1ol5F4uY1qeueDFkRrp-UHTpNonquYdiL").build(),
                        RoutineElement.builder().step(2).content("공중에 뿌린 뒤 걸어가며 은은하게 흡수시킵니다.").detailImage("https://drive.google.com/uc?export=view&id=1QtlZofuKLAXVj8f10bqoetBjDMwC3Vys").build()
                ))
                .build());

        // LUNCH 루틴
        routines.add(Routine.builder()
                .name("점심 식사 후 양치하기")
                .timeOfDay(TimeOfDay.LUNCH)
                .iconImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/routine/icon/LUNCH/%E1%84%8B%E1%85%A3%E1%86%BC%E1%84%8E%E1%85%B5.png")
                .tipList(createTips("칫솔질 전 가볍게 물로 헹구면 더 효과적입니다.", "치실이나 가글을 함께 사용하면 좋습니다."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).content("식사 후 30분 뒤 양치하세요.").build(),
                        RoutineElement.builder().step(2).content("혀까지 부드럽게 닦아 마무리합니다.").build()
                ))
                .build());

        routines.add(Routine.builder()
                .name("미스트 뿌리기")
                .timeOfDay(TimeOfDay.LUNCH)
                .iconImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/routine/icon/LUNCH/%E1%84%86%E1%85%B5%E1%84%89%E1%85%B3%E1%84%90%E1%85%B3.png")
                .tipList(createTips("미스트는 미온수 성분이 든 제품이 자극이 적습니다.", "뿌린 뒤 휴지로 닦아내지 말고 흡수시켜주세요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).content("얼굴에서 20cm 정도 거리를 두고 미스트를 분사하세요.").build(),
                        RoutineElement.builder().step(2).content("가볍게 손으로 두드려 흡수시킵니다.").build()
                ))
                .build());

        routines.add(Routine.builder()
                .name("립밤 바르기")
                .timeOfDay(TimeOfDay.LUNCH)
                .iconImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/routine/icon/LUNCH/%E1%84%85%E1%85%B5%E1%86%B8%E1%84%87%E1%85%A1%E1%86%B3.png")
                .tipList(createTips("자기 전 두껍게 바르면 ‘립 마스크’ 효과가 있습니다.", "무향·무색 제품을 쓰면 덧바르기 편해요."))
                .elementList(Collections.singletonList(
                        RoutineElement.builder().step(1).content("입술 중앙부터 바깥쪽으로 골고루 바르세요.").detailImage("https://drive.google.com/uc?export=view&id=105-219qac66hj43R9WaQkZIibhYkSJl-").build()
                ))
                .build());

        routines.add(Routine.builder()
                .name("핸드크림 바르기")
                .timeOfDay(TimeOfDay.LUNCH)
                .iconImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/routine/icon/LUNCH/%E1%84%92%E1%85%A2%E1%86%AB%E1%84%83%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%B7.png")
                .tipList(createTips("건조하기 쉬운 손등부터 챙기세요.", "끈적임이 적은 제형은 낮에 쓰기 좋아요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).content("적당량을 손등에 짜서 양손으로 문질러 주세요.").build(),
                        RoutineElement.builder().step(2).content("손가락 사이와 손톱 큐티클까지 꼼꼼히 발라주세요.").build()
                ))
                .build());

        routines.add(Routine.builder()
                .name("거울 보고 상태 확인하기")
                .timeOfDay(TimeOfDay.LUNCH)
                .iconImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/routine/icon/LUNCH/%E1%84%80%E1%85%A5%E1%84%8B%E1%85%AE%E1%86%AF.png")
                .tipList(createTips("중요한 회의 전 습관화하면 깔끔한 이미지를 유지할 수 있어요.", "표정까지 확인하면 자신감이 더 생겨요."))
                .elementList(Collections.singletonList(
                        RoutineElement.builder().step(1).content("얼굴, 머리카락, 옷매무새를 빠르게 체크하세요.").detailImage("https://drive.google.com/uc?export=view&id=1sdMU9TDZy36WvzN1MBeuwsSwssqcEL5Y").build()
                ))
                .build());

        // EVENING 루틴
        routines.add(Routine.builder()
                .name("클렌징 오일 / 폼으로 이중 세안하기")
                .timeOfDay(TimeOfDay.EVENING)
                .iconImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/routine/icon/EVENING/%E1%84%8F%E1%85%B3%E1%86%AF%E1%84%85%E1%85%A6%E1%86%AB%E1%84%8C%E1%85%B5%E1%86%BC.png")
                .tipList(createTips("눈가, 콧방울은 특히 꼼꼼히 닦아주세요.", "세안 후 타올로 문지르지 말고 톡톡 두드리세요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).content("클렌징 오일로 메이크업과 노폐물을 녹여줍니다.").build(),
                        RoutineElement.builder().step(2).content("폼 클렌저로 거품을 내어 피부결 방향으로 세안합니다.").build(),
                        RoutineElement.builder().step(3).content("미지근한 물로 꼼꼼히 헹궈냅니다.").build()
                ))
                .build());

        routines.add(Routine.builder()
                .name("보습 크림 바르기")
                .timeOfDay(TimeOfDay.EVENING)
                .iconImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/routine/icon/EVENING/%E1%84%87%E1%85%A9%E1%84%89%E1%85%B3%E1%86%B8%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%B7.png")
                .tipList(createTips("겨울철엔 평소보다 조금 더 두껍게 발라주세요.", "피부 결 방향대로 바르는 게 흡수에 좋아요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).content("손가락 끝에 크림을 소량씩 덜어냅니다.").build(),
                        RoutineElement.builder().step(2).content("볼, 이마, 턱, 코 순으로 발라주세요.").build(),
                        RoutineElement.builder().step(3).content("손바닥으로 지그시 눌러 흡수시킵니다.").build()
                ))
                .build());

        routines.add(Routine.builder()
                .name("손톱 및 발톱 관리")
                .timeOfDay(TimeOfDay.EVENING)
                .iconImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/routine/icon/EVENING/%E1%84%89%E1%85%A9%E1%86%AB%E1%84%90%E1%85%A9%E1%86%B8.png")
                .tipList(createTips("자르기보다는 갈아서 관리하면 손상이 적습니다.", "취침 전 관리하면 흡수가 잘돼요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).content("손톱깎이나 파일로 길이를 정돈합니다.").build(),
                        RoutineElement.builder().step(2).content("큐티클 오일이나 로션으로 보습합니다.").build(),
                        RoutineElement.builder().step(3).content("매니큐어/투명 코팅제로 보호막을 씌워줍니다.").build()
                ))
                .build());

        routines.add(Routine.builder()
                .name("취침 전 스트레칭 하기")
                .timeOfDay(TimeOfDay.EVENING)
                .iconImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/routine/icon/EVENING/%E1%84%89%E1%85%B3%E1%84%90%E1%85%B3%E1%84%85%E1%85%A6%E1%84%8E%E1%85%B5%E1%86%BC.png")
                .tipList(createTips("스트레칭은 10분 이내가 적당합니다.", "무리하지 말고 호흡에 맞춰 하세요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).content("가볍게 목과 어깨를 돌려 풀어줍니다.").build(),
                        RoutineElement.builder().step(2).content("허리를 숙여 다리 근육을 늘려줍니다.").build(),
                        RoutineElement.builder().step(3).content("누운 상태에서 전신 스트레칭을 합니다.").build()
                ))
                .build());

        routines.add(Routine.builder()
                .name("눈 마사지하기")
                .timeOfDay(TimeOfDay.EVENING)
                .iconImage("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/routine/icon/EVENING/%E1%84%82%E1%85%AE%E1%86%AB%E1%84%86%E1%85%A1%E1%84%89%E1%85%A1%E1%84%8C%E1%85%B5.png")
                .tipList(createTips("눈가 전용 크림을 함께 쓰면 효과가 좋아요.", "너무 강하게 누르지 않도록 주의하세요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).content("눈썹 아래 뼈를 지그시 눌러줍니다.").build(),
                        RoutineElement.builder().step(2).content("눈꼬리와 관자놀이를 원을 그리며 마사지합니다.").build()
                ))
                .build());

        return routines;
    }

    private List<RoutineTip> createTips(String... contents) {
        if (contents == null || contents.length == 0) {
            return new ArrayList<>();
        }
        return Stream.of(contents)
                .map(content -> RoutineTip.builder().content(content).build())
                .collect(Collectors.toList());
    }

    private void setBidirectionalReferences(Routine routine) {
        // Routine <-> RoutineElement 연결
        if (routine.getElementList() != null) {
            for (RoutineElement element : routine.getElementList()) {
                element.setRoutine(routine);
            }
        }
        // Routine <-> RoutineTip 연결
        if (routine.getTipList() != null) {
            for (RoutineTip tip : routine.getTipList()) {
                tip.setRoutine(routine);
            }
        }
    }
}