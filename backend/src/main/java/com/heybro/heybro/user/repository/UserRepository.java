package com.heybro.heybro.user.repository;

import com.heybro.heybro.user.domain.User;
import com.heybro.heybro.user.domain.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u.userType FROM User u WHERE u.email = :email")
    UserType findUserTypeByEmail(String email);

    User findByNickname(String nickname);

    @Query(value = "SELECT u.nickname, u.total_bro_point as totalBroPoint, u.profile_image as profileImage, DENSE_RANK() OVER (ORDER BY u.total_bro_point DESC) as ranking " +
            "FROM user u ORDER BY u.total_bro_point DESC LIMIT 10", nativeQuery = true)
    List<UserRankingProjection> findTop10UserRankingProjections();

    @Query(value = "SELECT ranking FROM (SELECT email, RANK() OVER (ORDER BY bro_point DESC) as ranking FROM user) as ranked_users WHERE email = :email", nativeQuery = true)
    Integer findMyRanking(String email);
}
