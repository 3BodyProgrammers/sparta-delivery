package com.example.spartadelivery.domain.user.repository;

import com.example.spartadelivery.domain.user.entity.User;
import com.example.spartadelivery.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

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
}