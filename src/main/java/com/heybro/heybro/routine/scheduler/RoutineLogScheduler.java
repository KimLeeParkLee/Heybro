package com.heybro.heybro.routine.scheduler;

import com.heybro.heybro.routine.domain.DailyRoutineLog;
import com.heybro.heybro.routine.repository.DailyRoutineLogRepository;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.domain.UserRoutine;
import com.heybro.heybro.user.domain.UserRoutineSchedule;
import com.heybro.heybro.user.repository.UserRepository;
import com.heybro.heybro.user.repository.UserRoutineRepository; // [변경점 1] import 추가
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoutineLogScheduler {

    private final UserRepository userRepository;
    private final DailyRoutineLogRepository dailyRoutineLogRepository;
    private final UserRoutineRepository userRoutineRepository; // [변경점 2] Repository 주입

    // 매일 자정(0시 0분 0초)에 실행
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void createDailyRoutineLogs() {
        log.info("매일 루틴 로그 생성을 시작합니다.");

        LocalDate today = LocalDate.now();
        DayOfWeek todayOfWeek = today.getDayOfWeek();

        // 1. 모든 사용자를 조회합니다.
        List<User> allUsers = userRepository.findAll();

        // 2. 생성할 모든 로그를 담을 리스트를 준비합니다.
        List<DailyRoutineLog> logsToCreate = new ArrayList<>();

        for (User user : allUsers) {
            List<UserRoutine> userRoutines = userRoutineRepository.findAllByUser(user);

            // 3. 각 사용자의 UserRoutine 및 UserRoutineSchedule을 순회합니다.
            for (UserRoutine userRoutine : userRoutines) {
                for (UserRoutineSchedule schedule : userRoutine.getSchedules()) {

                    // 4. 오늘 요일과 일치하는 스케줄이 있다면 로그 생성 대상으로 추가합니다.
                    if (schedule.getDayOfWeek().equals(todayOfWeek)) {
                        DailyRoutineLog newLog = DailyRoutineLog.builder()
                                .user(user)
                                .routine(userRoutine.getRoutine())
                                .taskDate(today)
                                .scheduledTime(schedule.getScheduleTime())
                                .isCompleted(false)
                                .build();
                        logsToCreate.add(newLog);
                    }
                }
            }
        }

        // 5. 준비된 모든 로그를 데이터베이스에 한 번에 저장합니다. (성능 최적화)
        if (!logsToCreate.isEmpty()) {
            dailyRoutineLogRepository.saveAll(logsToCreate);
            log.info("{}명의 사용자에 대해 총 {}개의 루틴 로그를 생성했습니다.", allUsers.size(), logsToCreate.size());
        } else {
            log.info("오늘 생성할 루틴 로그가 없습니다.");
        }
    }
}