package com.heybro.heybro.user.repository;

import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.domain.UserType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // 포인트 적립·사용처럼 회원 행을 수정하는 트랜잭션에서 사용
    // SELECT ... FOR UPDATE 로 행을 잠가 동시 요청에 의한 갱신 손실(lost update)을 방지
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailForUpdate(@Param("email") String email);

    @Query("SELECT u.userType FROM User u WHERE u.email = :email")
    UserType findUserTypeByEmail(String email);

    User findByNickname(String nickname);

    // TOP 10: total_bro_point 인덱스를 역순으로 10건만 읽는다 (윈도 함수를 전체 행에 적용하던 방식에서 변경).
    // 동점 시 보조 정렬(id)도 DESC 로 맞춰야 인덱스(total_bro_point, id)를 거꾸로 읽는 것만으로 끝난다 (방향이 섞이면 정렬이 필요).
    // 순위는 동점자를 같은 순위로 보는 DENSE_RANK 를 애플리케이션에서 계산한다.
    @Query(value = "SELECT u.nickname, u.total_bro_point AS totalBroPoint, u.profile_image AS profileImage " +
            "FROM user u ORDER BY u.total_bro_point DESC, u.id DESC LIMIT 10", nativeQuery = true)
    List<UserRankingProjection> findTop10UserRankingProjections();

    // 내 순위: TOP 10 과 같은 기준(누적 포인트, DENSE_RANK)으로 통일.
    // 전체 행을 정렬하지 않고 "나보다 누적 포인트가 큰 값의 개수 + 1" 을 인덱스 범위 스캔으로 구한다.
    @Query(value = "SELECT COUNT(DISTINCT u.total_bro_point) + 1 FROM user u " +
            "WHERE u.total_bro_point > (SELECT me.total_bro_point FROM user me WHERE me.email = :email)", nativeQuery = true)
    Integer findMyRanking(@Param("email") String email);
}
