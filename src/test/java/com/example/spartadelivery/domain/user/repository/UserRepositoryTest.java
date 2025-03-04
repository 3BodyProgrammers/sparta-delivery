package com.example.spartadelivery.domain.user.repository;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.user.entity.User;
import com.example.spartadelivery.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void 이메일로_사용자를_조회할_수_있다() {
        // given
        String email = "aa@aa.com";
        User user = new User(email, "Abcd1234!", "이름", UserRole.USER);
        userRepository.save(user);

        // when
        User foundUser = userRepository.findByEmail(email).orElse(null);

        // then
        assertNotNull(foundUser);
        assertEquals(email, foundUser.getEmail());
    }

    @Test
    void 존재하지_않는_User를_조회_시_예외를_던진다() {
        // given
        long invalidUserId = 999L;

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> userRepository.findByIdOrElseThrow(invalidUserId));
        assertEquals("유저를 찾을 수 없습니다.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}