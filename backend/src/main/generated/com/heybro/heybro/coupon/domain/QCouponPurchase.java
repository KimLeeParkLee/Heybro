package com.heybro.heybro.coupon.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCouponPurchase is a Querydsl query type for CouponPurchase
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCouponPurchase extends EntityPathBase<CouponPurchase> {

    private static final long serialVersionUID = -1575352778L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCouponPurchase couponPurchase = new QCouponPurchase("couponPurchase");

    public final QCoupon coupon;

    public final StringPath giftCode = createString("giftCode");

    public final EnumPath<GiftStatus> giftStatus = createEnum("giftStatus", GiftStatus.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isUsed = createBoolean("isUsed");

    public final DatePath<java.time.LocalDate> purchaseDate = createDate("purchaseDate", java.time.LocalDate.class);

    public final com.heybro.heybro.user.domain.QUser sender;

    public final DatePath<java.time.LocalDate> usedAt = createDate("usedAt", java.time.LocalDate.class);

    public final com.heybro.heybro.user.domain.QUser user;

    public QCouponPurchase(String variable) {
        this(CouponPurchase.class, forVariable(variable), INITS);
    }

    public QCouponPurchase(Path<? extends CouponPurchase> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCouponPurchase(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCouponPurchase(PathMetadata metadata, PathInits inits) {
        this(CouponPurchase.class, metadata, inits);
    }

    public QCouponPurchase(Class<? extends CouponPurchase> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.coupon = inits.isInitialized("coupon") ? new QCoupon(forProperty("coupon")) : null;
        this.sender = inits.isInitialized("sender") ? new com.heybro.heybro.user.domain.QUser(forProperty("sender")) : null;
        this.user = inits.isInitialized("user") ? new com.heybro.heybro.user.domain.QUser(forProperty("user")) : null;
    }

}

