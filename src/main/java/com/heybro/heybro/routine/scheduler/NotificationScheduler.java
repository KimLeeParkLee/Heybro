package com.heybro.heybro.routine.scheduler;

import com.heybro.heybro.common.date.service.DateService;
import com.heybro.heybro.notification.dto.request.FcmSendDto;
import com.heybro.heybro.notification.service.FcmService;
import com.heybro.heybro.user.domain.UserRoutineSchedule;
import com.heybro.heybro.user.repository.UserRoutineScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final UserRoutineScheduleRepository userRoutineScheduleRepository;
    private final FcmService fcmService;
    private final DateService dateService;

    @Scheduled(cron = "0 * * * * *") // 매 분마다 실행
    public void sendRoutineNotifications() {
        ZoneId seoulZone = ZoneId.of("Asia/Seoul");

        // DateService로 오늘 날짜 가져오기
        DayOfWeek today = dateService.getToday().getDayOfWeek();

        // 현재 시간 가져오기 (초, 나노 제거)
        LocalTime now = LocalTime.now(seoulZone).withSecond(0).withNano(0);

        log.info("NotificationScheduler 실행 - 오늘: {}, 현재 시간: {}", today, now);

        List<UserRoutineSchedule> schedules =
                userRoutineScheduleRepository.findAllByDayOfWeekEqualsAndScheduleTimeEquals(today, now);

        for (UserRoutineSchedule schedule : schedules) {
            String deviceToken = schedule.getUserRoutine().getUser().getNotificationToken();
            if (deviceToken != null && !deviceToken.isEmpty()) {
                String routineName = schedule.getUserRoutine().getRoutine().getName();
                String title = "오늘의 루틴";
                String body = "브로! 지금은 " + routineName + "할 시간이에요.";

                FcmSendDto fcmSendDto = FcmSendDto.builder()
                        .targetToken(deviceToken)
                        .title(title)
                        .body(body)
                        .build();

                fcmService.sendMessage(fcmSendDto);
                log.info("푸시 알림 전송 완료: {} - {}", title, body);
            }
        }
    }
}
