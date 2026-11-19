#!/usr/bin/env bash
# 랭킹 쿼리 실행 계획 비교 (회원 10만 명 시드 후 실행). 사용법: perf/explain-ranking.sh > results/<label>/explain-ranking.txt
MY="mysql -uheybro -pheybro1234 heybro"
run() { echo "--- $1"; $MY -e "EXPLAIN FORMAT=TREE $2" 2>/dev/null | tail -n +2 | sed 's/\\n/\n/g'; echo; }
echo "### 개선 후 (idx_user_total_bro_point 있음)"
run "TOP10 (new)" "SELECT u.nickname, u.total_bro_point, u.profile_image FROM user u ORDER BY u.total_bro_point DESC, u.id DESC LIMIT 10"
run "내 순위 (new)" "SELECT COUNT(DISTINCT u.total_bro_point)+1 FROM user u WHERE u.total_bro_point > (SELECT me.total_bro_point FROM user me WHERE me.email='perf@heybro.local')"
$MY -e "ALTER TABLE user DROP INDEX idx_user_total_bro_point" 2>/dev/null
echo "### 개선 전 (인덱스 없음, 윈도 함수)"
run "TOP10 (old)" "SELECT u.nickname, u.total_bro_point, u.profile_image, DENSE_RANK() OVER (ORDER BY u.total_bro_point DESC) AS ranking FROM user u ORDER BY u.total_bro_point DESC LIMIT 10"
run "내 순위 (old)" "SELECT ranking FROM (SELECT email, RANK() OVER (ORDER BY bro_point DESC) AS ranking FROM user) r WHERE email='perf@heybro.local'"
$MY -e "ALTER TABLE user ADD INDEX idx_user_total_bro_point (total_bro_point)" 2>/dev/null
