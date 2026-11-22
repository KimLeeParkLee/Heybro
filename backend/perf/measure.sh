#!/usr/bin/env bash
# 사용법: perf/measure.sh <app.jar> <label>
# DB를 초기화하고 앱을 띄운 뒤, 질문 상세 조회 N+1(쿼리 수·응답 시간)과 포인트 사용 동시성을 측정한다.
# 결과는 perf/results/<label>/ 에 저장된다.
set -uo pipefail
JAR=$1; LABEL=$2
DIR=$(cd "$(dirname "$0")" && pwd)
OUT=$DIR/results/$LABEL; mkdir -p "$OUT"; cd "$OUT"
JAVA=${JAVA_HOME17:-/Users/u/.jdks/jdk-17.0.21.1+2/Contents/Home}/bin/java
BASE=http://localhost:8080/api
MY="mysql -uheybro -pheybro1234 -N -s heybro"

log() { echo "[$(date +%H:%M:%S)] $*" | tee -a "$OUT/measure.log"; }

# 1. DB 초기화
mysql -uroot -e "DROP DATABASE IF EXISTS heybro; CREATE DATABASE heybro CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -uroot -e "SET GLOBAL general_log='OFF'; SET GLOBAL log_output='TABLE'; TRUNCATE mysql.general_log;"

# 2. 앱 기동
log "앱 기동: $JAR"
"$JAVA" -Duser.timezone=Asia/Seoul -jar "$JAR" --spring.profiles.active=dev --spring.jpa.open-in-view=true ${EXTRA_ARGS:-} > "$OUT/app.log" 2>&1 &
APP_PID=$!
for i in $(seq 1 180); do
  code=$(curl -s -o /dev/null -w '%{http_code}' "$BASE/users?email=x@x.x" || true)
  [ "$code" != "000" ] && break; sleep 1
done
log "앱 응답 코드: $code (pid $APP_PID)"
if [ "$code" = "000" ]; then log "앱 기동 실패"; kill $APP_PID; exit 1; fi

# 3. 측정용 회원 가입 → 토큰
REG=$(curl -s -X POST "$BASE/users" -H 'Content-Type: application/json' -d '{"user_name":"측정용","nickname":"perf_bro","email":"perf@heybro.local","password":"perf1234!","gender":"M","birth_date":"1995-01-01","phone":"010-0000-0000","privacy_consent":true,"marketing_consent":false,"notification_enabled":false,"provider":"local"}')
TOKEN=$(printf '%s' "$REG" | python3 -c "import sys,json;d=json.load(sys.stdin);d=d.get('data',d);print(d['access_token'].replace('Bearer ','',1))")
[ -z "$TOKEN" ] && { log "토큰 발급 실패: $REG"; kill $APP_PID; exit 1; }
log "토큰 발급 완료"

# 4. 시드 데이터 (질문 1 + 답변 10 + 답변 이미지 20)
QID=$($MY < "$DIR/seed-qna.sql" | tail -1)
log "시드 질문 id=$QID"

# 5. 쿼리 수 측정 (MySQL general_log, 워밍업 1회 후 2번째 요청만 기록)
curl -s -o /dev/null -H "Authorization: Bearer $TOKEN" "$BASE/questions/$QID"
mysql -uroot -e "TRUNCATE mysql.general_log; SET GLOBAL general_log='ON';"
curl -s -H "Authorization: Bearer $TOKEN" "$BASE/questions/$QID" > "$OUT/detail-response.json"
mysql -uroot -e "SET GLOBAL general_log='OFF';"
mysql -uroot -N -s -e "SELECT argument FROM mysql.general_log WHERE command_type='Query' AND (argument LIKE 'select%' OR argument LIKE 'insert%' OR argument LIKE 'update%' OR argument LIKE 'delete%') AND argument NOT LIKE 'select @@%' ORDER BY event_time" > "$OUT/detail-queries.sql"
QCOUNT=$(wc -l < "$OUT/detail-queries.sql" | tr -d ' ')
log "질문 상세 조회 1회 쿼리 수: $QCOUNT"

# 6. JMeter: 질문 상세 조회
J="jmeter -n -t $DIR/qna-detail.jmx -Jtoken=$TOKEN -JquestionId=$QID"
$J -Jthreads=1 -Jloops=100 -l "$OUT/qna-warmup.jtl" > /dev/null 2>&1
$J -Jthreads=1 -Jloops=300 -l "$OUT/qna-single.jtl" > "$OUT/qna-single.out" 2>&1
log "질문 상세 (1 thread x 300): $(python3 "$DIR/jtl-stats.py" "$OUT/qna-single.jtl" | tr '\n' ' ')"
$J -Jthreads=8 -Jloops=300 -Jrampup=1 -l "$OUT/qna-load.jtl" > "$OUT/qna-load.out" 2>&1
log "질문 상세 (8 threads x 300): $(python3 "$DIR/jtl-stats.py" "$OUT/qna-load.jtl" | tr '\n' ' ')"
# 6-1. 고부하 (HikariCP 풀 20개보다 많은 동시 요청): 커넥션 고갈 여부 확인. HEAVY_THREADS=0 이면 생략
HT=${HEAVY_THREADS:-50}; HL=${HEAVY_LOOPS:-100}
if [ "$HT" -gt 0 ]; then
  $J -Jthreads=$HT -Jloops=$HL -Jrampup=1 -l "$OUT/qna-heavy.jtl" > "$OUT/qna-heavy.out" 2>&1
  log "질문 상세 ($HT threads x $HL): $(python3 "$DIR/jtl-stats.py" "$OUT/qna-heavy.jtl" | tr '\n' ' ')"
  log "커넥션 타임아웃(Connection is not available) 로그 수: $(grep -c 'Connection is not available' "$OUT/app.log")"
fi
# 6-2. 조회수가 요청마다 1씩 반영됐는지 (curl 2회 + JMeter 전체 요청 수)
EXPECTED=$(( 2 + 100 + 300 + 8 * 300 + HT * HL ))
VC=$($MY -e "SELECT view_count FROM question WHERE id=$QID")
log "질문 조회수: $VC (기대값 $EXPECTED)"

# 7. JMeter: 포인트 사용 동시성 (잔액 1000, 10포인트씩 120명 동시 요청 → 정상이면 100 성공 / 20 실패 / 잔액 0)
$MY -e "UPDATE user SET bro_point=1000, total_bro_point=1000 WHERE email='perf@heybro.local'; DELETE FROM point_history;"
PT=120
sed "s|<intProp name=\"groupSize\">[0-9]*</intProp>|<intProp name=\"groupSize\">$PT</intProp>|" "$DIR/point-use.jmx" > "$OUT/point-use.jmx"
jmeter -n -t "$OUT/point-use.jmx" -Jtoken="$TOKEN" -Jthreads=$PT -Jpoint=10 -l "$OUT/point-use.jtl" > "$OUT/point-use.out" 2>&1
BAL=$($MY -e "SELECT bro_point FROM user WHERE email='perf@heybro.local'")
HIST=$($MY -e "SELECT COUNT(*) FROM point_history")
log "포인트 동시 사용 (120 x 10p, 잔액 1000): 최종 잔액=$BAL, 사용 내역 수=$HIST"
log "포인트 JMeter: $(python3 "$DIR/jtl-stats.py" "$OUT/point-use.jtl" | tr '\n' ' ')"
log "포인트 오류 원인: $(grep -o "Caused by: [A-Za-z.]*Exception: [^\[]\{0,80\}" "$OUT/app.log" | sort | uniq -c | sort -rn | head -3 | tr '\n' ';')"

# 8. 종료
kill $APP_PID; wait $APP_PID 2>/dev/null
log "완료"
