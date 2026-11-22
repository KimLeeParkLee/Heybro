package com.heybro.heybro.point.service;

import com.heybro.heybro.point.dto.request.PointTransactionRequestDto;
import com.heybro.heybro.point.dto.response.PointBalanceResponseDto;
import com.heybro.heybro.point.dto.response.PointHistoryResponseDto;
import com.heybro.heybro.point.dto.response.TotalPointBalanceResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PointService {
    PointBalanceResponseDto getPointBalance(String email);

    TotalPointBalanceResponseDto getTotalPointBalance(String email);

    void earnPoint(PointTransactionRequestDto requestDto, String email);

    void usePoints(PointTransactionRequestDto requestDto, String email);

    List<PointHistoryResponseDto> getPointHistory(String email);
}
