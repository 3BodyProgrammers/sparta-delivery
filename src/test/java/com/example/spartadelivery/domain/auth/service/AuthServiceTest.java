package com.example.spartadelivery.domain.auth.service;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.config.JwtUtil;
import com.example.spartadelivery.config.PasswordEncoder;
import com.example.spartadelivery.domain.auth.dto.request.SigninRequestDto;
import com.example.spartadelivery.domain.auth.dto.request.SignupRequestDto;
import com.example.spartadelivery.domain.auth.dto.response.SigninResponseDto;
import com.example.spartadelivery.domain.auth.dto.response.SignupResponseDto;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void 회원가입_성공() {
        // given
        SignupRequestDto requestDto = new SignupRequestDto("aa@aa.com", "Password12!", "이름", "USER");

        String encodedPassword = "EncodedPassword12!";
        String jwtToken = "Bearer test.jwt.token";
        User user = new User(requestDto.getEmail(), encodedPassword, requestDto.getName(), UserRole.of(requestDto.getUserRole()));
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.existsByEmail(requestDto.getEmail())).willReturn(false);
        given(passwordEncoder.encode(requestDto.getPassword())).willReturn(encodedPassword);
        given(userRepository.save(any(User.class))).willReturn(user);
        given(jwtUtil.createToken(user.getId(), user.getEmail(), user.getName(), user.getUserRole())).willReturn(jwtToken);

        // when
        SignupResponseDto responseDto = authService.signup(requestDto);

        // then
        assertNotNull(responseDto);
        assertEquals(jwtToken, responseDto.getBearerToken());
    }

    @Test
    void 이미_존재하는_이메일은_예외를_던진다() {
        // given
        SignupRequestDto requestDto = new SignupRequestDto("aa@aa.com", "Password12!", "이름", "USER");

        given(userRepository.existsByEmail(requestDto.getEmail())).willReturn(true);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> authService.signup(requestDto));
        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
    }

    @Test
    void 로그인_성공() {
        // given
        SigninRequestDto requestDto = new SigninRequestDto("aa@aa.com", "Password12!");
        String encodedPassword = "EncodedPassword12!";
        String jwtToken = "Bearer test.jwt.token";

        User user = new User(requestDto.getEmail(), encodedPassword, "이름", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findByEmail(requestDto.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).willReturn(true);
        given(jwtUtil.createToken(user.getId(), user.getEmail(), user.getName(), user.getUserRole())).willReturn(jwtToken);

        // when
        SigninResponseDto responseDto = authService.signin(requestDto);

        // then
        assertNotNull(responseDto);
        assertEquals(jwtToken, responseDto.getBearerToken());
    }

    @Test
    void 존재하지_않는_User_로그인_시_예외를_던진다() {
        // given
        SigninRequestDto requestDto = new SigninRequestDto("not_exist@aa.com", "Password12!");

        given(userRepository.findByEmail(requestDto.getEmail())).willReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> authService.signin(requestDto));
        assertEquals("가입되지 않은 유저입니다.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void 잘못된_비밀번호_입력_시_예외를_던진다() {
        // given
        SigninRequestDto requestDto = new SigninRequestDto("aa@aa.com", "WrongPassword12!");
        String encodedPassword = "EncodedPassword12!";

        User user = new User(requestDto.getEmail(), encodedPassword, "이름", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userRepository.findByEmail(requestDto.getEmail())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).willReturn(false);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> authService.signin(requestDto));
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }
}