package com.example.weathertrace.data.mapper

import com.example.weathertrace.data.remote.weather.dto.*
import com.example.weathertrace.domain.model.*
import com.example.weathertrace.domain.model.Afternoon
import com.example.weathertrace.domain.model.Total

import java.time.LocalDate


fun DailyWeatherDto.toDomain(): DailyWeatherModel {
    return DailyWeatherModel(
        date = LocalDate.parse(date) ,
        cloudCover = Afternoon(this.cloudCover.afternoon),
        humidity = Afternoon(this.humidity.afternoon),
        pressure = Afternoon(this.pressure.afternoon),
        precipitation = Total(this.precipitation.total),
        temperature = Temperature(
            min = this.temperature.min,
            max = this.temperature.max,
            afternoon = this.temperature.afternoon,
            night = this.temperature.night,
            evening = this.temperature.evening,
            morning = this.temperature.morning
        ),
        wind = Wind(
            max = MaxWind(
                speed = this.wind.max.speed,
                direction = this.wind.max.direction
            )
        )
    )
}
