package com.example.spartadelivery.domain.user.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResponseDto {

    private final Long id;
    private final String email;
    private final String name;
    private final String userRole;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public UserResponseDto(Long id, String email, String name, String userRole, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.userRole = userRole;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
