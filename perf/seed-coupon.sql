-- 쿠폰 동시 구매 측정용: 10포인트짜리 쿠폰 1개
INSERT INTO coupon (name, coupon_type, price, detail_image) VALUES ('아메리카노 교환권', 'PRODUCT', 10, 'https://example.com/c.jpg');
SELECT LAST_INSERT_ID() AS coupon_id;
