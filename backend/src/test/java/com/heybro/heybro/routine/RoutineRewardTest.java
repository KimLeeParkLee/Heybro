package com.heybro.heybro.routine;

import com.heybro.heybro.common.date.service.DateService;
import com.heybro.heybro.point.repository.PointHistoryRepository;
import com.heybro.heybro.routine.domain.DailyRoutineLog;
import com.heybro.heybro.routine.domain.Routine;
import com.heybro.heybro.routine.repository.DailyRoutineLogRepository;
import com.heybro.heybro.routine.repository.RoutineRepository;
import com.heybro.heybro.routine.service.RoutineService;
import com.heybro.heybro.support.IntegrationTestSupport;
import com.heybro.heybro.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class RoutineRewardTest extends IntegrationTestSupport {

    @Autowired RoutineService routineService;
    @Autowired RoutineRepository routineRepository;
    @Autowired DailyRoutineLogRepository dailyRoutineLogRepository;
    @Autowired PointHistoryRepository pointHistoryRepository;
    @Autowired DateService dateService;

    @Test
    @DisplayName("루틴 완료를 여러 번 토글해도 포인트·경험치는 하루 한 번만 지급되고 내역이 1건 남는다")
    void completeUserRoutine_rewardsOnlyOncePerDay() {
        User user = saveUser("routine-reward@test.local", 0);
        Routine routine = routineRepository.findAll().get(0); // 초기 데이터 로더가 넣은 루틴
        dailyRoutineLogRepository.save(DailyRoutineLog.builder()
                .user(user).routine(routine).taskDate(dateService.getToday()).isCompleted(false).build());

        for (int i = 0; i < 20; i++) {
            routineService.completeUserRoutine(user.getEmail(), routine.getId());
        }

        User after = userRepository.findById(user.getId()).orElseThrow();
        assertThat(after.getBroPoint()).isEqualTo(10);
        assertThat(after.getExperience()).isEqualTo(10);
        assertThat(pointHistoryRepository.findByUserOrderByTransactionDateDesc(after)).hasSize(1);
    }
}
