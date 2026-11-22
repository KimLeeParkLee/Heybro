package com.heybro.heybro.routine.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRoutineTip is a Querydsl query type for RoutineTip
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoutineTip extends EntityPathBase<RoutineTip> {

    private static final long serialVersionUID = 830314762L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRoutineTip routineTip = new QRoutineTip("routineTip");

    public final StringPath content = createString("content");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QRoutine routine;

    public QRoutineTip(String variable) {
        this(RoutineTip.class, forVariable(variable), INITS);
    }

    public QRoutineTip(Path<? extends RoutineTip> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRoutineTip(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRoutineTip(PathMetadata metadata, PathInits inits) {
        this(RoutineTip.class, metadata, inits);
    }

    public QRoutineTip(Class<? extends RoutineTip> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.routine = inits.isInitialized("routine") ? new QRoutine(forProperty("routine"), inits.get("routine")) : null;
    }

}

