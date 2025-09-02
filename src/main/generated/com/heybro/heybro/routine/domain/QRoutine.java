package com.heybro.heybro.routine.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRoutine is a Querydsl query type for Routine
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRoutine extends EntityPathBase<Routine> {

    private static final long serialVersionUID = 159191505L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRoutine routine = new QRoutine("routine");

    public final ListPath<RoutineElement, QRoutineElement> elementList = this.<RoutineElement, QRoutineElement>createList("elementList", RoutineElement.class, QRoutineElement.class, PathInits.DIRECT2);

    public final StringPath iconImage = createString("iconImage");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final ListPath<RecommendedProduct, QRecommendedProduct> recommendedProductList = this.<RecommendedProduct, QRecommendedProduct>createList("recommendedProductList", RecommendedProduct.class, QRecommendedProduct.class, PathInits.DIRECT2);

    public final QRoutineTemplate routineTemplate;

    public final EnumPath<TimeOfDay> timeOfDay = createEnum("timeOfDay", TimeOfDay.class);

    public final ListPath<RoutineTip, QRoutineTip> tipList = this.<RoutineTip, QRoutineTip>createList("tipList", RoutineTip.class, QRoutineTip.class, PathInits.DIRECT2);

    public QRoutine(String variable) {
        this(Routine.class, forVariable(variable), INITS);
    }

    public QRoutine(Path<? extends Routine> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRoutine(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRoutine(PathMetadata metadata, PathInits inits) {
        this(Routine.class, metadata, inits);
    }

    public QRoutine(Class<? extends Routine> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.routineTemplate = inits.isInitialized("routineTemplate") ? new QRoutineTemplate(forProperty("routineTemplate")) : null;
    }

}

