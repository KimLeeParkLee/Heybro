package com.heybro.heybro.routine.repository;

import com.heybro.heybro.routine.domain.DailyRoutineLog;
import com.heybro.heybro.routine.domain.Routine;
import com.heybro.heybro.user.domain.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyRoutineLogRepository extends JpaRepository<DailyRoutineLog, Long> {
    @Query("select d from DailyRoutineLog d join fetch d.routine where d.user = :user and d.taskDate = :taskDate")
    List<DailyRoutineLog> findAllByUserAndTaskDateWithRoutine(@Param("user") User user, @Param("taskDate") LocalDate taskDate);

    List<DailyRoutineLog> findAllByUserAndTaskDate(User user, LocalDate taskDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    DailyRoutineLog findByUserAndRoutineAndTaskDate(User user, Routine routine, LocalDate taskDate);

    @Query("select d from DailyRoutineLog d join fetch d.routine where d.user = :user and d.taskDate between :startDate and :endDate")
    List<DailyRoutineLog> findAllByUserAndTaskDateBetweenWithRoutine(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    void deleteAllByUser(User user);

    // 파생 deleteAllBy 는 전부 SELECT 한 뒤 1건씩 DELETE 한다. 탈퇴·온보딩 재제출처럼 많을 수 있는 행은 벌크로 지운다.
    @Modifying
    @Query("delete from DailyRoutineLog d where d.user = :user")
    int deleteBulkByUser(@Param("user") User user);

    @Modifying
    @Query("delete from DailyRoutineLog d where d.user = :user and d.taskDate = :taskDate")
    int deleteBulkByUserAndTaskDate(@Param("user") User user, @Param("taskDate") LocalDate taskDate);

    boolean existsByUserAndTaskDate(User user, LocalDate taskDate);

    // 해당 날짜에 이미 로그가 있는 회원 id (chunk 단위로 한 번에 확인)
    @Query("select distinct d.user.id from DailyRoutineLog d where d.taskDate = :taskDate and d.user.id in :userIds")
    List<Long> findUserIdsWithLogOn(@Param("userIds") List<Long> userIds, @Param("taskDate") LocalDate taskDate);
}

