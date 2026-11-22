package com.heybro.heybro.skin.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSkinDiagnosis is a Querydsl query type for SkinDiagnosis
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSkinDiagnosis extends EntityPathBase<SkinDiagnosis> {

    private static final long serialVersionUID = 1951354094L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSkinDiagnosis skinDiagnosis = new QSkinDiagnosis("skinDiagnosis");

    public final NumberPath<Integer> acneSocre = createNumber("acneSocre", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> diagnosisDate = createDateTime("diagnosisDate", java.time.LocalDateTime.class);

    public final NumberPath<Integer> finalScore = createNumber("finalScore", Integer.class);

    public final NumberPath<Integer> hydrationScore = createNumber("hydrationScore", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> oilinessScore = createNumber("oilinessScore", Integer.class);

    public final NumberPath<Integer> poreScore = createNumber("poreScore", Integer.class);

    public final StringPath skinType = createString("skinType");

    public final com.heybro.heybro.user.domain.QUser user;

    public QSkinDiagnosis(String variable) {
        this(SkinDiagnosis.class, forVariable(variable), INITS);
    }

    public QSkinDiagnosis(Path<? extends SkinDiagnosis> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSkinDiagnosis(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSkinDiagnosis(PathMetadata metadata, PathInits inits) {
        this(SkinDiagnosis.class, metadata, inits);
    }

    public QSkinDiagnosis(Class<? extends SkinDiagnosis> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.heybro.heybro.user.domain.QUser(forProperty("user")) : null;
    }

}

