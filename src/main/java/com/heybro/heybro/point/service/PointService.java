package com.heybro.heybro.point.service;

import com.heybro.heybro.point.dto.request.PointTransactionRequestDto;
import com.heybro.heybro.point.dto.response.PointBalanceResponseDto;
import com.heybro.heybro.point.dto.response.TotalPointBalanceResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface PointService {
    PointBalanceResponseDto getPointBalance(String email);

    TotalPointBalanceResponseDto getTotalPointBalance(String email);

    void earnPoint(PointTransactionRequestDto requestDto, String email);
}
