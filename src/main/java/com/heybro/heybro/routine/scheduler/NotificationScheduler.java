package com.heybro.heybro.routine.scheduler;

import com.heybro.heybro.notification.dto.request.FcmSendDto;
import com.heybro.heybro.notification.service.FcmService;
import com.heybro.heybro.user.domain.UserRoutineSchedule;
import com.heybro.heybro.user.repository.UserRoutineScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final UserRoutineScheduleRepository userRoutineScheduleRepository;
    private final FcmService fcmService;

    @Scheduled(cron = "0 * * * * *") // 매 분마다 실행
    public void sendRoutineNotifications() {
        ZoneId seoulZone = ZoneId.of("Asia/Seoul");
        LocalTime now = LocalTime.now(seoulZone).withSecond(0).withNano(0);
        DayOfWeek today = LocalDate.now(seoulZone).getDayOfWeek();

        List<UserRoutineSchedule> schedules = userRoutineScheduleRepository.findAllByDayOfWeekEqualsAndScheduleTimeEquals(today, now);

        for (UserRoutineSchedule schedule : schedules) {
            String deviceToken = schedule.getUserRoutine().getUser().getNotificationToken();
            if (deviceToken != null && !deviceToken.isEmpty()) {
                String routineName = schedule.getUserRoutine().getRoutine().getName();
                String title = "오늘의 루틴 알림";
                String body = "오늘의 루틴을 실천할 시간입니다: " + routineName;

                FcmSendDto fcmSendDto = FcmSendDto.builder()
                        .targetToken(deviceToken)
                        .title(title)
                        .body(body)
                        .build();

                fcmService.sendMessage(fcmSendDto);
            }
        }
    }
}
