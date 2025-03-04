package com.example.spartadelivery.domain.holiday.service;

import com.example.spartadelivery.common.dto.AuthUser;
import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.config.HolidayConverter;
import com.example.spartadelivery.domain.holiday.dto.request.StoreHolidayRequestDto;
import com.example.spartadelivery.domain.holiday.enums.Holiday;
import com.example.spartadelivery.domain.store.dto.response.StoreResponseDto;
import com.example.spartadelivery.domain.store.entity.Store;
import com.example.spartadelivery.domain.store.repository.StoreRepository;
import com.example.spartadelivery.domain.user.entity.User;
import com.example.spartadelivery.domain.user.enums.UserRole;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreHolidayService {

    private final StoreRepository storeRepository;
    private final HolidayConverter holidayConverter;

    @Transactional
    public StoreResponseDto updateHolidays(Long id, AuthUser authUser, StoreHolidayRequestDto request) {
        User user = User.fromAuthUser(authUser);

        Store findStore = storeRepository.findByIdAndUserIdAndDeletedAtIsNull(id, user.getId()).orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "해당 가게는 존재하지 않습니다."));

        if (!findStore.getUser().getId().equals(user.getId())) {
            throw new CustomException(HttpStatus.FORBIDDEN, "가게 휴일 수정은 가게의 사장님만 가능 합니다.");
        }

        Integer holiday = holidayConverter.convertToDatabaseColumn(request.getHolidays());
        List<String> holidays = holidayConverter.convertToEntityAttribute(holiday);
        findStore.updateHolidays(holiday);

        return StoreResponseDto.of(findStore, holidays);
    }

    //import OrderService
    public boolean isHoliday(Store store, LocalDateTime now) {
        int holidayValue = store.getHoliday();
        int todayValue = Holiday.valueOf(now.getDayOfWeek().name()).getValue();

        return (holidayValue & todayValue) != 0;
    }
}
