# 성능 측정 (JMeter)

질문 상세 조회 N+1 개선과 포인트 사용 동시성 개선을 JMeter로 측정한 스크립트와 결과 요약.

## 구성

| 파일 | 용도 |
|---|---|
| `measure.sh <jar> <label>` | DB 초기화 → 앱 기동 → 회원 가입/시드 → 쿼리 수 집계 → JMeter 실행. 결과는 `results/<label>/` |
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

## 참고: 50 threads 부하 시 커넥션 풀 고갈

`getQuestion`이 `@Transactional(readOnly)` 안에서 조회수 증가를 `REQUIRES_NEW`로 호출해 요청당 커넥션 2개를 사용한다.
HikariCP 풀(20)보다 동시 요청이 많으면 바깥 트랜잭션들이 커넥션을 모두 점유한 채 안쪽 트랜잭션이 커넥션을 기다려
`Connection is not available, request timed out after 30000ms` 오류가 발생했다. 이번 측정은 8 threads로 제한했으며, 별도 개선 대상.
