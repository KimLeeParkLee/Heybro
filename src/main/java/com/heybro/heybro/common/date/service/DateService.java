package com.heybro.heybro.common.date.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class DateService {

    private final ZoneId zoneId = ZoneId.of("Asia/Seoul");

    /**
     * 오늘 날짜를 Asia/Seoul 기준으로 반환
     */
    public LocalDate getToday() {
        return LocalDate.now(zoneId);
    }
}