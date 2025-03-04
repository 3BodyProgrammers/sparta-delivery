package com.example.spartadelivery.config;

import com.example.spartadelivery.domain.holiday.enums.Holiday;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Converter(autoApply = true)
public class HolidayConverter {

    public Integer convertToInteger(List<Holiday> holidays) {
        return holidays.stream().mapToInt(Holiday::getValue).reduce(0, (a, b) -> a | b);
    }

    public List<Holiday> convertToHoliday(Integer holiday) {
        return holiday == 0 ? new ArrayList<Holiday>() : Arrays.stream(Holiday.values()).filter(h -> (holiday & h.getValue()) != 0).collect(Collectors.toList());
    }
}
