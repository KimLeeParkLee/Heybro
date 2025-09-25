package com.heybro.heybro.user.dto;

import com.heybro.heybro.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRankDto {
    private User user;
    private int rank;
}
