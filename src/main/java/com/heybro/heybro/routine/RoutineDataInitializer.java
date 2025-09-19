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
    public void run(String... args) throws Exception {
        if (routineTemplateRepository.count() > 0) {
            return;
        }

        // 각 사용자 유형(UserType)별로 고유한 루틴 세트를 생성하고 저장합니다.
        for (UserType userType : UserType.values()) {
            RoutineTemplate routineTemplate = RoutineTemplate.builder()
                    .type(userType)
                    .build();

            List<Routine> routines = createInitialRoutines();

            for (Routine routine : routines) {
                routine.updateRoutineTemplate(routineTemplate);
                setBidirectionalReferences(routine);
            }

            routineTemplate.updateElementList(routines);
            routineTemplateRepository.save(routineTemplate);
        }
    }

    private List<Routine> createInitialRoutines() {
        List<Routine> routines = new ArrayList<>();

        // MORNING 루틴
        routines.add(Routine.builder()
                .name("일어나서 물 한 잔 마시기")
                .timeOfDay(TimeOfDay.MORNING)
                .iconImage("https://drive.google.com/file/d/1Vp03pqKmtYfFeLqkncbf_dTpP73brpgT/view?usp=drive_link")
                .tipList(createTips("천천히 마시면서 위장이 놀라지 않게 하세요.", "찬물보다는 미지근한 물이 소화에 더 좋아요."))
                .elementList(Collections.singletonList(
                        RoutineElement.builder()
                                .step(1)
                                .name("물 한 잔 마시기")
                                .content("기상 후 상온의 물 한 잔을 마셔 몸을 깨워주세요.")
                                .detailImage("https://drive.google.com/uc?export=view&id=12xxgmmAq96igwIujCzjGl-n8wdQKHPk-")
                                .build()
                ))
                .build());
        routines.add(Routine.builder()
                .name("건강 보조제 챙겨 먹기")
                .timeOfDay(TimeOfDay.MORNING)
                .iconImage("https://drive.google.com/file/d/1ukKs2To3sLyQYOAZ9tOxL1-zUpTGyrAm/view?usp=drive_link")
                .tipList(createTips("매일 같은 시간대에 섭취하면 습관화에 좋아요.", "커피나 차와 함께 먹지 않도록 주의하세요."))
                .elementList(Collections.singletonList(
                        RoutineElement.builder()
                                .step(1)
                                .name("건강 보조제 섭취")
                                .content("물과 함께 건강 보조제를 섭취하세요.")
                                .detailImage("https://drive.google.com/uc?export=view&id=12WVVEjTQtskpSCv75aIfSmQCsXvFXLd4")
                                .build()
                ))
                .recommendedProductList(new ArrayList<>(Arrays.asList(
                        RecommendedProduct.builder()
                                .name("고려은단 비타민C1000 이지+비타민D 120정+60정")
                                .image("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/A00000023239305ko+(1).jpg")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000232393&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EA%B1%B4%EA%B0%95%EC%8B%9D%ED%92%88&t_number=3&dispCatNo=1000002000100150002&trackingCd=Result_3")
                                .build(),

                        RecommendedProduct.builder()
                                .name("덴프스 덴마크 유산균이야기 30캡슐 1+1기획(60일분)+트루바이타민 3포")
                                .image("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/A00000023239305ko.jpg")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000206861&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EC%9C%A0%EC%82%B0%EA%B7%A0&t_number=3&dispCatNo=1000002000100240001&trackingCd=Result_3")
                                .build()
                )))
                .build());
        routines.add(Routine.builder()
                .name("토너로 피부결 정돈하기")
                .timeOfDay(TimeOfDay.MORNING)
                .iconImage("https://drive.google.com/file/d/1klmt34jWh1hDssErZRXPeLU1oa6OhcRK/view?usp=sharing")
                .tipList(createTips("세안 직후 바로 발라야 수분 손실을 막을 수 있어요.", "화장솜 대신 손바닥을 쓰면 피부 자극이 줄어듭니다."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).name("토너 덜어내기").content("세안 후 화장솜이나 손바닥에 토너를 적당히 덜어주세요.").detailImage("https://drive.google.com/uc?export=view&id=1Mn27Us_0_7gaZcywBGfn8TYLj-wPVHUG").build(),
                        RoutineElement.builder().step(2).name("피부결 따라 닦아내기").content("피부결을 따라 부드럽게 닦아내거나 두드려 흡수시킵니다.").detailImage("https://drive.google.com/uc?export=view&id=1xmvvHTnfqgp1_dxwyuMf9sYuGIfttCnJ").build(),
                        RoutineElement.builder().step(3).name("목과 귀밑에 바르기").content("남은 토너는 목이나 귀밑까지 발라주세요.").detailImage("https://drive.google.com/uc?export=view&id=1Qj-AAXxzjDUHXcxGEaepBtwvmbhBlJZz").build()
                ))
                .recommendedProductList(new ArrayList<>(Arrays.asList(
                        RecommendedProduct.builder()
                                .name("라운드랩 1025 독도 토너 500ml 기획 (+100ml)")
                                .image("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/A00000013718037ko.jpg")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000137180")
                                .build(),

                        RecommendedProduct.builder()
                                .name("토리든 다이브인 저분자 히알루론산 토너 300ml 기획(+100ml 추가 증정)")
                                .image("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/A00000017026613ko.jpg")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000170266&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%ED%86%A0%EB%84%88&t_number=2&dispCatNo=1000001000100130001&trackingCd=Result_2")
                                .build()
                )))
                .build());
        routines.add(Routine.builder()
                .name("에센스/세럼 바르기")
                .timeOfDay(TimeOfDay.MORNING)
                .iconImage("https://drive.google.com/file/d/1Q1bMGLr91Gp32nAEuL9kShLaOqQaEyX1/view?usp=sharing")
                .tipList(createTips("세럼은 피부 고민에 맞는 제품을 선택하세요.", "여러 제품을 쓸 땐 점도가 묽은 것부터 바르세요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).name("세럼 펌핑하기").content("적당량의 세럼을 손에 펌핑합니다.").build(),
                        RoutineElement.builder().step(2).name("얼굴에 펴 바르기").content("피부 중심에서 바깥쪽으로 부드럽게 발라주세요.").build(),
                        RoutineElement.builder().step(3).name("손으로 감싸 흡수시키기").content("손바닥으로 얼굴을 감싸며 흡수시킵니다.").build()
                ))
                .recommendedProductList(new ArrayList<>(Arrays.asList(
                        RecommendedProduct.builder()
                                .name("메디힐 마데카소사이드 흔적 리페어 세럼 40+40+10mL")
                                .image("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/A00000022644978ko.png")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000226449&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EC%97%90%EC%84%BC%EC%8A%A4&t_number=1&dispCatNo=1000001000100140001&trackingCd=Result_1")
                                .build(),

                        RecommendedProduct.builder()
                                .name("웰라쥬 리얼 히알루로닉 블루 100 앰플 75ml 더블기획")
                                .image("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/A00000023188510ko.jpg")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000231885&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EC%97%90%EC%84%BC%EC%8A%A4&t_number=7&dispCatNo=1000001000100140001&trackingCd=Result_7")
                                .build()
                )))
                .build());
        routines.add(Routine.builder()
                .name("로션/크림 바르기")
                .timeOfDay(TimeOfDay.MORNING)
                .iconImage("https://drive.google.com/file/d/17G9fijMamsQy8XZVG2Rqp8XAvJMuo8DY/view?usp=sharing")
                .tipList(createTips("목까지 함께 발라야 관리가 완성돼요.", "아침에는 가벼운 로션, 저녁엔 영양 크림이 적합합니다."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).name("로션/크림 덜어내기").content("손등만한 크기의 양을 손바닥에 덜어주세요.").build(),
                        RoutineElement.builder().step(2).name("얼굴에 펴 바르기").content("볼 → 이마 → 턱 순으로 바르며 고르게 펴 발라줍니다.").build(),
                        RoutineElement.builder().step(3).name("손으로 눌러 흡수시키기").content("손바닥으로 얼굴을 지그시 눌러 흡수시킵니다.").build()
                ))
                .recommendedProductList(new ArrayList<>(Arrays.asList(
                        RecommendedProduct.builder()
                                .name("에스트라 아토베리어365 로션 150ml")
                                .image("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/A00000019832102ko.jpg")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000198321&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EB%A1%9C%EC%85%98&t_number=1&dispCatNo=1000001000100160001,1000001000800130001&trackingCd=Result_1")
                                .build(),

                        RecommendedProduct.builder()
                                .name("피지오겔 DMT 페이셜 로션 200ml 기획 (+로션 50ml)")
                                .image("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/A00000023218605ko.png")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000232186&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EB%A1%9C%EC%85%98&t_number=2&dispCatNo=1000001000100160001,1000001000800130001&trackingCd=Result_2")
                                .build()
                )))
                .build());
        routines.add(Routine.builder()
                .name("면도하기")
                .timeOfDay(TimeOfDay.MORNING)
                .iconImage("https://drive.google.com/file/d/1aQ_O3PPuHEhiY0k6iWFJ3TvsQSb30O2C/view?usp=sharing")
                .tipList(createTips("쉐이빙 폼을 잘 발라야 칼날이 피부에 밀착돼서 상처가 나지않아요.", "잘 밀리지 않는 부분은 피부를 반대 방향으로 당겨서 한번 더 밀어주세요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).name("쉐이빙 폼 바르기").content("쉐이빙 폼을 거품을 잘내서 얼굴에 발라주세요.").detailImage("https://drive.google.com/uc?export=view&id=1-wwxnGNwTDlbx-_NI59XcwhW6phCHMvA").build(),
                        RoutineElement.builder().step(2).name("결대로 면도 후 역방향 마무리").content("처음엔 수염의 결대로 면도를 하고, 깔끔하게 역방향으로 한번 더 마무리 합니다.").detailImage("https://drive.google.com/uc?export=view&id=1j_JnkzKL5N8KxjAij2zdMW-kjaoVUvi6").build(),
                        RoutineElement.builder().step(3).name("미온수로 헹구기").content("미온수로 쉐이빙 폼이 남지 않게 꼼꼼히 행궈주세요.").detailImage("https://drive.google.com/uc?export=view&id=1cG6SbLuMcJjttnP7h0bZgCKuWJ66QY9H").build()
                ))
                .recommendedProductList(new ArrayList<>(Arrays.asList(
                        RecommendedProduct.builder()
                                .name("니베아 맨 센서티브 쉐이빙 폼 더블기획 (200ml+200ml)")
                                .image("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/A00000020014916ko.jpg")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000200149&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EC%89%90%EC%9D%B4%EB%B9%99%ED%8F%BC&t_number=1&dispCatNo=1000001000300190005,1000001000700100003&trackingCd=Result_1")
                                .build(),

                        RecommendedProduct.builder()
                                .name("질레트 프로쉴드 옐로우 파워 기획 (핸들+4입날+미니젤)")
                                .image("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/A00000021091810ko.jpg")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000210918&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EB%A9%B4%EB%8F%84&t_number=7&dispCatNo=1000001000300190002,1000001000300190004,1000001000700100001&trackingCd=Result_7")
                                .build()
                )))
                .build());
        routines.add(Routine.builder()
                .name("외출 전 향수 / 섬유 탈취제 뿌리기")
                .timeOfDay(TimeOfDay.MORNING)
                .iconImage("https://drive.google.com/file/d/1nohQ6AnD_HFh9PqjnFjoBBL6rAndQZxk/view?usp=sharing")
                .tipList(createTips("가까운 거리에서 직접 뿌리면 자극적일 수 있습니다.", "계절에 맞는 향을 선택하세요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).name("몸과 옷에 분사하기").content("손목, 귀 뒤, 옷 위에 가볍게 분사합니다.").detailImage("https://drive.google.com/uc?export=view&id=1ol5F4uY1qeueDFkRrp-UHTpNonquYdiL").build(),
                        RoutineElement.builder().step(2).name("공중에 뿌려 흡수시키기").content("공중에 뿌린 뒤 걸어가며 은은하게 흡수시킵니다.").detailImage("https://drive.google.com/uc?export=view&id=1QtlZofuKLAXVj8f10bqoetBjDMwC3Vys").build()
                ))
                .recommendedProductList(new ArrayList<>(Arrays.asList(
                        RecommendedProduct.builder()
                                .name("센녹 퍼퓸 6종&솔리드퍼퓸 2종")
                                .image("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/A00000020476786ko.jpg")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000204767&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%ED%96%A5%EC%88%98&t_number=2&dispCatNo=1000001000500130003&trackingCd=Result_2")
                                .build(),

                        RecommendedProduct.builder()
                                .name("라운드어라운드 편백 클린 스프레이160ml/160ml리필")
                                .image("https://heybro-bucket.s3.ap-northeast-2.amazonaws.com/images/A00000016703023ko.jpg")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000167030&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EC%84%AC%EC%9C%A0&t_number=2&dispCatNo=1000001000500120005&trackingCd=Result_2")
                                .build()
                )))
                .build());
        // LUNCH 루틴
        routines.add(Routine.builder()
                .name("점심 식사 후 양치하기")
                .timeOfDay(TimeOfDay.LUNCH)
                .iconImage("https://drive.google.com/file/d/1dfr1pTE-eEpXPKyi3FEUeWl040cSmBBY/view?usp=sharing")
                .tipList(createTips("칫솔질 전 가볍게 물로 헹구면 더 효과적입니다.", "치실이나 가글을 함께 사용하면 좋습니다."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).name("식후 30분 뒤 양치").content("식사 후 30분 뒤 양치하세요.").build(),
                        RoutineElement.builder().step(2).name("혀 닦아 마무리하기").content("혀까지 부드럽게 닦아 마무리합니다.").build()
                ))
                .recommendedProductList(new ArrayList<>(Arrays.asList(
                        RecommendedProduct.builder()
                                .name("유시몰 화이트닝 퍼플코렉터 치약 106g")
                                .image("https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/550/10/0000/0019/A00000019932937ko.jpg?l=ko")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000199329&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EC%B9%98%EC%95%BD&t_number=4&dispCatNo=1000002000300160001,1000002000300160002,1000002000300160003&trackingCd=Result_4")
                                .build(),

                        RecommendedProduct.builder()
                                .name("테라브레스 오랄린스 패밀리사이즈 2종 택1 (본품1L+150ml 추가 증정)")
                                .image("https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/550/10/0000/0015/A00000015853904ko.jpg?l=ko")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000158539&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EA%B0%80%EA%B8%80&t_number=1&dispCatNo=1000002000300170001&trackingCd=Result_1")
                                .build()
                )))
                .build());
        routines.add(Routine.builder()
                .name("미스트 뿌리기")
                .timeOfDay(TimeOfDay.LUNCH)
                .iconImage("https://drive.google.com/file/d/15YJ5DluvK9upjKenT4Hwlbw8T3caLnb-/view?usp=sharing")
                .tipList(createTips("미스트는 미온수 성분이 든 제품이 자극이 적습니다.", "뿌린 뒤 휴지로 닦아내지 말고 흡수시켜주세요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).name("거리 두고 분사하기").content("얼굴에서 20cm 정도 거리를 두고 미스트를 분사하세요.").build(),
                        RoutineElement.builder().step(2).name("두드려 흡수시키기").content("가볍게 손으로 두드려 흡수시킵니다.").build()
                ))
                .recommendedProductList(new ArrayList<>(Arrays.asList(
                        RecommendedProduct.builder()
                                .name("바이오더마 하이드라비오 에센스로션 200ml (+안개분사 미스트 증정)")
                                .image("https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/550/10/0000/0022/A00000022608611ko.jpg?l=ko")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000226086&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EB%AF%B8%EC%8A%A4%ED%8A%B8&t_number=1&dispCatNo=1000001000100140001,1000001000800130005&trackingCd=Result_1")
                                .build(),

                        RecommendedProduct.builder()
                                .name("아벤느 오 떼르말 미스트 300ml*2 + 50ml 기획")
                                .image("https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/550/10/0000/0016/A00000016643614ko.jpg?l=ko")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000166436&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EB%AF%B8%EC%8A%A4%ED%8A%B8&t_number=13&dispCatNo=1000001000100100001,1000001000800130006&trackingCd=Result_11_30")
                                .build()
                )))
                .build());
        routines.add(Routine.builder()
                .name("립밤 바르기")
                .timeOfDay(TimeOfDay.LUNCH)
                .iconImage("https://drive.google.com/file/d/1zup8mc8jcGqDsFs0wXm3wuIsAGh-Y9rs/view?usp=sharing")
                .tipList(createTips("자기 전 두껍게 바르면 ‘립 마스크’ 효과가 있습니다.", "무향·무색 제품을 쓰면 덧바르기 편해요."))
                .elementList(Collections.singletonList(
                        RoutineElement.builder().step(1).name("입술에 골고루 바르기").content("입술 중앙부터 바깥쪽으로 골고루 바르세요.").detailImage("https://drive.google.com/uc?export=view&id=105-219qac66hj43R9WaQkZIibhYkSJl-").build()
                ))
                .recommendedProductList(new ArrayList<>(Arrays.asList(
                        RecommendedProduct.builder()
                                .name("바이오더마 아토덤 립스틱 1+1 기획 (저자극 립밤)")
                                .image("https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/550/10/0000/0017/A00000017111325ko.jpg?l=ko")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000171113&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EB%A6%BD%EB%B0%A4&t_number=11&dispCatNo=1000001000200060001,1000001000800040003&trackingCd=Result_11_30")
                                .build(),

                        RecommendedProduct.builder()
                                .name("버츠비 모이스춰라이징 립밤 8종 택1")
                                .image("https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/550/10/0000/0010/A00000010640018ko.png?l=ko")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000106400&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EB%A6%BD%EB%B0%A4&t_number=45&dispCatNo=1000001000200060001&trackingCd=Result_31_60")
                                .build()
                )))
                .build());
        routines.add(Routine.builder()
                .name("핸드크림 바르기")
                .timeOfDay(TimeOfDay.LUNCH)
                .iconImage("https://drive.google.com/file/d/1zabKYnzrq7_SM4o-ZdiOSORnqWDQE6T5/view?usp=sharing")
                .tipList(createTips("건조하기 쉬운 손등부터 챙기세요.", "끈적임이 적은 제형은 낮에 쓰기 좋아요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).name("손등에 덜어 문지르기").content("적당량을 손등에 짜서 양손으로 문질러 주세요.").build(),
                        RoutineElement.builder().step(2).name("손가락과 큐티클에 바르기").content("손가락 사이와 손톱 큐티클까지 꼼꼼히 발라주세요.").build()
                ))
                .recommendedProductList(new ArrayList<>(Arrays.asList(
                        RecommendedProduct.builder()
                                .name("헤트라스 망고씨드버터 퍼퓸 핸드크림 50ml 4종")
                                .image("https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/550/10/0000/0020/A00000020053711ko.jpg?l=ko")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000200537&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%ED%95%B8%EB%93%9C%ED%81%AC%EB%A6%BC&t_number=7&dispCatNo=1000001000300160002&trackingCd=Result_7")
                                .build(),

                        RecommendedProduct.builder()
                                .name("타입넘버 핸드크림 40ml 6종 (기획/단품)")
                                .image("https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/550/10/0000/0016/A000000165760100ko.jpg?l=ko")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000165760&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%ED%95%B8%EB%93%9C%ED%81%AC%EB%A6%BC&t_number=1&dispCatNo=1000001000300160002&trackingCd=Result_1")
                                .build()
                )))
                .build());
        routines.add(Routine.builder()
                .name("거울 보고 상태 확인하기")
                .timeOfDay(TimeOfDay.LUNCH)
                .iconImage("https://drive.google.com/file/d/1pHcsbjGfSPVAc5Ozd_on0gEZy8_4si9e/view?usp=sharing")
                .tipList(createTips("중요한 회의 전 습관화하면 깔끔한 이미지를 유지할 수 있어요.", "표정까지 확인하면 자신감이 더 생겨요."))
                .elementList(Collections.singletonList(
                        RoutineElement.builder().step(1).name("얼굴과 옷매무새 체크").content("얼굴, 머리카락, 옷매무새를 빠르게 체크하세요.").detailImage("https://drive.google.com/uc?export=view&id=1sdMU9TDZy36WvzN1MBeuwsSwssqcEL5Y").build()
                ))
                .build());
        // EVENING 루틴
        routines.add(Routine.builder()
                .name("클렌징 오일 / 폼으로 이중 세안하기")
                .timeOfDay(TimeOfDay.EVENING)
                .iconImage("https://drive.google.com/file/d/1J6vOsP4wPlHgnD5guVENqYFCp1_IKM5V/view?usp=sharing")
                .tipList(createTips("눈가, 콧방울은 특히 꼼꼼히 닦아주세요.", "세안 후 타올로 문지르지 말고 톡톡 두드리세요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).name("클렌징 오일로 녹이기").content("클렌징 오일로 메이크업과 노폐물을 녹여줍니다.").build(),
                        RoutineElement.builder().step(2).name("폼 클렌저로 세안하기").content("폼 클렌저로 거품을 내어 피부결 방향으로 세안합니다.").build(),
                        RoutineElement.builder().step(3).name("미온수로 헹궈내기").content("미지근한 물로 꼼꼼히 헹궈냅니다.").build()
                ))
                .recommendedProductList(new ArrayList<>(Arrays.asList(
                        RecommendedProduct.builder()
                                .name("마녀공장 퓨어 클렌징 오일 200ml 리필기획 (+리필 200ml+캡슐형 2mlx3)")
                                .image("https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/550/10/0000/0023/A00000023199645ko.jpg?l=ko")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000231996&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%ED%81%B4%EB%A0%8C%EC%A7%95%20%EC%98%A4%EC%9D%BC&t_number=1&dispCatNo=1000001001000040001&trackingCd=Result_1")
                                .build(),

                        RecommendedProduct.builder()
                                .name("휩드 비건 팩클렌저 130g 기획 3종 (머그트리/무화버터/유자몽)")
                                .image("https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/550/10/0000/0021/A00000021751125ko.png?l=ko")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000217511&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%ED%81%B4%EB%A0%8C%EC%A7%95%ED%8F%BC&t_number=8&dispCatNo=1000001001000010001,1000001001000010003&trackingCd=Result_8")
                                .build()
                )))
                .build());
        routines.add(Routine.builder()
                .name("보습 크림 바르기")
                .timeOfDay(TimeOfDay.EVENING)
                .iconImage("https://drive.google.com/file/d/1mKJhBfjPlFgz89Ck3hNEQ5Cc0_0fT2BW/view?usp=sharing")
                .tipList(createTips("겨울철엔 평소보다 조금 더 두껍게 발라주세요.", "피부 결 방향대로 바르는 게 흡수에 좋아요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).name("크림 덜어내기").content("손가락 끝에 크림을 소량씩 덜어냅니다.").build(),
                        RoutineElement.builder().step(2).name("얼굴 부위별로 바르기").content("볼, 이마, 턱, 코 순으로 발라주세요.").build(),
                        RoutineElement.builder().step(3).name("눌러서 흡수시키기").content("손바닥으로 지그시 눌러 흡수시킵니다.").build()
                ))
                .recommendedProductList(new ArrayList<>(Arrays.asList(
                        RecommendedProduct.builder()
                                .name("에스트라 아토베리어365 크림 80ml 기획 (+하이드로 에센스25ml+세라-히알 앰플7ml)")
                                .image("https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/550/10/0000/0022/A00000022283309ko.jpg?l=ko")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000222833&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EB%B3%B4%EC%8A%B5%ED%81%AC%EB%A6%BC&t_number=1&dispCatNo=1000001000100150001,1000001000800130001&trackingCd=Result_1")
                                .build(),

                        RecommendedProduct.builder()
                                .name("바이오더마 시카비오 포마드 100ml 기획(+시카비오 크림+5ml 2ea)")
                                .image("https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/550/10/0000/0023/A00000023213802ko.jpg?l=ko")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000232138&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EB%B3%B4%EC%8A%B5%ED%81%AC%EB%A6%BC&t_number=4&dispCatNo=1000001000100150001,1000001000800130001&trackingCd=Result_4")
                                .build()
                )))
                .build());
        routines.add(Routine.builder()
                .name("손톱 및 발톱 관리")
                .timeOfDay(TimeOfDay.EVENING)
                .iconImage("https://drive.google.com/file/d/18mX5LE-Eox8Jg9qamyIIF5OwOZDH2QbD/view?usp=sharing")
                .tipList(createTips("자르기보다는 갈아서 관리하면 손상이 적습니다.", "취침 전 관리하면 흡수가 잘돼요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).name("길이 정돈하기").content("손톱깎이나 파일로 길이를 정돈합니다.").build(),
                        RoutineElement.builder().step(2).name("오일/로션으로 보습").content("큐티클 오일이나 로션으로 보습합니다.").build(),
                        RoutineElement.builder().step(3).name("코팅제로 보호하기").content("매니큐어/투명 코팅제로 보호막을 씌워줍니다.").build()
                ))
                .recommendedProductList(new ArrayList<>(Arrays.asList(
                        RecommendedProduct.builder()
                                .name("반디 스팀 큐티클 오일 & 큐티클 케어 키트 2종")
                                .image("https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/550/10/0000/0019/A00000019983810ko.jpg?l=ko")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000199838&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%ED%81%90%ED%8B%B0%ED%81%B4&t_number=1&dispCatNo=1000001001200040004,1000001001200040005&trackingCd=Result_1")
                                .build(),

                        RecommendedProduct.builder()
                                .name("코스노리 실크리페어 네일 크림 손톱영양제")
                                .image("https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/550/10/0000/0015/A00000015461726ko.jpg?l=ko")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000154617&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EC%86%90%ED%86%B1%EC%98%81%EC%96%91%EC%A0%9C&t_number=2&dispCatNo=1000001001200040005&trackingCd=Result_2")
                                .build()
                )))
                .build());
        routines.add(Routine.builder()
                .name("취침 전 스트레칭 하기")
                .timeOfDay(TimeOfDay.EVENING)
                .iconImage("https://drive.google.com/file/d/1j5NQU-lNrf7nW2dCRaA6Ul03ni-Afk6p/view?usp=sharing")
                .tipList(createTips("스트레칭은 10분 이내가 적당합니다.", "무리하지 말고 호흡에 맞춰 하세요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).name("목과 어깨 풀기").content("가볍게 목과 어깨를 돌려 풀어줍니다.").build(),
                        RoutineElement.builder().step(2).name("다리 근육 늘리기").content("허리를 숙여 다리 근육을 늘려줍니다.").build(),
                        RoutineElement.builder().step(3).name("전신 스트레칭").content("누운 상태에서 전신 스트레칭을 합니다.").build()
                ))
                .build());
        routines.add(Routine.builder()
                .name("눈 마사지하기")
                .timeOfDay(TimeOfDay.EVENING)
                .iconImage("https://drive.google.com/file/d/1BhJP1boulSgaUM9sElEAceUA604yK1Zh/view?usp=sharing")
                .tipList(createTips("눈가 전용 크림을 함께 쓰면 효과가 좋아요.", "너무 강하게 누르지 않도록 주의하세요."))
                .elementList(Arrays.asList(
                        RoutineElement.builder().step(1).name("눈썹 뼈 누르기").content("눈썹 아래 뼈를 지그시 눌러줍니다.").build(),
                        RoutineElement.builder().step(2).name("눈꼬리/관자놀이 마사지").content("눈꼬리와 관자놀이를 원을 그리며 마사지합니다.").build()
                ))
                .recommendedProductList(new ArrayList<>(Arrays.asList(
                        RecommendedProduct.builder()
                                .name("AHC 텐 레볼루션 리얼 아이크림 포 페이스 더블 기획")
                                .image("https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/550/10/0000/0019/A00000019036912ko.jpg?l=ko")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000190369&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EC%95%84%EC%9D%B4%ED%81%AC%EB%A6%BC&t_number=2&dispCatNo=1000001000100150002&trackingCd=Result_2")
                                .build(),

                        RecommendedProduct.builder()
                                .name("아비브 콜라겐 아이크림 부활초 튜브 30ml")
                                .image("https://image.oliveyoung.co.kr/cfimages/cf-goods/uploads/images/thumbnails/550/10/0000/0019/A00000019191209ko.jpg?l=ko")
                                .link("https://www.oliveyoung.co.kr/store/goods/getGoodsDetail.do?goodsNo=A000000191912&t_page=%ED%86%B5%ED%95%A9%EA%B2%80%EC%83%89%EA%B2%B0%EA%B3%BC%ED%8E%98%EC%9D%B4%EC%A7%80&t_click=%EA%B2%80%EC%83%89%EC%83%81%ED%92%88%EC%83%81%EC%84%B8&t_search_name=%EC%95%84%EC%9D%B4%ED%81%AC%EB%A6%BC&t_number=5&dispCatNo=1000001000100150002&trackingCd=Result_5")
                                .build()
                )))
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