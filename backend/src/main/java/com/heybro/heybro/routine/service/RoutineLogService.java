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

    // 회원 id 묶음에 대해 해당 날짜 로그를 일괄 생성하고, 생성한 로그 수를 반환
    int createLogsForUsers(List<Long> userIds, LocalDate date);
}
