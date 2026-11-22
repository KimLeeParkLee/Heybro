package com.heybro.heybro.point;

import com.heybro.heybro.point.dto.request.PointTransactionRequestDto;
import com.heybro.heybro.point.repository.PointHistoryRepository;
import com.heybro.heybro.point.service.PointService;
import com.heybro.heybro.support.IntegrationTestSupport;
import com.heybro.heybro.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class PointConcurrencyTest extends IntegrationTestSupport {

    @Autowired PointService pointService;
    @Autowired PointHistoryRepository pointHistoryRepository;

    @Test
    @DisplayName("잔액 1,000 인 회원에게 10P 사용 요청 120건을 동시에 보내면 정확히 100건만 성공하고 잔액은 0 이 된다")
    void usePoints_concurrently_neverOverspends() throws Exception {
        User user = saveUser("point-concurrency@test.local", 1000);
        PointTransactionRequestDto request = PointTransactionRequestDto.builder().point(10).build();

        ConcurrentResult result = runConcurrently(120, () -> pointService.usePoints(request, user.getEmail()));

        User after = userRepository.findById(user.getId()).orElseThrow();
        assertThat(result.success()).isEqualTo(100);
        assertThat(result.failure()).isEqualTo(20);                 // 잔액 부족으로 거절
        assertThat(after.getBroPoint()).isZero();                   // 갱신 손실 없음
        assertThat(pointHistoryRepository.findByUserOrderByTransactionDateDesc(after)).hasSize(100);
    }

    @Test
    @DisplayName("동시 적립 100건은 모두 반영된다")
    void earnPoints_concurrently_noLostUpdate() throws Exception {
        User user = saveUser("point-earn@test.local", 0);
        PointTransactionRequestDto request = PointTransactionRequestDto.builder().point(5).build();

        ConcurrentResult result = runConcurrently(100, () -> pointService.earnPoint(request, user.getEmail()));

        User after = userRepository.findById(user.getId()).orElseThrow();
        assertThat(result.failure()).isZero();
        assertThat(after.getBroPoint()).isEqualTo(500);
        assertThat(after.getTotalBroPoint()).isEqualTo(500);
    }
}
