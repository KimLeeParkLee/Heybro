package com.heybro.heybro.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserRoutineSchedule is a Querydsl query type for UserRoutineSchedule
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserRoutineSchedule extends EntityPathBase<UserRoutineSchedule> {

    private static final long serialVersionUID = 1407127708L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserRoutineSchedule userRoutineSchedule = new QUserRoutineSchedule("userRoutineSchedule");

    public final EnumPath<java.time.DayOfWeek> dayOfWeek = createEnum("dayOfWeek", java.time.DayOfWeek.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final TimePath<java.time.LocalTime> scheduleTime = createTime("scheduleTime", java.time.LocalTime.class);

    public final QUserRoutine userRoutine;

    public QUserRoutineSchedule(String variable) {
        this(UserRoutineSchedule.class, forVariable(variable), INITS);
    }

    public QUserRoutineSchedule(Path<? extends UserRoutineSchedule> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserRoutineSchedule(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserRoutineSchedule(PathMetadata metadata, PathInits inits) {
        this(UserRoutineSchedule.class, metadata, inits);
    }

    public QUserRoutineSchedule(Class<? extends UserRoutineSchedule> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.userRoutine = inits.isInitialized("userRoutine") ? new QUserRoutine(forProperty("userRoutine"), inits.get("userRoutine")) : null;
    }

}

