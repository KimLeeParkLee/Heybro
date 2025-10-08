package com.heybro.heybro.routine.service;

import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.domain.UserRoutine;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface RoutineLogService {
    void createLogsForUser(User user, LocalDate date);

    void createLogsForUser(User user, List<UserRoutine> userRoutines, LocalDate date);
}
