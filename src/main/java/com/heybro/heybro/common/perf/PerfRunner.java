package com.heybro.heybro.common.perf;

import com.heybro.heybro.routine.scheduler.NotificationScheduler;
import com.heybro.heybro.routine.scheduler.RoutineLogScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 성능 측정용. perf 프로필에서만 활성화되며, 기동 직후 지정한 스케줄러 작업을 한 번 실행하고 소요 시간을 기록한다.
 * 운영 프로필에서는 빈으로 등록되지 않는다. 사용: --spring.profiles.active=dev,perf --perf.task=midnight|notify
 */
@Slf4j
@Component
@Profile("perf")
@RequiredArgsConstructor
public class PerfRunner implements ApplicationRunner {
    private final RoutineLogScheduler routineLogScheduler;
    private final NotificationScheduler notificationScheduler;

    @Value("${perf.task:}")
    private String task;

    @Override
    public void run(ApplicationArguments args) {
        if (task == null || task.isBlank()) {
            return;
        }
        long started = System.currentTimeMillis();
        log.info("PERF start task={}", task);
        try {
            switch (task) {
                case "midnight" -> routineLogScheduler.createDailyRoutineLogs();
                case "notify" -> notificationScheduler.sendRoutineNotifications();
                default -> log.warn("PERF unknown task {}", task);
            }
        } catch (Exception e) {
            log.error("PERF task {} failed: {}", task, e.toString(), e);
        }
        log.info("PERF done task={} elapsedMs={}", task, System.currentTimeMillis() - started);
    }
}
