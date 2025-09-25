package com.heybro.heybro.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserRoutine is a Querydsl query type for UserRoutine
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserRoutine extends EntityPathBase<UserRoutine> {

    private static final long serialVersionUID = 454348517L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserRoutine userRoutine = new QUserRoutine("userRoutine");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.heybro.heybro.routine.domain.QRoutine routine;

    public final ListPath<UserRoutineSchedule, QUserRoutineSchedule> schedules = this.<UserRoutineSchedule, QUserRoutineSchedule>createList("schedules", UserRoutineSchedule.class, QUserRoutineSchedule.class, PathInits.DIRECT2);

    public final QUser user;

    public QUserRoutine(String variable) {
        this(UserRoutine.class, forVariable(variable), INITS);
    }

    public QUserRoutine(Path<? extends UserRoutine> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserRoutine(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserRoutine(PathMetadata metadata, PathInits inits) {
        this(UserRoutine.class, metadata, inits);
    }

    public QUserRoutine(Class<? extends UserRoutine> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.routine = inits.isInitialized("routine") ? new com.heybro.heybro.routine.domain.QRoutine(forProperty("routine"), inits.get("routine")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

