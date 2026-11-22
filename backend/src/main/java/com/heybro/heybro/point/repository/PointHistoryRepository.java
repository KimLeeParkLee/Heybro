package com.heybro.heybro.point.repository;

import com.heybro.heybro.point.domain.PointHistory;
import com.heybro.heybro.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findByUserOrderByTransactionDateDesc(User user);

    @Modifying
    @Query("delete from PointHistory p where p.user = :user")
    int deleteBulkByUser(@Param("user") User user);
}
