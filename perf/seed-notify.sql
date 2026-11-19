-- 알림 스케줄러 측정용: 토큰이 있는 회원 3명, 각각 루틴 1개, 지금 분 + 다음 2분 스케줄
-- 변수 @dow, @t0, @t1, @t2 는 호출 측에서 SET
INSERT INTO user (name, nickname, email, gender, bro_point, total_bro_point, bro_level, experience, privacy_consent, marketing_consent, notification_enabled, notification_token, provider, profile_image)
VALUES ('알림1', 'noti_1', 'noti1@heybro.local', 'M', 0, 0, 1, 0, 1, 0, 1, 'dummy-fcm-token-1', 'local', 'https://example.com/p.jpg'),
       ('알림2', 'noti_2', 'noti2@heybro.local', 'M', 0, 0, 1, 0, 1, 0, 1, 'dummy-fcm-token-2', 'local', 'https://example.com/p.jpg'),
       ('알림3', 'noti_3', 'noti3@heybro.local', 'M', 0, 0, 1, 0, 1, 0, 1, 'dummy-fcm-token-3', 'local', 'https://example.com/p.jpg');
INSERT INTO user_routine (user_id, routine_id)
SELECT u.id, 1 FROM user u WHERE u.email LIKE 'noti%@heybro.local';
INSERT INTO user_routine_schedule (schedule_time, day_of_week, user_routine_id)
SELECT t.tm, @dow, ur.id FROM user_routine ur JOIN user u ON u.id = ur.user_id
JOIN (SELECT @t0 tm UNION SELECT @t1 UNION SELECT @t2) t
WHERE u.email LIKE 'noti%@heybro.local';
SELECT COUNT(*) AS schedules FROM user_routine_schedule;
