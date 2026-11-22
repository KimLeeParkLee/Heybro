-- 2026-11-19 스키마 변경 안내 (ddl-auto: update 는 컬럼 타입을 바꾸지 않으므로 운영 DB 에 직접 적용 필요)
-- 1) enum 을 ordinal(정수) 에서 문자열로 저장하도록 변경한 컬럼
--    point_history.transaction_type : 0=EARN, 1=USE
--    coupon.coupon_type            : CouponType 상수 순서대로 (AMOUNT=0, PRODUCT=1)
--    coupon_purchase.gift_status   : 0=PENDING, 1=ACCEPTED, 2=EXPIRED
-- 2) user.nickname 유니크 제약 (중복 닉네임이 있으면 먼저 정리해야 인덱스가 생성됨)
-- 반드시 백업 후, 트랜잭션 안에서 실행하고 결과를 확인할 것.

START TRANSACTION;

ALTER TABLE point_history MODIFY transaction_type VARCHAR(255);
UPDATE point_history SET transaction_type = CASE transaction_type WHEN '0' THEN 'EARN' WHEN '1' THEN 'USE' ELSE transaction_type END;

ALTER TABLE coupon MODIFY coupon_type VARCHAR(255);
UPDATE coupon SET coupon_type = CASE coupon_type WHEN '0' THEN 'AMOUNT' WHEN '1' THEN 'PRODUCT' ELSE coupon_type END;

ALTER TABLE coupon_purchase MODIFY gift_status VARCHAR(255);
UPDATE coupon_purchase SET gift_status = CASE gift_status WHEN '0' THEN 'PENDING' WHEN '1' THEN 'ACCEPTED' WHEN '2' THEN 'EXPIRED' ELSE gift_status END;

-- 중복 닉네임 확인 (0건이어야 함)
SELECT nickname, COUNT(*) FROM user GROUP BY nickname HAVING COUNT(*) > 1;
-- 중복이 없으면
ALTER TABLE user ADD CONSTRAINT uk_user_nickname UNIQUE (nickname);

-- 탈퇴 익명화 컬럼 (ddl-auto 가 추가하지만 명시)
-- ALTER TABLE user ADD COLUMN withdrawn_at DATETIME(6) NULL;

COMMIT;
