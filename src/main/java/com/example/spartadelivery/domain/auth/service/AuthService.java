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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponseDto signup(SignupRequestDto requestDto) {

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        UserRole userRole = UserRole.of(requestDto.getUserRole());

        User newUser = new User(
                requestDto.getEmail(),
                encodedPassword,
                requestDto.getName(),
                userRole
        );

        User savedUser = userRepository.save(newUser);

        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), savedUser.getName(), userRole);

        return new SignupResponseDto(bearerToken);
    }

    @Transactional(readOnly = true)
    public SigninResponseDto signin(SigninRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(
                () -> new CustomException(HttpStatus.BAD_REQUEST, "가입되지 않은 유저입니다.")
        );

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "잘못된 비밀번호입니다.");
        }

        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getName(), user.getUserRole());

        return new SigninResponseDto(bearerToken);
    }
}
