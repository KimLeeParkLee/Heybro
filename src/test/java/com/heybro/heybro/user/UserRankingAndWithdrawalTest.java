package com.heybro.heybro.user;

import com.heybro.heybro.point.domain.PointHistory;
import com.heybro.heybro.point.domain.TransactionType;
import com.heybro.heybro.point.repository.PointHistoryRepository;
import com.heybro.heybro.routine.domain.DailyRoutineLog;
import com.heybro.heybro.routine.repository.DailyRoutineLogRepository;
import com.heybro.heybro.routine.repository.RoutineRepository;
import com.heybro.heybro.support.IntegrationTestSupport;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.dto.response.UserRankingResponseDto;
import com.heybro.heybro.user.service.UserService;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserRankingAndWithdrawalTest extends IntegrationTestSupport {

    @Autowired UserService userService;
    @Autowired PointHistoryRepository pointHistoryRepository;
    @Autowired DailyRoutineLogRepository dailyRoutineLogRepository;
    @Autowired RoutineRepository routineRepository;
    @Autowired EntityManagerFactory entityManagerFactory;

    @Test
    @DisplayName("내 순위와 TOP 10 은 같은 기준(누적 포인트, 동점 동순위)으로 계산되고 현재 잔액에는 영향받지 않는다")
    void ranking_usesSameBasisForTop10AndMyRank() {
        saveUser("rank-a@test.local", 300);
        saveUser("rank-b@test.local", 300);
        User me = saveUser("rank-me@test.local", 100);
        // 현재 잔액만 줄여도 누적 포인트 기준이라 순위가 바뀌지 않아야 한다
        me.usePoints(100);
        userRepository.save(me);

        UserRankingResponseDto result = userService.getUserRankings(me.getEmail());

        // TOP 10: 누적 포인트 내림차순, 동점은 같은 순위, 순위는 건너뛰지 않음(DENSE)
        var rankings = result.getRankings();
        for (int i = 1; i < rankings.size(); i++) {
            assertThat(rankings.get(i).getTotalBroPoint()).isLessThanOrEqualTo(rankings.get(i - 1).getTotalBroPoint());
            boolean tie = rankings.get(i).getTotalBroPoint() == rankings.get(i - 1).getTotalBroPoint();
            assertThat(rankings.get(i).getRank()).isEqualTo(rankings.get(i - 1).getRank() + (tie ? 0 : 1));
        }
        // 내 순위 = 나보다 누적 포인트가 큰 "값"의 개수 + 1 (다른 테스트가 만든 회원도 포함해 전체 기준으로 계산)
        long higherDistinct = userRepository.findAll().stream()
                .map(User::getTotalBroPoint).filter(p -> p > me.getTotalBroPoint()).distinct().count();
        assertThat(result.getMyRanking().getRank()).isEqualTo((int) higherDistinct + 1);
        assertThat(result.getMyRanking().getTotalBroPoint()).isEqualTo(100);
    }

    @Test
    @DisplayName("탈퇴하면 개인 데이터는 벌크로 지워지고 회원 행은 익명화되며, 삭제 쿼리 수가 데이터 양에 비례하지 않는다")
    void deleteUser_bulkDeletesAndAnonymizes() {
        User user = saveUser("withdraw@test.local", 50);
        var routine = routineRepository.findAll().get(0);
        for (int i = 0; i < 200; i++) {
            dailyRoutineLogRepository.save(DailyRoutineLog.builder()
                    .user(user).routine(routine).taskDate(LocalDate.now().minusDays(i)).isCompleted(true).build());
            pointHistoryRepository.save(PointHistory.builder()
                    .user(user).amount(10).transactionType(TransactionType.EARN).transactionDate(LocalDateTime.now()).description("x").build());
        }
        Statistics stats = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        userService.deleteUser(user.getEmail());

        User after = userRepository.findById(user.getId()).orElseThrow();
        assertThat(after.getWithdrawnAt()).isNotNull();
        assertThat(after.getEmail()).startsWith("withdrawn_");
        assertThat(after.getNickname()).startsWith("탈퇴한 회원");
        assertThat(dailyRoutineLogRepository.findAllByUserAndTaskDate(after, LocalDate.now())).isEmpty();
        assertThat(pointHistoryRepository.findByUserOrderByTransactionDateDesc(after)).isEmpty();
        assertThat(stats.getPrepareStatementCount()).isLessThan(15); // 이전 방식은 행마다 DELETE (400개 이상)
    }
}
