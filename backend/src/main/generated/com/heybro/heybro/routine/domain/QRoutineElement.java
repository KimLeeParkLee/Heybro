package com.heybro.heybro.routine.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRoutineElement is a Querydsl query type for RoutineElement
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoutineElement extends EntityPathBase<RoutineElement> {

    private static final long serialVersionUID = -2100353045L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRoutineElement routineElement = new QRoutineElement("routineElement");

    public final StringPath content = createString("content");

    public final StringPath detailImage = createString("detailImage");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final QRoutine routine;

    public final NumberPath<Integer> step = createNumber("step", Integer.class);

    public QRoutineElement(String variable) {
        this(RoutineElement.class, forVariable(variable), INITS);
    }

    public QRoutineElement(Path<? extends RoutineElement> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRoutineElement(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRoutineElement(PathMetadata metadata, PathInits inits) {
        this(RoutineElement.class, metadata, inits);
    }

    public QRoutineElement(Class<? extends RoutineElement> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.routine = inits.isInitialized("routine") ? new QRoutine(forProperty("routine"), inits.get("routine")) : null;
    }

}

