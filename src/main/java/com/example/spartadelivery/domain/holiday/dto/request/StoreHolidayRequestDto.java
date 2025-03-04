package com.example.spartadelivery.domain.holiday.dto.request;

import com.example.spartadelivery.common.exception.CustomException;
import com.example.spartadelivery.domain.holiday.enums.Holiday;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreHolidayRequestDto {

    private List<String> holidays;

    public void validate() {
        for (String holiday : holidays) {
            if (holiday == null || holiday.isBlank()) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "올바른 휴일 값을 입력해야 합니다.");
            }
        }
    }
}
