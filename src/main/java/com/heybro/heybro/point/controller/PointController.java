package com.heybro.heybro.point.controller;

import com.heybro.heybro.point.dto.request.PointTransactionRequestDto;
import com.heybro.heybro.point.dto.response.PointBalanceResponseDto;
import com.heybro.heybro.point.dto.response.TotalPointBalanceResponseDto;
import com.heybro.heybro.point.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/points")
@Tag(name = "포인트", description = "포인트 API")
@Slf4j
public class PointController {
    private final PointService pointService;

    @Operation(summary = "포인트 조회")
    @GetMapping("/balance")
    public PointBalanceResponseDto getPointBalance(@AuthenticationPrincipal UserDetails userDetails) {
        return pointService.getPointBalance(userDetails.getUsername());
    }

    @Operation(summary = "누적 포인트 조회")
    @GetMapping("/total")
    public TotalPointBalanceResponseDto getTotalPointBalance(@AuthenticationPrincipal UserDetails userDetails) {
        return pointService.getTotalPointBalance(userDetails.getUsername());
    }

    @Operation(summary = "현재 포인트, 누적 포인트 적립")
    @GetMapping("/earn")
    public void earnPoints(@RequestBody PointTransactionRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {
        pointService.earnPoint(requestDto, userDetails.getUsername());
    }
}
