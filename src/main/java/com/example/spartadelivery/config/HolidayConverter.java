package com.example.spartadelivery.config;

import com.example.spartadelivery.domain.holiday.enums.Holiday;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Converter(autoApply = true)
public class HolidayConverter implements AttributeConverter<List<String>, Integer> {

    @Override
    public Integer convertToDatabaseColumn(List<String> holidays) {
        return holidays.stream().mapToInt(holiday -> Holiday.valueOf(holiday).getValue()).reduce(0, (a, b) -> a | b);
    }

    @Override
    public List<String> convertToEntityAttribute(Integer holiday) {
        return holiday == 0 ? new ArrayList<>() : Arrays.stream(Holiday.values()).filter(h -> (holiday & h.getValue()) != 0).map(
                Holiday::getDay).collect(Collectors.toList());
    }


}
