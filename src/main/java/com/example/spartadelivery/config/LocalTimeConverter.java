package com.example.spartadelivery.config;

import jakarta.persistence.Converter;
import java.time.LocalTime;
import org.springframework.stereotype.Component;

@Component
@Converter(autoApply = true)
public class LocalTimeConverter {

    public static LocalTime toLocalTime(String time) {
        return LocalTime.parse(time);
    }

}
