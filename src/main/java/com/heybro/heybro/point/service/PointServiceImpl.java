package com.heybro.heybro.point.service;

import com.heybro.heybro.point.dto.request.PointTransactionRequestDto;
import com.heybro.heybro.point.dto.response.PointBalanceResponseDto;
import com.heybro.heybro.point.dto.response.TotalPointBalanceResponseDto;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointServiceImpl implements PointService {
    private final UserRepository userRepository;

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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        user.earnPoints(requestDto.getPoint());
    }

    @Override
    @Transactional
    public void usePoints(PointTransactionRequestDto requestDto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        // 현재 포인트가 사용할 포인트보다 적을 경우 400 반환
        if (user.getBroPoint() < requestDto.getPoint()) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        user.usePoints(requestDto.getPoint());
    }
}
