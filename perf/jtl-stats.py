#!/usr/bin/env python3
"""JMeter JTL(CSV) 결과를 읽어 응답 시간 통계를 출력한다."""
import csv, sys, statistics
path = sys.argv[1]
rows = list(csv.DictReader(open(path, newline='')))
if not rows:
    print("no samples"); sys.exit(1)
lat = sorted(int(r['elapsed']) for r in rows)
ok = sum(1 for r in rows if r['success'] == 'true')
codes = {}
for r in rows:
    codes[r['responseCode']] = codes.get(r['responseCode'], 0) + 1
start = min(int(r['timeStamp']) for r in rows)
end = max(int(r['timeStamp']) + int(r['elapsed']) for r in rows)
dur = max((end - start) / 1000.0, 0.001)
def pct(p):
    k = max(0, min(len(lat) - 1, int(round(p / 100.0 * len(lat))) - 1))
    return lat[k]
print(f"samples={len(rows)} success={ok} errors={len(rows)-ok} codes={codes}")
print(f"avg={statistics.mean(lat):.1f}ms median={pct(50)}ms p90={pct(90)}ms p95={pct(95)}ms p99={pct(99)}ms min={lat[0]}ms max={lat[-1]}ms")
print(f"throughput={len(rows)/dur:.1f} req/s (duration {dur:.2f}s)")
