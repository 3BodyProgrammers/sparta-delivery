package com.example.spartadelivery.common.aop;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.user.enums.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class UserAccessAspect {

    private final HttpServletRequest request;

    @Around("@annotation(com.example.spartadelivery.common.annotation.User)")
    public Object userApiAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        UserRole userRole = UserRole.of((String) request.getAttribute("userRole"));

        if (!UserRole.USER.equals(userRole)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "일반 사용자 권한이 필요합니다.");
        }
        return joinPoint.proceed();
    }
}
