-- 루틴 토글 측정용: 측정 회원에게 루틴 1개 + 오늘 요일 스케줄
SET @uid = (SELECT id FROM user WHERE email = 'perf@heybro.local');
INSERT INTO user_routine (user_id, routine_id) VALUES (@uid, 1);
INSERT INTO user_routine_schedule (schedule_time, day_of_week, user_routine_id) VALUES ('08:00:00', @dow, LAST_INSERT_ID());
