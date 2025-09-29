package com.heybro.heybro.routine.service;

import com.heybro.heybro.routine.domain.DailyRoutineLog;
import com.heybro.heybro.routine.domain.Routine;
import com.heybro.heybro.routine.repository.DailyRoutineLogRepository;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.domain.UserRoutine;
import com.heybro.heybro.user.domain.UserRoutineSchedule;
import com.heybro.heybro.user.repository.UserRoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutineLogServiceImpl implements RoutineLogService {
    private final UserRoutineRepository userRoutineRepository;
    private final DailyRoutineLogRepository dailyRoutineLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createLogsForUser(User user, LocalDate date) {
        // 1. 해당 날짜에 이미 생성된 로그가 있는지 확인
        if (dailyRoutineLogRepository.existsByUserAndTaskDate(user, date)) {
            return;
        }

        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // 2. 사용자의 전체 루틴 스케줄 조회
        List<UserRoutine> userRoutines = userRoutineRepository.findAllByUser(user);
        List<DailyRoutineLog> logsToCreate = new ArrayList<>();

        for (UserRoutine userRoutine : userRoutines) {
            Routine routine = userRoutine.getRoutine();

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
