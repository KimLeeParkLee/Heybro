package com.heybro.heybro.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 회원 식별키

    private String name; // 회원 이름

    private String nickname; // 닉네임

    @Column(nullable = false)
    private String email; // 이메일

    private String password; // 비밀번호

    private String gender; // 성별

    private LocalDate birthDate; // 생년월일

    private String phone; // 핸드폰 번호

    private int broPoint; // 브로 포인트

    private long totalBroPoint; // 누적 브로 포인트

    @Builder.Default
    private int broLevel = 1; // 브로 레벨

    private int experience; // 경험치

    private Time wakeUpTime; // 기상 시간

    private Time bedtime; // 취침 시간

    private String profileImageUrl; // 프로필 사진

    private boolean privacyConsent; // 개인정보 동의 여부

    private boolean marketingConsent; // 마케팅 동의 여부

    private boolean notificationEnabled; // 알림 설정 여부

    private String notificationToken; // 알림 토큰

    @Enumerated(EnumType.STRING)
    private UserType userType; // 회원 유형

    private String provider; // provider(Kakao, Google)

    private String providerId; // provider id

    public void updateUserType(UserType userType) {
        this.userType = userType;
    }

    public void earnPoints(int broPoint) {
        this.broPoint += broPoint;
        this.totalBroPoint += broPoint;
    }

    public void usePoints(int broPoint) {
        this.broPoint -= broPoint;
    }
}
