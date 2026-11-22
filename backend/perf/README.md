# 성능 측정 (JMeter)

질문 상세 조회 N+1 개선과 포인트 사용 동시성 개선을 JMeter로 측정한 스크립트와 결과 요약.

## 구성

| 파일 | 용도 |
|---|---|
| `measure.sh <jar> <label>` | DB 초기화 → 앱 기동 → 회원 가입/시드 → 쿼리 수 집계 → JMeter 실행(1·8·50 threads) → 조회수 검증. 결과는 `results/<label>/`. jar는 절대 경로로. `HEAVY_THREADS`/`HEAVY_LOOPS`(기본 50/100)로 고부하 단계 조정, `HEAVY_THREADS=0`이면 생략 |
| `qna-detail.jmx` | `GET /api/questions/{id}` 부하 플랜 (`-Jthreads`, `-Jloops`, `-Jtoken`, `-JquestionId`) |
| `point-use.jmx` | `POST /api/points/use` 동시 요청 플랜 (Synchronizing Timer로 전원 동시 발사) |
| `seed-qna.sql` | 질문 1개 + 답변 10개(작성자 각각 다름) + 답변 이미지 20장 |
| `jtl-stats.py` | JTL(CSV) → 평균/백분위/처리량 |

측정 환경: MacBook(Apple Silicon) 로컬, MySQL 8 계열(brew), JDK 17, JMeter 5.6, 2026-11-19.
쿼리 수는 MySQL `general_log`로 요청 1건을 기록해 집계 (JWT 인증용 회원 조회 1건, 조회수 증가 UPDATE 1건 포함).

## 1. 질문 상세 조회 N+1 (답변 10개, 이미지 2장씩)

| 단계 | 쿼리 수/요청 | 1 thread × 300 평균 | 8 threads × 300 평균 | 처리량 (8 threads) |
|---|---|---|---|---|
| 원본 (지연 로딩만) | 28 | 5.9 ms | 6.3 ms | 924 req/s |
| Fetch Join (질문·작성자·질문 이미지, 답변·답변 작성자) | 16 | 4.6 ms | 5.2 ms | 1,060 req/s |
| + `@BatchSize(100)` (답변 이미지 IN 절 1회) | **7** | **3.8 ms** | **4.2 ms** | **1,234 req/s** |

남은 7개: 인증 회원 조회 1, 조회수 UPDATE 1, 질문+작성자+질문이미지 1, 답변+작성자 1, 태그 1, 답변 이미지(IN) 1, 카테고리 1.

## 2. 포인트 사용 동시성 (잔액 1,000 · 10P씩 · 120건 동시)

기대값: 100건 성공, 20건 잔액 부족(400), 최종 잔액 0.

| 구분 | 성공 | 400 | 500 | 최종 잔액 | 포인트 내역 수 | 평균 응답 |
|---|---|---|---|---|---|---|
| 개선 전 (락 없음) | 11 | 0 | 109 (Deadlock) | 940 | 11 | 103 ms |
| 개선 후 (`PESSIMISTIC_WRITE`) | **100** | **20** | **0** | **0** | **100** | 114 ms |

개선 전 원인
1. 갱신 손실: 락 없이 읽은 잔액으로 검증·차감 → 성공 11건인데 잔액은 60만 줄어듦.
2. 데드락: `point_history` INSERT가 FK 검사로 `user` 행에 공유 락(S)을 걸고, 이어지는 `user` UPDATE가 배타 락(X)을 기다리며 서로 대기.

개선: `UserRepository.findByEmailForUpdate` (`SELECT ... FOR UPDATE`)로 트랜잭션 시작 시 회원 행 배타 락을 먼저 확보.

## 3. 커넥션 풀 고갈 (50 threads × 100, HikariCP 풀 20)

원인: `getQuestion`이 `@Transactional(readOnly)` 안에서 조회수 증가를 `REQUIRES_NEW`로 호출해 요청당 커넥션 2개를 사용했다.
동시 요청이 풀 크기(20)를 넘으면 바깥 트랜잭션 20개가 커넥션을 전부 쥔 채 안쪽 트랜잭션이 커넥션을 기다려
`Connection is not available, request timed out after 30000ms`가 발생하고, 인증 필터의 회원 조회까지 막혀 403/500이 쏟아졌다.

개선: `getQuestion`에서 `@Transactional`을 제거하고, 조회수 증가(`QuestionViewService`, 일반 `@Transactional`)와
상세 조회·DTO 변환(`QuestionDetailService`, `readOnly`)을 각각 별도 빈의 트랜잭션 메서드로 분리해 순서대로 호출한다.
각 단계가 커넥션을 1개씩 쓰고 커밋 즉시 반납하므로 요청당 동시 점유 커넥션은 1개다 (자기 호출로 프록시가 우회되지 않도록 별도 빈으로 분리).
DTO 변환은 읽기 트랜잭션 안에서 끝나며, 지연 로딩 `categories`는 `new ArrayList<>(...)`로 복사해 직렬화가 OSIV에 의존하지 않게 했다.

| 구분 | 요청 수 | 성공 | 403 | 500 | 커넥션 타임아웃 로그 | 평균 | 중앙값 | p90 | 처리량 |
|---|---|---|---|---|---|---|---|---|---|
| 개선 전 (`results/pool-before`) | 3,683 (※) | 1,632 | 1,038 | 1,012 | 5,203건 | 13,478 ms | 58 ms | 30,061 ms | 3.7 req/s |
| 개선 후 (`results/pool-fix`) | **5,000** | **5,000** | **0** | **0** | **0건** | **21.1 ms** | 22 ms | 29 ms | **1,702 req/s** |

※ 개선 전 실행은 30초 타임아웃이 누적되어 약 16.5분 뒤 중단(3,683/5,000건 시점). 404 1건은 인증 실패 후 경로 처리.

개선 후 확인 사항 (`results/pool-fix/measure.log`)
- 요청당 쿼리 수 7개로 동일 (인증 회원 조회 1, 조회수 UPDATE 1, 질문 1, 답변 1, 카테고리 1, 태그 1, 답변 이미지 1).
- 1 thread × 300 평균 4.2 ms, 8 threads × 300 평균 4.6 ms, 1,159 req/s — 개선 전(3.8 / 4.2 ms)과 오차 범위 내.
- 조회수: 전체 요청 7,802건(curl 2 + JMeter 7,800) 후 `view_count` = 7,802 로 요청마다 1씩 반영.
- 포인트 동시 사용: 100 성공 / 20 잔액 부족 / 잔액 0 으로 변동 없음.

## 4. 루틴 완료 토글의 포인트 중복 적립 (`measure2.sh` B)

`PATCH /routines/{id}/complete`가 완료 상태와 상관없이 호출할 때마다 10P·경험치 10을 더했고 포인트 내역도 남기지 않았다.
`DailyRoutineLog.rewarded` 플래그를 두어 하루에 한 번만 지급하고 내역을 기록한다. 오늘 로그가 없으면 NPE(500) 대신 404.

| 구분 | PATCH 20회 호출 후 포인트 | 포인트 내역 |
|---|---|---|
| 개선 전 | 200 | 0건 |
| 개선 후 | **10** | **1건** |

## 5. 쿠폰 구매·선물의 포인트 미차감 (`measure2.sh` C)

구매·선물 API 가 포인트를 전혀 차감하지 않았고(`Coupon.price` 미사용), 트랜잭션도 없었다.
`findByEmailForUpdate`(비관적 락) → 잔액 검증 → 차감 → `PointHistory` → `CouponPurchase` 를 한 트랜잭션으로 묶고,
쿠폰 코드는 `Random` 6자리(약 1,200건부터 충돌 50%)에서 `SecureRandom` 10자리 + 중복 확인 재시도로 바꿨다.

| 구분 (잔액 1,000 · 10P 쿠폰 · 120건 동시) | 성공 | 400 | 최종 잔액 | 구매 내역 | 포인트 내역 |
|---|---|---|---|---|---|
| 개선 전 | 120 | 0 | 1,000 (차감 없음) | 120 | 0 |
| 개선 후 | **100** | **20** | **0** | **100** | **100** |

## 6. 랭킹 조회: 기준 불일치 + 전체 스캔 (`measure2.sh` D, 회원 10만 명)

TOP 10 은 누적 포인트(`DENSE_RANK`), 내 순위는 현재 잔액(`RANK`)으로 계산해 기준이 달랐고, 두 쿼리 모두 윈도 함수 때문에
`user` 전체를 정렬·임시 테이블에 올렸다(`results/after-items/explain-ranking.txt`).
누적 포인트로 통일하고 `idx_user_total_bro_point` 를 추가해, TOP 10 은 인덱스 역순 스캔 10건, 내 순위는
`COUNT(DISTINCT total_bro_point)+1 WHERE total_bro_point > 내 점수`(커버링 인덱스 범위 스캔)로 구한다.
보조 정렬을 `id DESC` 로 맞춰야 역순 인덱스 스캔이 되고, `id ASC` 면 다시 정렬이 생긴다(실행 계획으로 확인).

| 구분 (8 threads × 100) | 평균 | p95 | 처리량 | TOP 10 실행 계획 |
|---|---|---|---|---|
| 개선 전 | 527 ms | 868 ms | 14.9 req/s | Table scan 96,792행 → Sort → Window → Temporary |
| 개선 후 | **39 ms** | **49 ms** | **170 req/s** | Index scan (reverse) 10행 |

## 7. 자정 루틴 로그 스케줄러 (`measure2.sh` E, 회원 1만 명 × 루틴 3개)

전체 회원 엔티티를 올린 뒤 회원마다 `existsBy` + fetch join 조회를 하고 로그를 1건씩 INSERT 했으며, 전체가 트랜잭션 하나였다.
루틴이 있는 회원 id 만 가져와 500명 단위로 나누고, 묶음마다 별도 트랜잭션(REQUIRES_NEW)에서
`이미 생성된 회원 확인 1 + 스케줄 조회 1 + JDBC 배치 INSERT 1` 로 처리한다 (IDENTITY 전략이라 JPA 배치 대신 JdbcTemplate).
유니크 제약 충돌은 `ON DUPLICATE KEY UPDATE id = id` 로 그 행만 건너뛴다.

| 구분 | 소요 시간 | 쿼리 수 | 생성 로그 |
|---|---|---|---|
| 개선 전 | 1,426 s (23.8분, 단일 트랜잭션) | 50,030 | 30,000 |
| 개선 후 | **5.3 s** | **44** (묶음 20개 × 2 + 배치 INSERT 20 + 대상 조회 1 + 기타) | 30,000 |

## 8. 알림 스케줄러의 지연 로딩 예외 (`measure2.sh` F)

매분 실행되는 `NotificationScheduler`가 트랜잭션 없이 `schedule.getUserRoutine().getUser()` 를 호출해
실제로는 `LazyInitializationException` 이 나고 알림이 한 건도 나가지 않았다 (`results/base-items/app-notify.log`).
회원·루틴을 fetch join 하는 `findNotificationTargets` 로 바꾸고, `(day_of_week, schedule_time)` 인덱스를 추가했으며,
FCM 은 `sendEach` 로 500건씩 묶어 보낸다.

| 구분 (대상 3명) | LazyInitializationException | 조회 쿼리 | 발송 시도 |
|---|---|---|---|
| 개선 전 | 2회 (발송 0건) | 3 (스케줄 1 + 프록시 초기화 시도) | 0 |
| 개선 후 | **0회** | **1** (fetch join) | 3건 일괄 (로컬은 더미 키라 FCM 응답은 실패) |

## 9. 인증 (`measure2.sh` A)

- 로그아웃 시 Redis 에 넣던 블랙리스트를 아무도 읽지 않아 로그아웃한 토큰이 만료까지 유효했다 → 필터에서 확인해 401.
- access/refresh 토큰이 같은 형식이라 refresh 토큰으로도 API 호출이 됐다 → `typ` 클레임으로 구분, refresh 는 재발급에만 허용.
- 필터가 요청마다 회원을 DB 조회했다 → 서명 검증된 클레임으로 인증 객체를 만들어 조회 제거.

| 구분 | 질문 상세 조회 쿼리/요청 | 로그아웃한 토큰 | refresh 토큰으로 API 호출 |
|---|---|---|---|
| 개선 전 | 7 (회원 조회 1 포함) | 200 | 200 |
| 개선 후 | **6** | **401** | **401** |

## 10. 외부 API 호출과 트랜잭션

- Face++(`SkinAnalysisService`): 메서드 전체 `@Transactional` → 외부 호출은 밖에서, DB 저장만 `TransactionTemplate` 로.
- S3 업로드(`QnaServiceImpl.createQuestion/createAnswer`): 업로드를 먼저 끝내고 DB 저장만 트랜잭션으로. 저장 실패 시 업로드 파일 삭제(보상).
- `RestTemplate` 연결 3초/읽기 10초, Gemini `block(15초)` 타임아웃. (수치 측정은 하지 않음: 외부 API 모킹이 필요)

## 11. 포인트 금액 검증·예외 매핑

`POST /points/use` 에 음수를 보내면 잔액이 늘어나던 문제를 `@Positive` + 서비스 검증으로 막았다.
검증 실패(400), `EntityNotFoundException`(404), `IllegalStateException`/무결성 위반(409)을 매핑해 500 으로 뭉개지지 않게 했다.

## 측정 스크립트 (2차)

| 파일 | 용도 |
|---|---|
| `measure2.sh <jar> <label>` | 위 4~9 항목 자동 측정. `PARTS=ABCDEF` 로 부분 실행 |
| `seed-routine.sql`, `seed-coupon.sql`, `seed-ranking.sql`, `seed-midnight.sql`, `seed-notify.sql` | 각 항목 시드 |
| `explain-ranking.sh` | 랭킹 쿼리 실행 계획 전/후 비교 |
| `common/perf/PerfRunner` (perf 프로필) | 기동 직후 스케줄러를 1회 실행해 측정 (`--perf.task=midnight|notify`) |
