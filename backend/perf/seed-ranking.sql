-- 랭킹 측정용: 회원 10만 명 (누적 포인트는 0~99,999 분포)
SET SESSION cte_max_recursion_depth = 200000;
INSERT INTO user (name, nickname, email, gender, bro_point, total_bro_point, bro_level, experience, privacy_consent, marketing_consent, notification_enabled, provider, profile_image)
WITH RECURSIVE seq AS (SELECT 1 n UNION ALL SELECT n + 1 FROM seq WHERE n < 100000)
SELECT CONCAT('회원', n), CONCAT('bro_', n), CONCAT('rank', n, '@heybro.local'), 'M',
       (n * 7919) % 100000, (n * 7919) % 100000, 1, 0, 1, 0, 0, 'local', 'https://example.com/p.jpg'
FROM seq;
UPDATE user SET bro_point = 54321, total_bro_point = 54321 WHERE email = 'perf@heybro.local';
SELECT COUNT(*) AS users FROM user;
