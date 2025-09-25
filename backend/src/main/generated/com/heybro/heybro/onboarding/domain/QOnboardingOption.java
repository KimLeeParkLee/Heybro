package com.heybro.heybro.onboarding.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOnboardingOption is a Querydsl query type for OnboardingOption
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOnboardingOption extends EntityPathBase<OnboardingOption> {

    private static final long serialVersionUID = 2126611988L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOnboardingOption onboardingOption = new QOnboardingOption("onboardingOption");

    public final StringPath content = createString("content");

    public final NumberPath<Integer> displayOrder = createNumber("displayOrder", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QOnboardingQuestion question;

    public QOnboardingOption(String variable) {
        this(OnboardingOption.class, forVariable(variable), INITS);
    }

    public QOnboardingOption(Path<? extends OnboardingOption> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOnboardingOption(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOnboardingOption(PathMetadata metadata, PathInits inits) {
        this(OnboardingOption.class, metadata, inits);
    }

    public QOnboardingOption(Class<? extends OnboardingOption> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.question = inits.isInitialized("question") ? new QOnboardingQuestion(forProperty("question")) : null;
    }

}

