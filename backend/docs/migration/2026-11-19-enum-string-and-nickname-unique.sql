-- 2026-11-19 스키마 변경 안내 (ddl-auto: update 는 컬럼 타입을 바꾸지 않으므로 운영 DB 에 직접 적용 필요)
-- 1) enum 을 ordinal(정수) 에서 문자열로 저장하도록 변경한 컬럼
--    point_history.transaction_type : 0=EARN, 1=USE
--    coupon.coupon_type            : CouponType 상수 순서대로 (AMOUNT=0, PRODUCT=1)
--    coupon_purchase.gift_status   : 0=PENDING, 1=ACCEPTED, 2=EXPIRED
-- 2) user.nickname 유니크 제약 (중복 닉네임이 있으면 먼저 정리해야 인덱스가 생성됨)
-- 3) ordinal 시절 Hibernate가 만든 CHECK 제약 (예: transaction_type between 0 and 1) 제거
--    남겨두면 문자열 값 INSERT가 'Check constraint ... is violated'로 실패함
-- 반드시 백업 후 실행하고 결과를 확인할 것. (MySQL의 ALTER TABLE은 자동 커밋되므로 트랜잭션으로 되돌릴 수 없음)

-- 제약 이름 확인 (운영 DB에서 이름이 다르면 아래 DROP CHECK 이름을 맞출 것)
SELECT tc.TABLE_NAME, cc.CONSTRAINT_NAME, cc.CHECK_CLAUSE
FROM information_schema.CHECK_CONSTRAINTS cc
JOIN information_schema.TABLE_CONSTRAINTS tc USING (CONSTRAINT_SCHEMA, CONSTRAINT_NAME)
WHERE cc.CONSTRAINT_SCHEMA = DATABASE();

ALTER TABLE point_history DROP CHECK point_history_chk_1;
ALTER TABLE coupon DROP CHECK coupon_chk_1;
ALTER TABLE coupon_purchase DROP CHECK coupon_purchase_chk_1;

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
