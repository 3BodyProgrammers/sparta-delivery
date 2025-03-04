package com.example.spartadelivery.domain.auth.controller;

import com.example.spartadelivery.domain.auth.dto.request.SigninRequestDto;
import com.example.spartadelivery.domain.auth.dto.request.SignupRequestDto;
import com.example.spartadelivery.domain.auth.dto.response.SigninResponseDto;
import com.example.spartadelivery.domain.auth.dto.response.SignupResponseDto;
import com.example.spartadelivery.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auths")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@Valid @RequestBody SignupRequestDto signupRequest) {
        return ResponseEntity.ok(authService.signup(signupRequest));
    }

    @PostMapping("/signin")
    public ResponseEntity<SigninResponseDto> signin(@Valid @RequestBody SigninRequestDto signinRequest) {
        return ResponseEntity.ok(authService.signin(signinRequest));
    }
}
