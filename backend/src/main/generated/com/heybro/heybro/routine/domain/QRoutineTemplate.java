package com.heybro.heybro.routine.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRoutineTemplate is a Querydsl query type for RoutineTemplate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoutineTemplate extends EntityPathBase<RoutineTemplate> {

    private static final long serialVersionUID = -1999642261L;

    public static final QRoutineTemplate routineTemplate = new QRoutineTemplate("routineTemplate");

    public final ListPath<Routine, QRoutine> elementList = this.<Routine, QRoutine>createList("elementList", Routine.class, QRoutine.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.heybro.heybro.user.domain.UserType> type = createEnum("type", com.heybro.heybro.user.domain.UserType.class);

    public QRoutineTemplate(String variable) {
        super(RoutineTemplate.class, forVariable(variable));
    }

    public QRoutineTemplate(Path<? extends RoutineTemplate> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoutineTemplate(PathMetadata metadata) {
        super(RoutineTemplate.class, metadata);
    }

}

