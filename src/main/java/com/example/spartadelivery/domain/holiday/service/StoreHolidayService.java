package com.example.spartadelivery.domain.holiday.service;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.config.HolidayConverter;
import com.example.spartadelivery.domain.holiday.dto.request.StoreHolidayRequestDto;
import com.example.spartadelivery.domain.store.dto.response.StoreResponseDto;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreHolidayService {

    private final StoreRepository storeRepository;
    private final HolidayConverter holidayConverter;

    public StoreResponseDto updateHolidays(Long id, Long userId, String userRole, StoreHolidayRequestDto request) {
        //TODO : AOP 구현 이후 수정
        if (!isOwner(userRole)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "가게 휴일 수정은 사장님만 가능합니다.");
        }

        //TODO : 유저 구현 이후 유저 정보도 같이 가져옴
        Store findStore = storeRepository.findById(id).orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "해당 가게는 존재하지 않습니다."));

        if (!findStore.getUserId().equals(userId)) {
            throw new CustomException(HttpStatus.FORBIDDEN, "가게 휴일 수정은 가게의 사장님만 가능 합니다.");
        }

        Integer holiday = holidayConverter.convertToInteger(request.getHolidays());
        findStore.updateHolidays(holiday);

        return StoreResponseDto.of(findStore, request.getHolidays());
    }

    public boolean isOwner(String userRole) {
        return "OWNER".equals(userRole);
    }
}
