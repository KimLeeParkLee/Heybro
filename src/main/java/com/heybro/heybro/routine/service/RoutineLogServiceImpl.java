package com.heybro.heybro.routine.service;

import com.heybro.heybro.routine.domain.DailyRoutineLog;
import com.heybro.heybro.routine.domain.Routine;
import com.heybro.heybro.routine.repository.DailyRoutineLogRepository;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.domain.UserRoutine;
import com.heybro.heybro.user.domain.UserRoutineSchedule;
import com.heybro.heybro.user.repository.UserRoutineRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineLogServiceImpl implements RoutineLogService {
    private final UserRoutineRepository userRoutineRepository;
    private final DailyRoutineLogRepository dailyRoutineLogRepository;

    @Transactional
    public void createLogsForUser(User user, LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // 1. 해당 날짜에 이미 생성된 로그가 있는지 확인
        List<DailyRoutineLog> existingLogs = dailyRoutineLogRepository.findAllByUserAndTaskDate(user, date);
        Set<Long> existingRoutineIds = existingLogs.stream()
                .map(log -> log.getRoutine().getId())
                .collect(Collectors.toSet());

        // 2. 사용자의 전체 루틴 스케줄 조회
        List<UserRoutine> userRoutines = userRoutineRepository.findAllByUser(user);
        List<DailyRoutineLog> logsToCreate = new ArrayList<>();

        for (UserRoutine userRoutine : userRoutines) {
            Routine routine = userRoutine.getRoutine();

            // 3. 이미 로그가 존재하면 건너뛰기
            if (existingRoutineIds.contains(routine.getId())) {
                continue;
            }

            for (UserRoutineSchedule schedule : userRoutine.getSchedules()) {
                if (schedule.getDayOfWeek().equals(dayOfWeek)) {
                    DailyRoutineLog newLog = DailyRoutineLog.builder()
                            .user(user)
                            .routine(routine)
                            .taskDate(date)
                            .scheduledTime(schedule.getScheduleTime())
                            .isCompleted(false)
                            .build();
                    logsToCreate.add(newLog);
                    break;
                }
            }
        }

        if (!logsToCreate.isEmpty()) {
            dailyRoutineLogRepository.saveAll(logsToCreate);
        }
    }
}
