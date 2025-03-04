package com.example.spartadelivery.domain.holiday.dto.request;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.holiday.enums.Holiday;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreHolidayRequestDto {

    private List<Holiday> holidays;

    //값이 유효한지 검사
    public void validate() {
        for (Holiday holiday : holidays) {
            if (holiday == null) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "올바른 휴일 값을 입력해야 합니다.");
            }
        }
    }
}
