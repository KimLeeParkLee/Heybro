package com.heybro.heybro.routine.scheduler;

import com.heybro.heybro.common.date.service.DateService;
import com.heybro.heybro.routine.service.RoutineLogService;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoutineLogScheduler {

    private final UserRepository userRepository;
    private final RoutineLogService routineLogService;
    private final DateService dateService; // DateService 주입

    // 매일 자정(0시 0분 0초)에 실행
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void createDailyRoutineLogs() {
        log.info("매일 루틴 로그 생성을 시작합니다.");

        LocalDate today = dateService.getToday(); // 수정: DateService 사용

        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            routineLogService.createLogsForUser(user, today);
        }

        log.info("{}명의 사용자에 대한 루틴 로그 생성을 완료했습니다.", allUsers.size());
    }
}
