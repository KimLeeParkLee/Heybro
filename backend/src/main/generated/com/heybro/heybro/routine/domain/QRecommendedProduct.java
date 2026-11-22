package com.heybro.heybro.routine.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecommendedProduct is a Querydsl query type for RecommendedProduct
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecommendedProduct extends EntityPathBase<RecommendedProduct> {

    private static final long serialVersionUID = -908210457L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRecommendedProduct recommendedProduct = new QRecommendedProduct("recommendedProduct");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image = createString("image");

    public final StringPath link = createString("link");

    public final StringPath name = createString("name");

    public final QRoutine routine;

    public QRecommendedProduct(String variable) {
        this(RecommendedProduct.class, forVariable(variable), INITS);
    }

    public QRecommendedProduct(Path<? extends RecommendedProduct> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QRecommendedProduct(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QRecommendedProduct(PathMetadata metadata, PathInits inits) {
        this(RecommendedProduct.class, metadata, inits);
    }

    public QRecommendedProduct(Class<? extends RecommendedProduct> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.routine = inits.isInitialized("routine") ? new QRoutine(forProperty("routine"), inits.get("routine")) : null;
    }

}

