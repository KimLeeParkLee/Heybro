package com.heybro.heybro.qna.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAnswerImage is a Querydsl query type for AnswerImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAnswerImage extends EntityPathBase<AnswerImage> {

    private static final long serialVersionUID = -1048766422L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAnswerImage answerImage1 = new QAnswerImage("answerImage1");

    public final QAnswer answer;

    public final StringPath answerImage = createString("answerImage");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> sortOrder = createNumber("sortOrder", Integer.class);

    public QAnswerImage(String variable) {
        this(AnswerImage.class, forVariable(variable), INITS);
    }

    public QAnswerImage(Path<? extends AnswerImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAnswerImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAnswerImage(PathMetadata metadata, PathInits inits) {
        this(AnswerImage.class, metadata, inits);
    }

    public QAnswerImage(Class<? extends AnswerImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.answer = inits.isInitialized("answer") ? new QAnswer(forProperty("answer"), inits.get("answer")) : null;
    }

}

