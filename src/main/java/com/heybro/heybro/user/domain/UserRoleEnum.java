package com.heybro.heybro.user.domain;

import lombok.Getter;

@Getter
public enum UserRoleEnum {
    USER("ROLE_USER"),  // 사용자 권한
    ADMIN("ROLE_ADMIN");  // 관리자 권한

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }
}
