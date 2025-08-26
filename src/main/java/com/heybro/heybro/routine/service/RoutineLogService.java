package com.heybro.heybro.routine.service;

import com.heybro.heybro.user.domain.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface RoutineLogService {
    void createLogsForUser(User user, LocalDate date);
}
