package com.example.spartadelivery.domain.user.repository;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    default User findByIdOrElseThrow(Long userId) {
        return findById(userId).orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "유저를 찾을 수 없습니다."));
    }

}
