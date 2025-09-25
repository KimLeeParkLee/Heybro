package com.heybro.heybro.onboarding.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserOnboardingAnswer is a Querydsl query type for UserOnboardingAnswer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserOnboardingAnswer extends EntityPathBase<UserOnboardingAnswer> {

    private static final long serialVersionUID = -1346697912L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserOnboardingAnswer userOnboardingAnswer = new QUserOnboardingAnswer("userOnboardingAnswer");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QOnboardingQuestion question;

    public final QOnboardingOption selectedOption;

    public final com.heybro.heybro.user.domain.QUser user;

    public QUserOnboardingAnswer(String variable) {
        this(UserOnboardingAnswer.class, forVariable(variable), INITS);
    }

    public QUserOnboardingAnswer(Path<? extends UserOnboardingAnswer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserOnboardingAnswer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserOnboardingAnswer(PathMetadata metadata, PathInits inits) {
        this(UserOnboardingAnswer.class, metadata, inits);
    }

    public QUserOnboardingAnswer(Class<? extends UserOnboardingAnswer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.question = inits.isInitialized("question") ? new QOnboardingQuestion(forProperty("question")) : null;
        this.selectedOption = inits.isInitialized("selectedOption") ? new QOnboardingOption(forProperty("selectedOption"), inits.get("selectedOption")) : null;
        this.user = inits.isInitialized("user") ? new com.heybro.heybro.user.domain.QUser(forProperty("user")) : null;
    }

}

