package com.heybro.heybro.routine.service;

import com.heybro.heybro.routine.domain.DailyRoutineLog;
import com.heybro.heybro.routine.domain.Routine;
import com.heybro.heybro.routine.domain.RoutineElement;
import com.heybro.heybro.routine.repository.DailyRoutineLogRepository;
import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.domain.UserRoutine;
import com.heybro.heybro.user.domain.UserRoutineSchedule;
import com.heybro.heybro.user.dto.response.UserRoutineResponseDto;
import com.heybro.heybro.user.repository.UserRepository;
import com.heybro.heybro.user.repository.UserRoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoutineServiceImpl implements RoutineService {
    private final UserRepository userRepository;
    private final UserRoutineRepository userRoutineRepository;
    private final DailyRoutineLogRepository dailyRoutineLogRepository;

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
                    .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

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

                // (5) Routine에 연결된 Element와 Tip을 DTO 리스트로 변환 (반복문 밖에서 한 번만 수행)
                List<UserRoutineResponseDto.RoutineElementResponseDto> elementDtos = routine.getElementList().stream()
                        .sorted(Comparator.comparing(RoutineElement::getStep))
                        .map(UserRoutineResponseDto.RoutineElementResponseDto::from)
                        .collect(Collectors.toList());

                List<UserRoutineResponseDto.RoutineTipResponseDto> tipDtos = routine.getTipList().stream()
                        .map(UserRoutineResponseDto.RoutineTipResponseDto::from)
                        .collect(Collectors.toList());

                // (6) 필터링된 각 스케줄에 대해 최종 RoutineResponseDto를 만들어 리스트에 추가
                for (UserRoutineSchedule schedule : filteredSchedules) {
                    UserRoutineResponseDto.RoutineResponseDto routineDto = UserRoutineResponseDto.RoutineResponseDto.builder()
                            .scheduleTime(schedule.getScheduleTime())
                            .routineName(routine.getName())
                            .iconImage(routine.getIconImage())
                            .timeOfDay(routine.getTimeOfDay())
                            .completed(false) // 미래 날짜이므로 항상 false
                            .elements(elementDtos)
                            .tips(tipDtos)
                            .build();
                    routineResponseDtoList.add(routineDto);
                }
            }

            // TimeOfDay순으로 먼저 정렬 후 scheduleTime 순으로 정렬
            routineResponseDtoList.sort(Comparator.comparing(UserRoutineResponseDto.RoutineResponseDto::getTimeOfDay)
                    .thenComparing(UserRoutineResponseDto.RoutineResponseDto::getScheduleTime));

            // (7) 최종적으로 UserRoutineResponseDto로 감싸서 반환
            return UserRoutineResponseDto.builder()
                    .routines(routineResponseDtoList)
                    .build();
        } else { // 과거~오늘까지 조회
            // (1) user 조회
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

            // (2) 해당 날짜의 모든 로그를 DailyRoutineLog에서 조회
            List<DailyRoutineLog> logs = dailyRoutineLogRepository.findAllByUserAndTaskDate(user, date);

            // (3) 로그를 DTO로 변환
            List<UserRoutineResponseDto.RoutineResponseDto> routineResponseDtoList = logs.stream()
                    .map(log -> {
                        Routine routine = log.getRoutine();

                        List<UserRoutineResponseDto.RoutineElementResponseDto> elementDtos = routine.getElementList().stream()
                                .sorted(Comparator.comparing(RoutineElement::getStep))
                                .map(UserRoutineResponseDto.RoutineElementResponseDto::from)
                                .collect(Collectors.toList());

                        List<UserRoutineResponseDto.RoutineTipResponseDto> tipDtos = routine.getTipList().stream()
                                .map(UserRoutineResponseDto.RoutineTipResponseDto::from)
                                .collect(Collectors.toList());

                        return UserRoutineResponseDto.RoutineResponseDto.builder()
                                .scheduleTime(log.getScheduledTime())
                                .routineName(routine.getName())
                                .iconImage(routine.getIconImage())
                                .completed(log.isCompleted()) // 로그에 기록된 실제 완료 여부
                                .elements(elementDtos)
                                .tips(tipDtos)
                                .build();
                    })
                    .collect(Collectors.toList());

            // TimeOfDay, scheduleTime 순으로 정렬
            routineResponseDtoList.sort(Comparator.comparing(UserRoutineResponseDto.RoutineResponseDto::getTimeOfDay)
                    .thenComparing(UserRoutineResponseDto.RoutineResponseDto::getScheduleTime));

            return UserRoutineResponseDto.builder()
                    .routines(routineResponseDtoList)
                    .build();
        }
    }
}
