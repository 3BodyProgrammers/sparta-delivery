package com.example.spartadelivery.domain.user.enums;

import com.example.spartadelivery.common.exception.CustomException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

public enum UserRole {
    USER, OWNER;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "유효하지 않은 UserRole"));
    }
}
