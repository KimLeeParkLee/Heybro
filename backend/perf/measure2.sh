#!/usr/bin/env bash
# 사용법: perf/measure2.sh <app.jar> <label>
# 루틴 토글 포인트, 쿠폰 동시 구매, 랭킹(10만 명), 인증 쿼리 수, 자정 스케줄러(1만 명), 알림 스케줄러를 측정한다.
# 결과는 perf/results/<label>/ 에 저장된다.
set -uo pipefail
JAR=$1; LABEL=$2
DIR=$(cd "$(dirname "$0")" && pwd)
OUT=$DIR/results/$LABEL; mkdir -p "$OUT"; cd "$OUT"
JAVA=${JAVA_HOME17:-/Users/u/.jdks/jdk-17.0.21.1+2/Contents/Home}/bin/java
BASE=http://localhost:8080/api
MY="mysql -uheybro -pheybro1234 -N -s heybro"
ROOT="mysql -uroot -N -s"
DOW=$(LC_ALL=C date +%A | tr a-z A-Z)
OTHER=$(LC_ALL=C date -v+1d +%A | tr a-z A-Z)
APP_PID=""
PARTS=${PARTS:-ABCDEF}

log() { echo "[$(date +%H:%M:%S)] $*" | tee -a "$OUT/measure.log"; }

reset_db() {
  mysql -uroot -e "DROP DATABASE IF EXISTS heybro; CREATE DATABASE heybro CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
  mysql -uroot -e "SET GLOBAL general_log='OFF'; SET GLOBAL log_output='TABLE'; TRUNCATE mysql.general_log;"
}

start_app() { # $1 = 로그 파일 이름, 나머지 = 추가 인자
  local name=$1; shift
  "$JAVA" -Duser.timezone=Asia/Seoul -jar "$JAR" --spring.profiles.active=dev --spring.jpa.open-in-view=true "$@" > "$OUT/$name" 2>&1 &
  APP_PID=$!
  for i in $(seq 1 180); do
    code=$(curl -s -o /dev/null -w '%{http_code}' "$BASE/users?email=x@x.x" || true)
    [ "$code" != "000" ] && break; sleep 1
  done
  # 루틴 초기 데이터(CommandLineRunner) 적재가 끝날 때까지 대기 (routine 테이블 수가 0 이 아니고 2초간 변하지 않을 때)
  local prev=-1 cur=0
  for i in $(seq 1 120); do
    cur=$($MY -e "SELECT COUNT(*) FROM routine" 2>/dev/null || echo 0)
    [ "$cur" != "0" ] && [ "$cur" = "$prev" ] && break
    prev=$cur; sleep 2
  done
  log "앱 기동 ($name): 응답 $code, pid $APP_PID, 루틴 $cur개"
}

stop_app() { kill $APP_PID 2>/dev/null; wait $APP_PID 2>/dev/null; APP_PID=""; }

register() {
  REG=$(curl -s -X POST "$BASE/users" -H 'Content-Type: application/json' -d '{"user_name":"측정용","nickname":"perf_bro","email":"perf@heybro.local","password":"perf1234!","gender":"M","birth_date":"1995-01-01","phone":"010-0000-0000","privacy_consent":true,"marketing_consent":false,"notification_enabled":false,"provider":"local"}')
  TOKEN=$(printf '%s' "$REG" | python3 -c "import sys,json;d=json.load(sys.stdin);d=d.get('data',d);print(d['access_token'].replace('Bearer ','',1))")
  [ -z "$TOKEN" ] && { log "토큰 발급 실패: $REG"; stop_app; exit 1; }
}

count_queries() { # general_log 에서 SQL 문 수 (select/insert/update/delete)
  $ROOT -e "SELECT COUNT(*) FROM mysql.general_log WHERE command_type='Query' AND (argument LIKE 'select%' OR argument LIKE 'insert%' OR argument LIKE 'update%' OR argument LIKE 'delete%') AND argument NOT LIKE 'select @@%' AND argument NOT LIKE '%mysql.general_log%'"
}
glog_on() { mysql -uroot -e "TRUNCATE mysql.general_log; SET GLOBAL general_log='ON';"; }
glog_off() { mysql -uroot -e "SET GLOBAL general_log='OFF';"; }

# ================= 1부: 일반 기동 (API 측정) =================
if [[ "$PARTS" == *[ABCD]* ]]; then
reset_db
start_app app.log
register
log "오늘 요일: $DOW"

# --- A. 인증: 질문 상세 조회 1회 쿼리 수 (JWT 필터 DB 조회 유무) ---
QID=$($MY < "$DIR/seed-qna.sql" | tail -1)
curl -s -o /dev/null -H "Authorization: Bearer $TOKEN" "$BASE/questions/$QID"
glog_on; curl -s -o /dev/null -H "Authorization: Bearer $TOKEN" "$BASE/questions/$QID"; glog_off
$ROOT -e "SELECT argument FROM mysql.general_log WHERE command_type='Query' AND argument LIKE 'select%' ORDER BY event_time" | cut -c1-120 > "$OUT/auth-queries.txt"
log "A. 질문 상세 조회 1회 쿼리 수: $(count_queries) (JWT 필터 회원 조회: $($ROOT -e "SELECT COUNT(*) FROM mysql.general_log WHERE command_type='Query' AND argument LIKE 'select%from user u1_0 where u1_0.email=%'")회)"

# --- A2. 로그아웃 후 같은 토큰 재사용 ---
curl -s -o /dev/null -X POST -H "Authorization: Bearer $TOKEN" "$BASE/auth/logout"
code=$(curl -s -o /dev/null -w '%{http_code}' -H "Authorization: Bearer $TOKEN" "$BASE/questions/$QID")
log "A2. 로그아웃한 토큰으로 조회 시 응답 코드: $code (기대 401)"
# 다시 로그인해서 새 토큰
LOGIN=$(curl -s -X POST "$BASE/auth/login" -H 'Content-Type: application/json' -d '{"email":"perf@heybro.local","password":"perf1234!"}')
TOKEN=$(printf '%s' "$LOGIN" | python3 -c "import sys,json;d=json.load(sys.stdin);d=d.get('data',d);print(d['access_token'].replace('Bearer ','',1))")
REFRESH=$(printf '%s' "$LOGIN" | python3 -c "import sys,json;d=json.load(sys.stdin);d=d.get('data',d);print(d['refresh_token'])")
code=$(curl -s -o /dev/null -w '%{http_code}' -H "Authorization: Bearer $REFRESH" "$BASE/questions/$QID")
log "A3. refresh 토큰을 Authorization 에 넣어 조회 시 응답 코드: $code (기대 401)"

# --- B. 루틴 완료 토글 반복 호출 시 포인트 ---
{ echo "SET @dow='$DOW';"; cat "$DIR/seed-routine.sql"; } | $MY
curl -s -o /dev/null -H "Authorization: Bearer $TOKEN" "$BASE/routines/routine-logs?date=$(date +%F)"   # 오늘 로그 즉시 생성
$MY -e "UPDATE user SET bro_point=0, total_bro_point=0, experience=0, bro_level=1 WHERE email='perf@heybro.local'"
for i in $(seq 1 20); do curl -s -o /dev/null -X PATCH -H "Authorization: Bearer $TOKEN" "$BASE/routines/1/complete"; done
log "B. 루틴 완료 PATCH 20회 호출 후: 포인트=$($MY -e "SELECT bro_point FROM user WHERE email='perf@heybro.local'"), 포인트 내역=$($MY -e "SELECT COUNT(*) FROM point_history")건, 완료 상태=$($MY -e "SELECT is_completed FROM daily_routine_log WHERE user_id=(SELECT id FROM user WHERE email='perf@heybro.local') LIMIT 1") (기대: 10P, 1건)"

# --- C. 쿠폰 동시 구매 (잔액 1,000 · 10P 쿠폰 · 120건 동시) ---
CID=$($MY < "$DIR/seed-coupon.sql" | tail -1)
$MY -e "UPDATE user SET bro_point=1000, total_bro_point=1000 WHERE email='perf@heybro.local'; DELETE FROM point_history;"
sed "s|<intProp name=\"groupSize\">[0-9]*</intProp>|<intProp name=\"groupSize\">120</intProp>|" "$DIR/point-use.jmx" > "$OUT/coupon-buy.jmx"
jmeter -n -t "$OUT/coupon-buy.jmx" -Jtoken="$TOKEN" -Jthreads=120 -Jpath="/api/coupons/$CID/purchase" -l "$OUT/coupon-buy.jtl" > "$OUT/coupon-buy.out" 2>&1
log "C. 쿠폰 동시 구매 120건: 최종 잔액=$($MY -e "SELECT bro_point FROM user WHERE email='perf@heybro.local'"), 구매 내역=$($MY -e "SELECT COUNT(*) FROM coupon_purchase")건, 포인트 내역=$($MY -e "SELECT COUNT(*) FROM point_history")건 (기대: 잔액 0, 구매 100건)"
log "C. JMeter: $(python3 "$DIR/jtl-stats.py" "$OUT/coupon-buy.jtl" | tr '\n' ' ')"
log "C. 오류 원인: $(grep -o "Caused by: [A-Za-z.]*Exception: [^\[]\{0,60\}" "$OUT/app.log" | sort | uniq -c | sort -rn | head -2 | tr '\n' ';')"

# --- D. 랭킹 (회원 10만 명) ---
$MY < "$DIR/seed-ranking.sql" > /dev/null
$MY -e "ANALYZE TABLE user" > /dev/null
$MY -e "SHOW INDEX FROM user" | awk '{print $3, $5}' | sort -u > "$OUT/user-indexes.txt"
mysql -uheybro -pheybro1234 heybro -e "EXPLAIN SELECT u.nickname, u.total_bro_point, u.profile_image, DENSE_RANK() OVER (ORDER BY u.total_bro_point DESC) r FROM user u ORDER BY u.total_bro_point DESC LIMIT 10\G" 2>/dev/null > "$OUT/explain-top10-old.txt"
mysql -uheybro -pheybro1234 heybro -e "EXPLAIN SELECT u.nickname, u.total_bro_point, u.profile_image FROM user u ORDER BY u.total_bro_point DESC, u.id DESC LIMIT 10\G" 2>/dev/null > "$OUT/explain-top10-new.txt"
mysql -uheybro -pheybro1234 heybro -e "EXPLAIN SELECT COUNT(DISTINCT u.total_bro_point)+1 FROM user u WHERE u.total_bro_point > (SELECT me.total_bro_point FROM user me WHERE me.email='perf@heybro.local')\G" 2>/dev/null > "$OUT/explain-myrank-new.txt"
RANK_JSON=$(curl -s -H "Authorization: Bearer $TOKEN" "$BASE/users/ranking")
log "D. 랭킹 응답(내 순위): $(printf '%s' "$RANK_JSON" | python3 -c "import sys,json;d=json.load(sys.stdin)['data'];print('rank',d['my_ranking']['rank'],'top1',d['rankings'][0]['rank'],d['rankings'][0]['total_bro_point'],'top10 ranks',[r['rank'] for r in d['rankings']])")"
sed "s|/api/questions/\${__P(questionId,1)}|/api/users/ranking|" "$DIR/qna-detail.jmx" > "$OUT/ranking.jmx"
jmeter -n -t "$OUT/ranking.jmx" -Jtoken="$TOKEN" -Jthreads=1 -Jloops=30 -l "$OUT/ranking-warmup.jtl" > /dev/null 2>&1
jmeter -n -t "$OUT/ranking.jmx" -Jtoken="$TOKEN" -Jthreads=8 -Jloops=100 -Jrampup=1 -l "$OUT/ranking.jtl" > "$OUT/ranking.out" 2>&1
log "D. 랭킹 JMeter (8 threads x 100, 회원 10만): $(python3 "$DIR/jtl-stats.py" "$OUT/ranking.jtl" | tr '\n' ' ')"
log "D. 인덱스: $(tr '\n' ' ' < "$OUT/user-indexes.txt")"
stop_app
fi

# ================= 2부: 자정 스케줄러 (perf 프로필로 기동 시 1회 실행) =================
if [[ "$PARTS" == *E* ]]; then
reset_db
start_app app-midnight-seed.log    # 테이블·루틴 초기 데이터 생성
stop_app
{ echo "SET @dow='$DOW'; SET @other='$OTHER';"; cat "$DIR/seed-midnight.sql"; } | $MY | tail -1 | tee -a "$OUT/measure.log"
glog_on
start_app app-midnight.log --spring.profiles.active=dev,perf --perf.task=midnight
for i in $(seq 1 2400); do grep -q "PERF done task=midnight" "$OUT/app-midnight.log" && break; sleep 1; done
glog_off
stop_app
ELAPSED=$(grep -o "PERF done task=midnight elapsedMs=[0-9]*" "$OUT/app-midnight.log" | grep -o "[0-9]*$")
T0=$(grep "PERF start task=midnight" "$OUT/app-midnight.log" | head -1 | cut -c1-23 | tr 'T' ' ')
T1=$(grep "PERF done task=midnight" "$OUT/app-midnight.log" | head -1 | cut -c1-23 | tr 'T' ' ')
MQ=$($ROOT -e "SELECT COUNT(*) FROM mysql.general_log WHERE command_type='Query' AND event_time BETWEEN '$T0' AND '$T1' AND (argument LIKE 'select%' OR argument LIKE 'insert%' OR argument LIKE 'update%' OR argument LIKE 'delete%')")
LOGS=$($MY -e "SELECT COUNT(*) FROM daily_routine_log")
log "E. 자정 루틴 로그 생성 (회원 1만 × 루틴 3): ${ELAPSED}ms, 쿼리 ${MQ}개, 생성된 로그 ${LOGS}건 (기대 30,000건)"
grep -h "루틴 로그 생성\|PERF" "$OUT/app-midnight.log" | cut -c1-200 >> "$OUT/measure.log"
fi

# ================= 3부: 알림 스케줄러 =================
if [[ "$PARTS" == *F* ]]; then
reset_db
start_app app-notify-seed.log
stop_app
NOW_T0=$(date +%H:%M:00); NOW_T1=$(date -v+1M +%H:%M:00); NOW_T2=$(date -v+2M +%H:%M:00)
{ echo "SET @dow='$DOW'; SET @t0='$NOW_T0'; SET @t1='$NOW_T1'; SET @t2='$NOW_T2';"; cat "$DIR/seed-notify.sql"; } | $MY > /dev/null
glog_on
start_app app-notify.log --spring.profiles.active=dev,perf --perf.task=notify
for i in $(seq 1 60); do grep -q "PERF done task=notify" "$OUT/app-notify.log" && break; sleep 1; done
glog_off
stop_app
NQ=$($ROOT -e "SELECT COUNT(*) FROM mysql.general_log WHERE command_type='Query' AND argument LIKE 'select%' AND (argument LIKE '%user_routine_schedule%' OR argument LIKE '%from user_routine %' OR argument LIKE '%from user u%' OR argument LIKE '%from routine r%')")
LAZY=$(grep -c "LazyInitializationException" "$OUT/app-notify.log")
SENT=$(grep -c "푸시 알림 전송 완료\|FCM" "$OUT/app-notify.log")
log "F. 알림 스케줄러 (대상 3명): LazyInitializationException ${LAZY}회, 관련 조회 쿼리 ${NQ}개, 발송 로그 ${SENT}줄"
grep -h "PERF\|Lazy\|푸시\|FCM" "$OUT/app-notify.log" | cut -c1-220 | head -8 >> "$OUT/measure.log"
fi
log "완료"
