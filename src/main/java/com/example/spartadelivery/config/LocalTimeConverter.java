package com.example.spartadelivery.config;


import java.time.LocalTime;

public class LocalTimeConverter {

    public static LocalTime toLocalTime(String time){
        return LocalTime.parse(time);
    }

}
