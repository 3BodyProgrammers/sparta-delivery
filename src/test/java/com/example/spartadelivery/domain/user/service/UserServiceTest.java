package com.example.spartadelivery.domain.user.service;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.config.PasswordEncoder;
import com.example.spartadelivery.domain.user.dto.request.UserDeleteRequestDto;
import com.example.spartadelivery.domain.user.dto.request.UserUpdateRequestDto;
import com.example.spartadelivery.domain.user.dto.response.UserResponseDto;
import com.example.spartadelivery.domain.user.entity.User;
import com.example.spartadelivery.domain.user.enums.UserRole;
import com.example.spartadelivery.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void User를_ID로_조회할_수_있다() {
        // given
        long userId = 1L;
        String email = "aa@aa.com";
        User user = new User(email, "Abcd1234!", "이름", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        UserResponseDto userResponse = userService.getUser(userId);

        // then
        assertNotNull(userResponse);
        assertEquals(userId, userResponse.getId());
        assertEquals(email, userResponse.getEmail());
    }

    @Test
    void 존재하지_않는_User를_조회_시_예외를_던진다() {
        // given
        long invalidUserId = 999L;
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.getUser(invalidUserId));
        assertEquals("유저를 찾을 수 없습니다.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void User를_수정할_수_있다() {
        // given
        long userId = 1L;
        String oldPassword = "OldPassword12!";
        String newPassword = "NewPassword12!";
        String newName = "새이름";

        User user = new User("aa@aa.com", oldPassword, "이름", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);
        UserUpdateRequestDto requestDto = new UserUpdateRequestDto(oldPassword, newPassword, newName);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(oldPassword, user.getPassword())).willReturn(true);
        given(passwordEncoder.matches(newPassword, user.getPassword())).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        UserResponseDto responseDto = userService.updateProfile(userId, requestDto);

        // then
        assertNotNull(responseDto);
        assertEquals(userId, responseDto.getId());
        assertEquals("aa@aa.com", responseDto.getEmail());
        assertEquals(newName, responseDto.getName());
    }

    @Test
    void 새_비밀번호가_기존_비밀번호와_같으면_예외를_던진다() {
        // given
        long userId = 1L;
        String oldPassword = "OldPassword12!";
        String newPassword = "NewPassword12!";

        User user = new User("aa@aa.com", oldPassword, "이름", UserRole.USER);
        UserUpdateRequestDto requestDto = new UserUpdateRequestDto(oldPassword, newPassword, "새이름");

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> userService.updateProfile(userId, requestDto));
        assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());
    }

    @Test
    void 기존_비밀번호가_일치하지_않으면_예외를_던진다() {
        // given
        long userId = 1L;
        String oldPassword = "OldPassword12!";
        String newPassword = "NewPassword12!";

        User user = new User("aa@aa.com", oldPassword, "이름", UserRole.USER);
        UserUpdateRequestDto requestDto = new UserUpdateRequestDto(oldPassword, newPassword, "새이름");

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> userService.updateProfile(userId, requestDto));
        assertEquals("기존 비밀번호와 일치하지 않습니다.", exception.getMessage());
    }

    @Test
    void 존재하지_않는_User는_예외를_던진다() {
        // given
        long invalidUserId = 999L;
        UserUpdateRequestDto requestDto = new UserUpdateRequestDto("OldPassword12!", "NewPassword12!", "새이름");

        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> userService.updateProfile(invalidUserId, requestDto));
        assertEquals("유저를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void User를_삭제할_수_있다() {
        // given
        long userId = 1L;
        String password = "Password12!";
        UserDeleteRequestDto requestDto = new UserDeleteRequestDto(password);

        User user = new User("aa@aa.com", password, "이름", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        // when
        String result = userService.deleteUser(userId, requestDto);

        // then
        assertEquals("회원 탈퇴가 완료되었습니다.", result);
    }

    @Test
    void 이미_탈퇴한_User는_예외를_던진다() {
        // given
        long userId = 1L;
        String password = "Password12!";
        UserDeleteRequestDto requestDto = new UserDeleteRequestDto(password);

        User user = new User("aa@aa.com", password, "이름", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);
        user.delete();  // 탈퇴 상태로 변경

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> userService.deleteUser(userId, requestDto));
        assertEquals("이미 탈퇴한 사용자입니다.", exception.getMessage());
    }

    @Test
    void 비밀번호가_일치하지_않으면_예외를_던진다() {
        // given
        long userId = 1L;
        String wrongPassword = "Password12!";
        UserDeleteRequestDto requestDto = new UserDeleteRequestDto(wrongPassword);

        User user = new User("aa@aa.com", "password", "이름", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> userService.deleteUser(userId, requestDto));
        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());
    }
}