package com.heybro.heybro.user.repository;

import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.domain.UserRoutineSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface UserRoutineScheduleRepository extends JpaRepository<UserRoutineSchedule, Long> {
    List<UserRoutineSchedule> findAllByDayOfWeekEqualsAndScheduleTimeEquals(DayOfWeek dayOfWeek, LocalTime time);

    // 회원의 모든 루틴 스케줄을 벌크 삭제 (UserRoutine 을 지우기 전에 호출)
    @Modifying
    @Query("delete from UserRoutineSchedule s where s.userRoutine.id in (select ur.id from UserRoutine ur where ur.user = :user)")
    int deleteBulkByUser(@Param("user") User user);

    // 자정 로그 생성용: 회원 묶음(chunk)의 해당 요일 스케줄을 루틴과 함께 한 번에 조회 (회원별 반복 조회 제거)
    @Query("select s from UserRoutineSchedule s " +
            "join fetch s.userRoutine ur " +
            "join fetch ur.routine " +
            "where ur.user.id in :userIds and s.dayOfWeek = :dayOfWeek")
    List<UserRoutineSchedule> findAllByUserIdsAndDayOfWeek(@Param("userIds") List<Long> userIds, @Param("dayOfWeek") DayOfWeek dayOfWeek);


    // 알림 발송 대상: 회원·루틴까지 한 번에 가져와 스케줄러에서 지연 로딩(세션 없음)과 N+1 이 생기지 않게 한다.
    // 토큰이 없는 회원은 보낼 수 없으므로 DB 에서 걸러 전송 루프를 줄인다.
    @Query("select s from UserRoutineSchedule s " +
            "join fetch s.userRoutine ur " +
            "join fetch ur.user u " +
            "join fetch ur.routine r " +
            "where s.dayOfWeek = :dayOfWeek and s.scheduleTime = :time " +
            "and u.notificationToken is not null and u.notificationToken <> ''")
    List<UserRoutineSchedule> findNotificationTargets(@Param("dayOfWeek") DayOfWeek dayOfWeek, @Param("time") LocalTime time);
}
