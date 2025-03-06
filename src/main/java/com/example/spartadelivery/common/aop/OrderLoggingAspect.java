package com.example.spartadelivery.common.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OrderLoggingAspect {

    private final HttpServletRequest request;
    private final ObjectMapper objectMapper;

    @Around("@annotation(com.example.spartadelivery.common.annotation.Order)")
    public Object ownerApiAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        Long userId = (Long) request.getAttribute("userId");
        String url = request.getRequestURI();
        String requestBody = objectMapper.writeValueAsString(joinPoint.getArgs());
        log.info("AOP - Order API Request: userId={}, Timestamp={}, URL={}, RequestBody={}",
                userId, System.currentTimeMillis(), url, requestBody);
        Object result = joinPoint.proceed();

        String responseBody = objectMapper.writeValueAsString(result);
        log.info("AOP - Order API Response: userId={}, Timestamp={}, URL={}, ResponseBody={}",
                userId, System.currentTimeMillis(), url, responseBody);
        return result;
    }
}
