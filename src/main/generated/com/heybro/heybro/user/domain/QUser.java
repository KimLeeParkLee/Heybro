package com.heybro.heybro.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 150552799L;

    public static final QUser user = new QUser("user");

    public final ListPath<com.heybro.heybro.qna.domain.Answer, com.heybro.heybro.qna.domain.QAnswer> answers = this.<com.heybro.heybro.qna.domain.Answer, com.heybro.heybro.qna.domain.QAnswer>createList("answers", com.heybro.heybro.qna.domain.Answer.class, com.heybro.heybro.qna.domain.QAnswer.class, PathInits.DIRECT2);

    public final TimePath<java.time.LocalTime> bedtime = createTime("bedtime", java.time.LocalTime.class);

    public final DatePath<java.time.LocalDate> birthDate = createDate("birthDate", java.time.LocalDate.class);

    public final NumberPath<Integer> broLevel = createNumber("broLevel", Integer.class);

    public final NumberPath<Integer> broPoint = createNumber("broPoint", Integer.class);

    public final ListPath<com.heybro.heybro.coupon.domain.CouponPurchase, com.heybro.heybro.coupon.domain.QCouponPurchase> couponPurchases = this.<com.heybro.heybro.coupon.domain.CouponPurchase, com.heybro.heybro.coupon.domain.QCouponPurchase>createList("couponPurchases", com.heybro.heybro.coupon.domain.CouponPurchase.class, com.heybro.heybro.coupon.domain.QCouponPurchase.class, PathInits.DIRECT2);

    public final ListPath<com.heybro.heybro.routine.domain.DailyRoutineLog, com.heybro.heybro.routine.domain.QDailyRoutineLog> dailyRoutineLogs = this.<com.heybro.heybro.routine.domain.DailyRoutineLog, com.heybro.heybro.routine.domain.QDailyRoutineLog>createList("dailyRoutineLogs", com.heybro.heybro.routine.domain.DailyRoutineLog.class, com.heybro.heybro.routine.domain.QDailyRoutineLog.class, PathInits.DIRECT2);

    public final StringPath email = createString("email");

    public final NumberPath<Integer> experience = createNumber("experience", Integer.class);

    public final StringPath gender = createString("gender");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath marketingConsent = createBoolean("marketingConsent");

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final BooleanPath notificationEnabled = createBoolean("notificationEnabled");

    public final StringPath notificationToken = createString("notificationToken");

    public final ListPath<com.heybro.heybro.onboarding.domain.UserOnboardingAnswer, com.heybro.heybro.onboarding.domain.QUserOnboardingAnswer> onboardingAnswers = this.<com.heybro.heybro.onboarding.domain.UserOnboardingAnswer, com.heybro.heybro.onboarding.domain.QUserOnboardingAnswer>createList("onboardingAnswers", com.heybro.heybro.onboarding.domain.UserOnboardingAnswer.class, com.heybro.heybro.onboarding.domain.QUserOnboardingAnswer.class, PathInits.DIRECT2);

    public final StringPath password = createString("password");

    public final StringPath phone = createString("phone");

    public final ListPath<com.heybro.heybro.point.domain.PointHistory, com.heybro.heybro.point.domain.QPointHistory> pointHistories = this.<com.heybro.heybro.point.domain.PointHistory, com.heybro.heybro.point.domain.QPointHistory>createList("pointHistories", com.heybro.heybro.point.domain.PointHistory.class, com.heybro.heybro.point.domain.QPointHistory.class, PathInits.DIRECT2);

    public final BooleanPath privacyConsent = createBoolean("privacyConsent");

    public final StringPath profileImage = createString("profileImage");

    public final StringPath provider = createString("provider");

    public final StringPath providerId = createString("providerId");

    public final ListPath<com.heybro.heybro.qna.domain.Question, com.heybro.heybro.qna.domain.QQuestion> questions = this.<com.heybro.heybro.qna.domain.Question, com.heybro.heybro.qna.domain.QQuestion>createList("questions", com.heybro.heybro.qna.domain.Question.class, com.heybro.heybro.qna.domain.QQuestion.class, PathInits.DIRECT2);

    public final ListPath<com.heybro.heybro.coupon.domain.CouponPurchase, com.heybro.heybro.coupon.domain.QCouponPurchase> sentCoupons = this.<com.heybro.heybro.coupon.domain.CouponPurchase, com.heybro.heybro.coupon.domain.QCouponPurchase>createList("sentCoupons", com.heybro.heybro.coupon.domain.CouponPurchase.class, com.heybro.heybro.coupon.domain.QCouponPurchase.class, PathInits.DIRECT2);

    public final ListPath<com.heybro.heybro.skin.domain.SkinDiagnosis, com.heybro.heybro.skin.domain.QSkinDiagnosis> skinDiagnoses = this.<com.heybro.heybro.skin.domain.SkinDiagnosis, com.heybro.heybro.skin.domain.QSkinDiagnosis>createList("skinDiagnoses", com.heybro.heybro.skin.domain.SkinDiagnosis.class, com.heybro.heybro.skin.domain.QSkinDiagnosis.class, PathInits.DIRECT2);

    public final NumberPath<Long> totalBroPoint = createNumber("totalBroPoint", Long.class);

    public final ListPath<UserRoutine, QUserRoutine> userRoutines = this.<UserRoutine, QUserRoutine>createList("userRoutines", UserRoutine.class, QUserRoutine.class, PathInits.DIRECT2);

    public final EnumPath<UserType> userType = createEnum("userType", UserType.class);

    public final TimePath<java.time.LocalTime> wakeupTime = createTime("wakeupTime", java.time.LocalTime.class);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

