package com.heybro.heybro.routine.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QDailyRoutineLog is a Querydsl query type for DailyRoutineLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDailyRoutineLog extends EntityPathBase<DailyRoutineLog> {

    private static final long serialVersionUID = -1333926170L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QDailyRoutineLog dailyRoutineLog = new QDailyRoutineLog("dailyRoutineLog");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isCompleted = createBoolean("isCompleted");

    public final QRoutine routine;

    public final TimePath<java.time.LocalTime> scheduledTime = createTime("scheduledTime", java.time.LocalTime.class);

    public final DatePath<java.time.LocalDate> taskDate = createDate("taskDate", java.time.LocalDate.class);

    public final com.heybro.heybro.user.domain.QUser user;

    public QDailyRoutineLog(String variable) {
        this(DailyRoutineLog.class, forVariable(variable), INITS);
    }

    public QDailyRoutineLog(Path<? extends DailyRoutineLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QDailyRoutineLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QDailyRoutineLog(PathMetadata metadata, PathInits inits) {
        this(DailyRoutineLog.class, metadata, inits);
    }

    public QDailyRoutineLog(Class<? extends DailyRoutineLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.routine = inits.isInitialized("routine") ? new QRoutine(forProperty("routine"), inits.get("routine")) : null;
        this.user = inits.isInitialized("user") ? new com.heybro.heybro.user.domain.QUser(forProperty("user")) : null;
    }

}

