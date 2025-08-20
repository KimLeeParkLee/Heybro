package com.heybro.heybro.onboarding;

import com.heybro.heybro.onboarding.domain.OnboardingOption;
import com.heybro.heybro.onboarding.domain.OnboardingQuestion;
import com.heybro.heybro.onboarding.repository.OnboardingOptionRepository;
import com.heybro.heybro.onboarding.repository.OnboardingQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OnboardingDataInitializer implements CommandLineRunner {
    private final OnboardingQuestionRepository onboardingQuestionRepository;
    private final OnboardingOptionRepository onboardingOptionRepository;

    @Override
    public void run(String... args) throws Exception {
        // 애플리케이션 시작 시점에 코드 실행

        // 온보딩 질문 1
        OnboardingQuestion question = OnboardingQuestion.builder()
                .content("아침 세안 후 피부 느낌은 어떤가요?")
                .order(1)
                .build();

        OnboardingOption option1 = OnboardingOption.builder().content("당김").order(1).build();
        OnboardingOption option2 = OnboardingOption.builder().content("피부가 당김 + T존 유분").order(2).build();
        OnboardingOption option3 = OnboardingOption.builder().content("아무 느낌 없음").order(3).build();
        OnboardingOption option4 = OnboardingOption.builder().content("전체 유분").order(4).build();

        question.addChoice(option1);
        question.addChoice(option2);
        question.addChoice(option3);
        question.addChoice(option4);

        // 온보딩 질문 2
        question = OnboardingQuestion.builder()
                .content("오후가 되면 피부 상태가 어떤가요?")
                .order(2)
                .build();

        option1 = OnboardingOption.builder().content("유분이 거의 없음").order(1).build();
        option2 = OnboardingOption.builder().content("볼은 건조한데 T존 유분").order(2).build();
        option3 = OnboardingOption.builder().content("전체적으로 촉촉함").order(3).build();
        option4 = OnboardingOption.builder().content("전체적으로 번들거림").order(4).build();

        question.addChoice(option1);
        question.addChoice(option2);
        question.addChoice(option3);
        question.addChoice(option4);

        // 온보딩 질문 3
        question = OnboardingQuestion.builder()
                .content("평소 피부 고민은 어떤 편인가요?")
                .order(3)
                .build();

        option1 = OnboardingOption.builder().content("각질이 자주 생김").order(1).build();
        option2 = OnboardingOption.builder().content("쉽게 붉어지고 예민함").order(2).build();
        option3 = OnboardingOption.builder().content("뾰루지/트러블").order(3).build();
        option4 = OnboardingOption.builder().content("모공/피지 관리가 힘듦").order(4).build();

        question.addChoice(option1);
        question.addChoice(option2);
        question.addChoice(option3);
        question.addChoice(option4);

        // 온보딩 질문 4
        question = OnboardingQuestion.builder()
                .content("날씨가 건조할 때 피부가 어떤 편인가요?")
                .order(4)
                .build();

        option1 = OnboardingOption.builder().content("피부가 당기고 각질이 올라옴").order(1).build();
        option2 = OnboardingOption.builder().content("T존 유분 + 볼은 약간 건조").order(2).build();
        option3 = OnboardingOption.builder().content("쉽게 붉어지거나 가렵고 따가움").order(3).build();
        option4 = OnboardingOption.builder().content("별다른 변화 없음").order(4).build();

        question.addChoice(option1);
        question.addChoice(option2);
        question.addChoice(option3);
        question.addChoice(option4);

        // 온보딩 질문 5
        question = OnboardingQuestion.builder()
                .content("평소 주 활동 환경은 어디인가요?")
                .order(5)
                .build();

        option1 = OnboardingOption.builder().content("사무실/실내").order(1).build();
        option2 = OnboardingOption.builder().content("실외가 많음").order(2).build();
        option3 = OnboardingOption.builder().content("이동이 많고 유동적임").order(3).build();
        option4 = OnboardingOption.builder().content("반반").order(4).build();

        question.addChoice(option1);
        question.addChoice(option2);
        question.addChoice(option3);
        question.addChoice(option4);

        // 온보딩 질문 6
        question = OnboardingQuestion.builder()
                .content("평소 수염 관리는 어떻게 하고 있나요?")
                .order(6)
                .build();

        option1 = OnboardingOption.builder().content("수염 없음").order(1).build();
        option2 = OnboardingOption.builder().content("가끔 면도함").order(2).build();
        option3 = OnboardingOption.builder().content("자주 면도함").order(3).build();
        option4 = OnboardingOption.builder().content("기르고 있음").order(4).build();

        question.addChoice(option1);
        question.addChoice(option2);
        question.addChoice(option3);
        question.addChoice(option4);

        // 온보딩 질문 7
        question = OnboardingQuestion.builder()
                .content("운동/외부 활동 빈도는 어느정도인가요?")
                .order(7)
                .build();

        option1 = OnboardingOption.builder().content("거의 안 함").order(1).build();
        option2 = OnboardingOption.builder().content("주 1~2회 정도").order(2).build();
        option3 = OnboardingOption.builder().content("자주 하는 편").order(3).build();
        option4 = OnboardingOption.builder().content("매일 실외 활동이 있음").order(4).build();

        question.addChoice(option1);
        question.addChoice(option2);
        question.addChoice(option3);
        question.addChoice(option4);

        // 온보딩 질문 8
        question = OnboardingQuestion.builder()
                .content("본인의 스타일 성향을 골라주세요.")
                .order(8)
                .build();

        option1 = OnboardingOption.builder().content("깔끔하고 단정한 편").order(1).build();
        option2 = OnboardingOption.builder().content("자연스럽고 편안한 스타일").order(2).build();
        option3 = OnboardingOption.builder().content("트렌디하고 스타일리시한 편").order(3).build();
        option4 = OnboardingOption.builder().content("스타일에는 큰 관심 없음").order(4).build();

        question.addChoice(option1);
        question.addChoice(option2);
        question.addChoice(option3);
        question.addChoice(option4);

        // 온보딩 질문 9
        question = OnboardingQuestion.builder()
                .content("평소 관심있는 관리 항목은 무엇인가요?")
                .order(9)
                .build();

        option1 = OnboardingOption.builder().content("셔츠/슬랙스 같은 출근복").order(1).build();
        option2 = OnboardingOption.builder().content("방수 자켓, 트레이닝복 등 활동복").order(2).build();
        option3 = OnboardingOption.builder().content("향수, 미용 기기, 스타일 아이템").order(3).build();
        option4 = OnboardingOption.builder().content("속옷/세제 같은 생필품").order(4).build();

        question.addChoice(option1);
        question.addChoice(option2);
        question.addChoice(option3);
        question.addChoice(option4);

        question = OnboardingQuestion.builder()
                .content("루틴 관리에 대해 어떻게 생각하시나요?")
                .order(10)
                .build();

        option1 = OnboardingOption.builder().content("정해준 대로 따르는게 편함").order(1).build();
        option2 = OnboardingOption.builder().content("간단하게만 하면 좋음").order(2).build();
        option3 = OnboardingOption.builder().content("스스로 조절하고 싶음").order(3).build();
        option4 = OnboardingOption.builder().content("매번 새로하기 귀찮음").order(4).build();

        question.addChoice(option1);
        question.addChoice(option2);
        question.addChoice(option3);
        question.addChoice(option4);
    }
}
