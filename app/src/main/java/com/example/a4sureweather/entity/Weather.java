package com.example.a4sureweather.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * created by {Bennette Molepo} on {10/2/2021}.
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Weather {
    private String weekDay;
    private String iconType;
    private String temperature;
}
