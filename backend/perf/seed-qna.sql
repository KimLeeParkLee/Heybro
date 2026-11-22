-- 질문 상세 조회 N+1 측정용 시드 데이터
-- 사용법: mysql -uheybro -pheybro1234 heybro < perf/seed-qna.sql
-- 전제: perf 측정용 회원(perf@heybro.local)이 이미 가입되어 있어야 함
SET @uid = (SELECT id FROM user WHERE email = 'perf@heybro.local');

INSERT INTO question (title, content, created_at, view_count, thumbnail, user_id)
VALUES ('면도 후 피부 트러블 어떻게 관리하나요?', '면도만 하면 턱 부분이 붉어지고 따가워요. 좋은 방법 있을까요?', NOW(), 0, 'https://example.com/q1.jpg', @uid);
SET @qid = LAST_INSERT_ID();

INSERT INTO question_category (question_id, category_name) VALUES (@qid, 'SHAVING_HAIRCARE'), (@qid, 'SKIN_CONCERN');
INSERT INTO tag (question_id, content) VALUES (@qid, '면도'), (@qid, '피부트러블');
INSERT INTO question_image (question_image, sort_order, question_id)
VALUES ('https://example.com/q1-1.jpg', 1, @qid), ('https://example.com/q1-2.jpg', 2, @qid);

-- 답변 10개, 답변마다 이미지 2장 (답변 작성자는 각각 다른 회원)
INSERT INTO user (name, nickname, email, gender, bro_point, total_bro_point, bro_level, experience, privacy_consent, marketing_consent, notification_enabled, provider, profile_image)
SELECT CONCAT('답변자', n), CONCAT('bro_answer_', n), CONCAT('answer', n, '@heybro.local'), 'M', 0, 0, 1, 0, 1, 0, 0, 'local', 'https://example.com/p.jpg'
FROM (SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t;

INSERT INTO answer (content, created_at, user_id, question_id)
SELECT CONCAT('답변 ', n, ': 면도 전 따뜻한 수건으로 모공을 열고, 면도 후엔 알로에 젤로 진정시켜 보세요.'), NOW(), (SELECT id FROM user WHERE email = CONCAT('answer', n, '@heybro.local')), @qid
FROM (SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t;

INSERT INTO answer_image (answer_image, sort_order, answer_id)
SELECT CONCAT('https://example.com/a', a.id, '-', s.n, '.jpg'), s.n, a.id
FROM answer a JOIN (SELECT 1 n UNION SELECT 2) s
WHERE a.question_id = @qid;

SELECT @qid AS question_id;
