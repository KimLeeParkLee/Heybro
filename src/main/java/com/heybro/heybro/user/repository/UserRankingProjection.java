package com.heybro.heybro.user.repository;

public interface UserRankingProjection {
    String getNickname();
    long getTotalBroPoint();
    String getProfileImage();
    int getRanking();
}
