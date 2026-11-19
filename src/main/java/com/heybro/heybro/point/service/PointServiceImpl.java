package com.heybro.heybro.point.service;

import com.heybro.heybro.common.exception.BusinessException;
import com.heybro.heybro.common.exception.ErrorCode;
import com.heybro.heybro.point.domain.PointHistory;
import com.heybro.heybro.point.domain.TransactionType;
import com.heybro.heybro.point.dto.request.PointTransactionRequestDto;
import com.heybro.heybro.point.dto.response.PointBalanceResponseDto;
import com.heybro.heybro.point.dto.response.PointHistoryResponseDto;
import com.heybro.heybro.point.dto.response.TotalPointBalanceResponseDto;
import com.heybro.heybro.point.repository.PointHistoryRepository;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointServiceImpl implements PointService {
    private final UserRepository userRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Override
    public PointBalanceResponseDto getPointBalance(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        return PointBalanceResponseDto.builder().point(user.getBroPoint()).build();
    }

    @Override
    public TotalPointBalanceResponseDto getTotalPointBalance(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        return TotalPointBalanceResponseDto.builder().totalPoint(user.getTotalBroPoint()).build();
    }

    @Override
    @Transactional
    public void earnPoint(PointTransactionRequestDto requestDto, String email) {
        // 비관적 락으로 회원 행을 잠근 뒤 포인트를 변경 (동시 적립 시 갱신 손실 방지)
        User user = userRepository.findByEmailForUpdate(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        if (requestDto.getPoint() <= 0) {
            throw new BusinessException(ErrorCode.INVALID_POINT_AMOUNT);
        }

        // 포인트 적립
        user.earnPoints(requestDto.getPoint());

        // 포인트 변동 내역 저장
        pointHistoryRepository.save(PointHistory.builder()
                .amount(requestDto.getPoint())
                .transactionDate(LocalDateTime.now())
                .transactionType(TransactionType.EARN)
                .description("루틴 달성")
                .user(user)
                .build());
    }

    @Override
    @Transactional
    public void usePoints(PointTransactionRequestDto requestDto, String email) {
        // 비관적 락으로 회원 행을 잠근 뒤 잔액 검증과 차감을 수행
        // 락 없이 조회하면 동시 요청이 같은 잔액을 읽어 잔액 이상으로 사용되는 문제 발생
        User user = userRepository.findByEmailForUpdate(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        // 음수·0 금액은 거절 (음수를 보내면 잔액이 늘어나는 문제 방지, DTO 검증과 이중 방어)
        if (requestDto.getPoint() <= 0) {
            throw new BusinessException(ErrorCode.INVALID_POINT_AMOUNT);
        }

        // 현재 포인트가 사용할 포인트보다 적을 경우 400 반환
        if (user.getBroPoint() < requestDto.getPoint()) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_POINT);
        }

        // 포인트 사용
        user.usePoints(requestDto.getPoint());

        // 포인트 변동 내역 저장
        pointHistoryRepository.save(PointHistory.builder()
                .amount(requestDto.getPoint())
                .transactionDate(LocalDateTime.now())
                .transactionType(TransactionType.USE)
                .description("쿠폰 결제")
                .user(user)
                .build());
    }

    @Override
    public List<PointHistoryResponseDto> getPointHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        List<PointHistoryResponseDto> responseDtoList = new ArrayList<>();
        List<PointHistory> histories = pointHistoryRepository.findByUserOrderByTransactionDateDesc(user);

        for (PointHistory history : histories) {
            responseDtoList.add(PointHistoryResponseDto.builder()
                    .transactionType(history.getTransactionType())
                    .transactionDate(history.getTransactionDate())
                    .description(history.getDescription())
                    .amount(history.getAmount())
                    .build());
        }

        return responseDtoList;
    }
}
