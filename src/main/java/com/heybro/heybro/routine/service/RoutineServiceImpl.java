package com.heybro.heybro.routine.service;

import com.heybro.heybro.routine.domain.*;
import com.heybro.heybro.routine.dto.request.RoutineAddRequestDto;
import com.heybro.heybro.routine.dto.request.RoutineModifyRequestDto;
import com.heybro.heybro.routine.dto.response.*;
import com.heybro.heybro.routine.repository.DailyRoutineLogRepository;
import com.heybro.heybro.routine.repository.RoutineRepository;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.domain.UserRoutine;
import com.heybro.heybro.user.domain.UserRoutineSchedule;
import com.heybro.heybro.user.dto.response.RoutineAddResponseDto;
import com.heybro.heybro.user.dto.response.UserRoutineResponseDto;
import com.heybro.heybro.user.repository.UserRepository;
import com.heybro.heybro.user.repository.UserRoutineRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoutineServiceImpl implements RoutineService {
    private final UserRepository userRepository;
    private final UserRoutineRepository userRoutineRepository;
    private final DailyRoutineLogRepository dailyRoutineLogRepository;
    private final RoutineLogService routineLogService;
    private final RoutineRepository routineRepository;

    /**
     * 과거부터 오늘까지면 DailyRoutineLog에서 조회
     * 오늘 이후부터면 UserRoutineSchedule에서 조회
     */

    @Override
    public UserRoutineResponseDto getRoutinesByDate(LocalDate date, String email) {
        // 오늘 이후부터 조회
        if (date.isAfter(LocalDate.now())) {
            // (1) user 조회
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

            // (2) 조회한 user로 모든 UserRoutine 조회
            List<UserRoutine> userRoutines = userRoutineRepository.findAllByUser(user);

            // 응답할 Dto의 루틴 리스트
            List<UserRoutineResponseDto.RoutineResponseDto> routineResponseDtoList = new ArrayList<>();

            // (3) 각 UserRoutine에 대해 반복
            for (UserRoutine userRoutine : userRoutines) {
                // (4) 해당 UserRoutine의 스케줄 중, 요청된 날짜의 요일과 일치하는 스케줄만 필터링
                List<UserRoutineSchedule> filteredSchedules = userRoutine.getSchedules().stream()
                        .filter(schedule -> schedule.getDayOfWeek().equals(date.getDayOfWeek()))
                        .toList();

                if (filteredSchedules.isEmpty()) {
                    continue; // 해당 요일에 스케줄이 없으면 다음 UserRoutine으로 넘어감
                }

                Routine routine = userRoutine.getRoutine();

                // (6) 필터링된 각 스케줄에 대해 최종 RoutineResponseDto를 만들어 리스트에 추가
                for (UserRoutineSchedule schedule : filteredSchedules) {
                    UserRoutineResponseDto.RoutineResponseDto routineDto = UserRoutineResponseDto.RoutineResponseDto.builder()
                            .routineId(routine.getId())
                            .scheduleTime(schedule.getScheduleTime())
                            .routineName(routine.getName())
                            .iconImage(routine.getIconImage())
                            .timeOfDay(routine.getTimeOfDay())
                            .completed(false) // 미래 날짜이므로 항상 false
                            .build();
                    routineResponseDtoList.add(routineDto);
                }
            }

            // TimeOfDay순으로 먼저 정렬 후 scheduleTime 순으로 정렬
            routineResponseDtoList.sort(
                    Comparator.comparing(
                            UserRoutineResponseDto.RoutineResponseDto::getTimeOfDay,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    ).thenComparing(
                            UserRoutineResponseDto.RoutineResponseDto::getScheduleTime,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    )
            );

            // (7) 최종적으로 UserRoutineResponseDto로 감싸서 반환
            return UserRoutineResponseDto.builder()
                    .routines(routineResponseDtoList)
                    .build();
        } else { // 과거~오늘까지 조회
            // (1) user 조회
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

            // (2) 해당 날짜의 모든 로그를 DailyRoutineLog에서 조회
            List<DailyRoutineLog> logs = dailyRoutineLogRepository.findAllByUserAndTaskDate(user, date);

            // 만약 오늘 날짜인데 로그가 없으면 즉시 생성
            if (logs.isEmpty() && date.isEqual(LocalDate.now())) {
                routineLogService.createLogsForUser(user, LocalDate.now());
                logs = dailyRoutineLogRepository.findAllByUserAndTaskDate(user, date);
            }

            // (3) 로그를 DTO로 변환
            List<UserRoutineResponseDto.RoutineResponseDto> routineResponseDtoList = logs.stream()
                    .map(log -> {
                        Routine routine = log.getRoutine();

                        return UserRoutineResponseDto.RoutineResponseDto.builder()
                                .routineId(routine.getId())
                                .scheduleTime(log.getScheduledTime())
                                .routineName(routine.getName())
                                .timeOfDay(routine.getTimeOfDay())
                                .iconImage(routine.getIconImage())
                                .completed(log.isCompleted()) // 로그에 기록된 실제 완료 여부
                                .build();
                    })
                    .collect(Collectors.toList());

            // TimeOfDay, scheduleTime 순으로 정렬
            routineResponseDtoList.sort(
                    Comparator.comparing(
                            UserRoutineResponseDto.RoutineResponseDto::getTimeOfDay,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    ).thenComparing(
                            UserRoutineResponseDto.RoutineResponseDto::getScheduleTime,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    )
            );


            return UserRoutineResponseDto.builder()
                    .routines(routineResponseDtoList)
                    .build();
        }
    }

    @Override
    public RoutineDetailResponseDto getRoutines(Long routineId) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 루틴을 찾을 수 없습니다."));

        List<RoutineElementResponseDto> elementDtos = routine.getElementList().stream()
                .sorted(Comparator.comparing(RoutineElement::getStep))
                .map(RoutineElementResponseDto::from)
                .toList();

        List<RoutineTipResponseDto> tipDtos = routine.getTipList().stream()
                .map(RoutineTipResponseDto::from)
                .toList();

        return RoutineDetailResponseDto.builder()
                .elements(elementDtos)
                .tips(tipDtos)
                .build();
    }

    @Override
    @Transactional
    public void completeUserRoutine(String email, Long routineId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 루틴을 찾을 수 없습니다."));

        // (1) DailyRoutineLog에서 회원, 루틴 아이디로 해당하는 루틴 찾기
        DailyRoutineLog logs = dailyRoutineLogRepository.findByUserAndRoutineAndTaskDate(user, routine, LocalDate.now());

        // (2) 완료 상태로 변경
        logs.toggleCompletion();

        // (3) 브로 포인트, 경험치 10씩 추가
        user.earnPoints(10);
        user.updateExperience(10);
    }

    // 단일 루틴 달성률 조회 시 월별, 일별만 가능
    @Override
    public AchievementSummaryResponseDto getSummaryAchievements(ViewType view, PeriodType period, LocalDate date, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        List<DailyRoutineLog> logs;

        // PeriodType.MONTH인 경우 : 월 루틴 달성률 조회
        if (period == PeriodType.month) {
            // 해당 월의 시작일과 마지막일 계산
            LocalDate startDate = date.withDayOfMonth(1);
            LocalDate endDate = date.withDayOfMonth(date.lengthOfMonth());

            // DailyRoutineLog에서 date에 해당하는 달의 routine log 가져오기
            logs = dailyRoutineLogRepository.findAllByUserAndTaskDateBetween(user, startDate, endDate);
        } else { // PeriodType.DATE인 경우 : 일 루틴 달성률 조회
            // DailyRoutineLog에서 date에 해당하는 달의 routine log 가져오기
            logs = dailyRoutineLogRepository.findAllByUserAndTaskDate(user, date);
        }

        long totalCount = logs.size();
        long completedCount = logs.stream().filter(DailyRoutineLog::isCompleted).count();
        System.out.println(Math.round((double) completedCount / totalCount));
        int achievementRate = (totalCount == 0) ? 0 : (int) Math.round((double) completedCount / totalCount * 100.0);

        return AchievementSummaryResponseDto.builder()
                .view(view)
                .period(period)
                .date(date)
                .achievementRate(achievementRate)
                .build();
    }

    // 목록 루틴 달성률 조회 시 월별, 주별만 가능
    // 일주일은 해당 주의 일요일~토요일 조회
    @Override
    public AchievementListResponseDto getListAchievements(ViewType view, PeriodType period, LocalDate date, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        LocalDate startDate;
        LocalDate endDate;

        // PeriodType.MONTH인 경우 : 해당 달의 모든 일 루틴 달성률 조회
        if (period == PeriodType.month) {
            // 해당 월의 시작일과 마지막일 계산
            startDate = date.withDayOfMonth(1);
            endDate = date.withDayOfMonth(date.lengthOfMonth());
        } else { // PeriodType.WEEK인 경우 : 해당 주의 모든 일 루틴 달성률 조회
            // 해당 주의 시작일(일요일)과 마지막일(토요일) 계산
            startDate = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
            endDate = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        }

        // DailyRoutineLog에서 date에 해당하는 달 또는 주의 routine log 가져오기
        List<DailyRoutineLog> logs = dailyRoutineLogRepository.findAllByUserAndTaskDateBetween(user, startDate, endDate);

        // 가져온 로그들을 날짜(taskDate)별로 그룹화
        Map<LocalDate, List<DailyRoutineLog>> logsByDate = logs.stream()
                .collect(Collectors.groupingBy(DailyRoutineLog::getTaskDate));

        List<AchievementListResponseDto.AchievementResponseDto> dailyAchievements = new ArrayList<>();

        // 시작일부터 종료일까지 하루씩 반복
        for (LocalDate day = startDate; !day.isAfter(endDate); day = day.plusDays(1)) {
            // 해당 날짜에 해당하는 로그 리스트를 Map에서 찾기
            List<DailyRoutineLog> dailyLogs = logsByDate.get(day);

            if (dailyLogs == null || dailyLogs.isEmpty()) {
                dailyAchievements.add(
                        AchievementListResponseDto.AchievementResponseDto.builder()
                                .date(day)
                                .achievementRate(null)
                                .build()
                );
            } else {
                // 로그가 있는 경우에만 달성률 계산 (0% ~ 100%)
                long totalCount = dailyLogs.size();
                long completedCount = dailyLogs.stream().filter(DailyRoutineLog::isCompleted).count();
                int achievementRate = (int) Math.round((double) completedCount / totalCount * 100.0);

                dailyAchievements.add(
                        AchievementListResponseDto.AchievementResponseDto.builder()
                                .date(day)
                                .achievementRate(achievementRate)
                                .build()
                );
            }
        }

        return AchievementListResponseDto.builder()
                .view(view)
                .period(period)
                .startDate(startDate)
                .endDate(endDate)
                .achievements(dailyAchievements)
                .build();
    }

    @Override
    @Transactional
    public void addRoutine(Long routineId, RoutineAddRequestDto requestDto, String email) {
        User user = findUserByEmail(email);
        Routine routine = findRoutineById(routineId);

        // 이미 추가된 루틴인지 확인
        if (userRoutineRepository.existsByUserAndRoutine(user, routine)) {
            throw new IllegalArgumentException("이미 추가된 루틴입니다.");
        }

        UserRoutine userRoutine = UserRoutine.builder()
                .user(user)
                .routine(routine)
                .build();

        List<UserRoutineSchedule> schedules = requestDto.getRoutineInfos().stream()
                .map(info -> UserRoutineSchedule.builder()
                        .scheduleTime(info.getScheduleTime())
                        .dayOfWeek(info.getDayOfWeek())
                        .userRoutine(userRoutine)
                        .build())
                .toList();

        userRoutine.getSchedules().addAll(schedules);
        userRoutineRepository.save(userRoutine);
    }

    @Override
    @Transactional
    public void modifyRoutine(Long routineId, RoutineModifyRequestDto requestDto, String email) {
        User user = findUserByEmail(email);
        Routine routine = findRoutineById(routineId);
        UserRoutine userRoutine = findUserRoutine(user, routine);

        // 새로운 스케줄 리스트를 생성
        List<UserRoutineSchedule> newSchedules = requestDto.getRoutineInfos().stream()
                .map(info -> UserRoutineSchedule.builder()
                        .scheduleTime(info.getScheduleTime())
                        .dayOfWeek(info.getDayOfWeek())
                        .userRoutine(userRoutine)
                        .build())
                .collect(Collectors.toList());

        // 엔티티의 업데이트 메서드를 사용
        userRoutine.updateSchedules(newSchedules);
    }

    @Override
    @Transactional
    public void deleteRoutine(Long routineId, String email) {
        User user = findUserByEmail(email);
        Routine routine = findRoutineById(routineId);
        UserRoutine userRoutine = findUserRoutine(user, routine);

        userRoutineRepository.delete(userRoutine);
    }

    @Override
    public RoutineAddResponseDto getAvailableRoutines(String email) {
        User user = findUserByEmail(email);

        // 1. 사용자가 이미 가지고 있는 루틴 ID 목록을 조회
        List<Long> userRoutineIds = userRoutineRepository.findAllByUser(user).stream()
                .map(userRoutine -> userRoutine.getRoutine().getId())
                .toList();

        List<Routine> availableRoutines;

        // 2. 사용자가 가진 루틴이 없으면 모든 루틴을, 있으면 해당 루틴을 제외하고 조회
        if (userRoutineIds.isEmpty()) {
            availableRoutines = routineRepository.findByRoutineTemplateType(user.getUserType());
        } else {
            availableRoutines = routineRepository.findByRoutineTemplateTypeAndIdNotIn(user.getUserType(), userRoutineIds);
        }

        // 3. 조회된 루틴 목록을 DTO 리스트로 변환
        List<RoutineAddResponseDto.AvailableRoutineDto> dtoList = availableRoutines.stream()
                .map(RoutineAddResponseDto.AvailableRoutineDto::from)
                .collect(Collectors.toList());

        // 4. 최종 DTO 객체로 감싸서 반환
        return RoutineAddResponseDto.builder()
                .routines(dtoList)
                .build();
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));
    }

    private Routine findRoutineById(Long routineId) {
        return routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 루틴을 찾을 수 없습니다."));
    }

    private UserRoutine findUserRoutine(User user, Routine routine) {
        return userRoutineRepository.findByUserAndRoutine(user, routine)
                .orElseThrow(() -> new EntityNotFoundException("사용자에게 해당 루틴이 존재하지 않습니다."));
    }
}