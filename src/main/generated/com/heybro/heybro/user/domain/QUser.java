package com.heybro.heybro.user.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = 150552799L;

    public static final QUser user = new QUser("user");

    public final TimePath<java.time.LocalTime> bedtime = createTime("bedtime", java.time.LocalTime.class);

    public final DatePath<java.time.LocalDate> birthDate = createDate("birthDate", java.time.LocalDate.class);

    public final NumberPath<Integer> broLevel = createNumber("broLevel", Integer.class);

    public final NumberPath<Integer> broPoint = createNumber("broPoint", Integer.class);

    public final StringPath email = createString("email");

    public final NumberPath<Integer> experience = createNumber("experience", Integer.class);

    public final StringPath gender = createString("gender");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath marketingConsent = createBoolean("marketingConsent");

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final BooleanPath notificationEnabled = createBoolean("notificationEnabled");

    public final StringPath notificationToken = createString("notificationToken");

    public final StringPath password = createString("password");

    public final StringPath phone = createString("phone");

    public final BooleanPath privacyConsent = createBoolean("privacyConsent");

    public final StringPath profileImage = createString("profileImage");

    public final StringPath provider = createString("provider");

    public final StringPath providerId = createString("providerId");

    public final NumberPath<Long> totalBroPoint = createNumber("totalBroPoint", Long.class);

    public final EnumPath<UserType> userType = createEnum("userType", UserType.class);

    public final TimePath<java.time.LocalTime> wakeupTime = createTime("wakeupTime", java.time.LocalTime.class);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

