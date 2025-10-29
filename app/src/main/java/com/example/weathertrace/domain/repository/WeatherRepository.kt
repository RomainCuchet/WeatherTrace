package com.example.weathertrace.domain.repository

import android.graphics.Bitmap
import com.example.weathertrace.BuildConfig
import com.example.weathertrace.data.mapper.toDomain
import com.example.weathertrace.data.remote.weather.api.openWeatherApiClient
import com.example.weathertrace.domain.model.*
import kotlin.math.min

import kotlin.random.Random
import java.time.LocalDate

class WeatherRepository(
    private val devMode: Boolean = true
) {

    private val apiService = openWeatherApiClient.openWeatherService

    suspend fun getHistoricalDailyWeathers(
        lat: Double,
        lon: Double,
        baseDate: LocalDate,
        apiKey: String,
        units: String? = null,
        lang: String? = null
    ): List<DailyWeatherModel> =
        List(
            min(BuildConfig.PAST_YEARS_TO_FETCH_COUNT, BuildConfig.PAST_YEARS_TO_FETCH_COUNT_MAX)
        ) { i ->
            val date = baseDate.minusYears(i.toLong())
            if (devMode) {
                getMockDailyWeather(date)
            } else {
                apiService.getDailyWeather(lat, lon, date, apiKey, units, lang).toDomain()
            }
        }

    suspend fun getDailyWeather(
        lat: Double,
        lon: Double,
        date: LocalDate,
        apiKey: String,
        units: String? = null,
        lang: String? = null
    ): DailyWeatherModel {
        return if (devMode) {
            getMockDailyWeather(date)
        } else {
            apiService.getDailyWeather(lat, lon, date, apiKey, units, lang).toDomain()
        }
    }

    private fun getMockDailyWeather(date: LocalDate): DailyWeatherModel {
        // Température
        val minTemp = Random.nextDouble(-5.0, 20.0)
        val maxTemp = Random.nextDouble(minTemp + 1, minTemp + 15)
        val morningTemp = Random.nextDouble(minTemp, maxTemp)
        val afternoonTemp = Random.nextDouble(minTemp, maxTemp)
        val eveningTemp = Random.nextDouble(minTemp, maxTemp)
        val nightTemp = Random.nextDouble(minTemp, maxTemp)

        // Vent
        val maxWindSpeed = Random.nextDouble(0.0, 25.0)
        val windDirection = Random.nextDouble(0.0, 360.0)

        return DailyWeatherModel(
            date = date,
            cloudCover = Afternoon(Random.nextDouble(0.0, 100.0)),
            humidity = Afternoon(Random.nextDouble(20.0, 100.0)),
            precipitation = Total(Random.nextDouble(0.0, 50.0)),
            pressure = Afternoon(Random.nextDouble(980.0, 1050.0)),
            temperature = Temperature(
                min = minTemp,
                max = maxTemp,
                morning = morningTemp,
                afternoon = afternoonTemp,
                evening = eveningTemp,
                night = nightTemp
            ),
            wind = Wind(
                max = MaxWind(
                    speed = maxWindSpeed,
                    direction = windDirection
                )
            )
        )
    }
}
