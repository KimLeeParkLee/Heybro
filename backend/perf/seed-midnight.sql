-- 자정 로그 생성 측정용: 회원 1만 명 × 루틴 3개, 루틴마다 오늘 요일 스케줄 1개 (+ 다른 요일 1개)
-- 변수 @dow 는 호출 측에서 SET (예: SET @dow='WEDNESDAY')
SET SESSION cte_max_recursion_depth = 200000;
INSERT INTO user (name, nickname, email, gender, bro_point, total_bro_point, bro_level, experience, privacy_consent, marketing_consent, notification_enabled, provider, profile_image)
WITH RECURSIVE seq AS (SELECT 1 n UNION ALL SELECT n + 1 FROM seq WHERE n < 10000)
SELECT CONCAT('자정', n), CONCAT('mid_', n), CONCAT('mid', n, '@heybro.local'), 'M', 0, 0, 1, 0, 1, 0, 0, 'local', 'https://example.com/p.jpg'
FROM seq;
INSERT INTO user_routine (user_id, routine_id)
SELECT u.id, r.id FROM user u JOIN routine r ON r.id IN (1, 2, 3) WHERE u.email LIKE 'mid%@heybro.local';
INSERT INTO user_routine_schedule (schedule_time, day_of_week, user_routine_id)
SELECT '08:00:00', @dow, ur.id FROM user_routine ur JOIN user u ON u.id = ur.user_id WHERE u.email LIKE 'mid%@heybro.local';
INSERT INTO user_routine_schedule (schedule_time, day_of_week, user_routine_id)
SELECT '21:00:00', @other, ur.id FROM user_routine ur JOIN user u ON u.id = ur.user_id WHERE u.email LIKE 'mid%@heybro.local';
SELECT (SELECT COUNT(*) FROM user_routine) AS user_routines, (SELECT COUNT(*) FROM user_routine_schedule) AS schedules;
