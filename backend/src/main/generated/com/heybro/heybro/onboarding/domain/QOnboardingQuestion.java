package com.heybro.heybro.onboarding.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOnboardingQuestion is a Querydsl query type for OnboardingQuestion
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOnboardingQuestion extends EntityPathBase<OnboardingQuestion> {

    private static final long serialVersionUID = -1817162299L;

    public static final QOnboardingQuestion onboardingQuestion = new QOnboardingQuestion("onboardingQuestion");

    public final StringPath content = createString("content");

    public final NumberPath<Integer> displayOrder = createNumber("displayOrder", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<OnboardingOption, QOnboardingOption> options = this.<OnboardingOption, QOnboardingOption>createList("options", OnboardingOption.class, QOnboardingOption.class, PathInits.DIRECT2);

    public QOnboardingQuestion(String variable) {
        super(OnboardingQuestion.class, forVariable(variable));
    }

    public QOnboardingQuestion(Path<? extends OnboardingQuestion> path) {
        super(path.getType(), path.getMetadata());
    }

    public QOnboardingQuestion(PathMetadata metadata) {
        super(OnboardingQuestion.class, metadata);
    }

}

