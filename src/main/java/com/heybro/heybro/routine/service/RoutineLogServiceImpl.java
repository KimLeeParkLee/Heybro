package com.heybro.heybro.routine.service;

import com.heybro.heybro.routine.domain.DailyRoutineLog;
import com.heybro.heybro.routine.domain.Routine;
import com.heybro.heybro.routine.repository.DailyRoutineLogRepository;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.domain.UserRoutine;
import com.heybro.heybro.user.domain.UserRoutineSchedule;
import com.heybro.heybro.user.repository.UserRoutineRepository;
import com.heybro.heybro.user.repository.UserRoutineScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Date;
import java.sql.Time;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoutineLogServiceImpl implements RoutineLogService {
    private final UserRoutineRepository userRoutineRepository;
    private final DailyRoutineLogRepository dailyRoutineLogRepository;
    private final UserRoutineScheduleRepository userRoutineScheduleRepository;
    private final JdbcTemplate jdbcTemplate;

    public void createLogsForUser(User user, LocalDate date) {
        // 1. DB에서 루틴 목록 조회
        List<UserRoutine> userRoutines = userRoutineRepository.findAllByUserWithDetails(user);

        // 2. 이미 구현된 다른 메서드를 호출하여 로직 재사용
        createLogsForUser(user, userRoutines, date);
    }

    public void createLogsForUser(User user, List<UserRoutine> userRoutines, LocalDate date) {
        // 1. 해당 날짜에 이미 생성된 로그가 있는지 확인
        if (dailyRoutineLogRepository.existsByUserAndTaskDate(user, date)) {
            return;
        }

        DayOfWeek dayOfWeek = date.getDayOfWeek();

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

    // 자정 스케줄러용. 회원 묶음(chunk)마다 별도 트랜잭션(REQUIRES_NEW)으로 처리해 한 묶음의 실패가 다른 묶음에 번지지 않게 한다.
    // 쿼리는 묶음당 3개(이미 생성된 회원 확인 1, 스케줄 조회 1, 배치 INSERT 1)로, 회원 수에 비례하지 않는다.
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int createLogsForUsers(List<Long> userIds, LocalDate date) {
        if (userIds.isEmpty()) {
            return 0;
        }

        // 1. 이미 로그가 있는 회원 제외 (앱에서 먼저 조회해 즉시 생성된 경우)
        Set<Long> alreadyCreated = new HashSet<>(dailyRoutineLogRepository.findUserIdsWithLogOn(userIds, date));

        // 2. 묶음의 해당 요일 스케줄을 루틴과 함께 한 번에 조회
        List<UserRoutineSchedule> schedules = userRoutineScheduleRepository.findAllByUserIdsAndDayOfWeek(userIds, date.getDayOfWeek());

        // 3. 회원-루틴 조합당 로그 1건 (같은 요일에 스케줄이 여러 개여도 한 번만)
        Set<String> seen = new HashSet<>();
        List<Object[]> rows = new ArrayList<>();
        for (UserRoutineSchedule schedule : schedules) {
            UserRoutine userRoutine = schedule.getUserRoutine();
            Long userId = userRoutine.getUser().getId(); // 프록시의 id 접근은 추가 조회를 일으키지 않음
            if (alreadyCreated.contains(userId)) {
                continue;
            }
            Long routineId = userRoutine.getRoutine().getId();
            if (!seen.add(userId + ":" + routineId)) {
                continue;
            }
            rows.add(new Object[]{
                    Date.valueOf(date),
                    schedule.getScheduleTime() == null ? null : Time.valueOf(schedule.getScheduleTime()),
                    userId,
                    routineId
            });
        }

        if (rows.isEmpty()) {
            return 0;
        }

        // 4. 배치 INSERT. IDENTITY 전략이라 JPA 배치가 동작하지 않으므로 JDBC 로 직접 넣는다.
        //    유니크 제약(user_id, routine_id, task_date)과 충돌하면 그 행만 건너뛴다 (동시에 앱이 생성한 경우).
        jdbcTemplate.batchUpdate(
                "INSERT INTO daily_routine_log (task_date, scheduled_time, is_completed, rewarded, user_id, routine_id) " +
                        "VALUES (?, ?, false, false, ?, ?) ON DUPLICATE KEY UPDATE id = id",
                rows);
        return rows.size();
    }
}
