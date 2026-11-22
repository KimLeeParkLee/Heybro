package com.heybro.heybro.routine.scheduler;

import com.heybro.heybro.common.date.service.DateService;
import com.heybro.heybro.routine.service.RoutineLogService;
import com.heybro.heybro.user.repository.UserRoutineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoutineLogScheduler {
    // 한 트랜잭션에서 처리할 회원 수. 너무 크면 트랜잭션이 길어지고, 너무 작으면 쿼리 수가 늘어난다.
    static final int CHUNK_SIZE = 500;

    private final UserRoutineRepository userRoutineRepository;
    private final RoutineLogService routineLogService;
    private final DateService dateService;

    // 매일 자정(0시 0분 0초)에 실행.
    // 이전에는 전체 회원 엔티티를 메모리에 올린 뒤 회원마다 2개 이상의 쿼리를 날리고 전체가 하나의 트랜잭션이었다.
    // 지금은 루틴이 있는 회원 id 만 가져와 500명 단위로 나눠 각각 별도 트랜잭션에서 배치 INSERT 한다.
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void createDailyRoutineLogs() {
        long started = System.currentTimeMillis();
        LocalDate today = dateService.getToday();
        List<Long> userIds = userRoutineRepository.findAllUserIdsWithRoutine();
        log.info("매일 루틴 로그 생성 시작: 대상 회원 {}명, {}명 단위", userIds.size(), CHUNK_SIZE);

        int created = 0;
        int failedChunks = 0;
        for (int from = 0; from < userIds.size(); from += CHUNK_SIZE) {
            List<Long> chunk = userIds.subList(from, Math.min(from + CHUNK_SIZE, userIds.size()));
            try {
                created += routineLogService.createLogsForUsers(chunk, today);
            } catch (Exception e) {
                // 한 묶음의 실패는 기록만 하고 다음 묶음을 계속 처리한다
                failedChunks++;
                log.error("루틴 로그 생성 실패 (회원 id {}~{}): {}", chunk.get(0), chunk.get(chunk.size() - 1), e.getMessage());
            }
        }
        log.info("매일 루틴 로그 생성 완료: {}명 대상, 로그 {}건 생성, 실패 묶음 {}개, {}ms",
                userIds.size(), created, failedChunks, System.currentTimeMillis() - started);
    }
}
