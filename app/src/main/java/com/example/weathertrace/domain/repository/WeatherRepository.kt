package com.example.weathertrace.domain.repository

import com.example.weathertrace.BuildConfig
import com.example.weathertrace.data.mapper.toDomain
import com.example.weathertrace.data.remote.weather.api.openWeatherApiClient
import com.example.weathertrace.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlin.math.min
import kotlin.random.Random
import java.time.LocalDate

class WeatherRepository(
    private val devMode: Boolean = true
) {

    private val apiService = openWeatherApiClient.openWeatherService

    /**
     * Retrieve historical daily weather for the past N years from baseDate in parallel.
     */
    suspend fun getHistoricalDailyWeathers(
        lat: Double,
        lon: Double,
        baseDate: LocalDate,
        apiKey: String,
        units: String? = null,
        lang: String? = null
    ): List<DailyWeatherModel> = withContext(Dispatchers.IO) {
        val count = min(BuildConfig.PAST_YEARS_TO_FETCH_COUNT, BuildConfig.PAST_YEARS_TO_FETCH_COUNT_MAX)
        if (count <= 0) return@withContext emptyList()

        // Coroutine scope to launch several calls in parallel
        coroutineScope {
            val deferredList = (0 until count).map { i ->
                async {
                    val date = baseDate.minusYears(i.toLong())
                    getDailyWeather(lat,lon,date,apiKey,units,lang)
                }
            }
            // Wait for results
            deferredList.map { it.await() }
        }
    }

    /**
     * Retrieve a single daily weather entry.
     */
    suspend fun getDailyWeather(
        lat: Double,
        lon: Double,
        date: LocalDate,
        apiKey: String,
        units: String? = null,
        lang: String? = null
    ): DailyWeatherModel = withContext(Dispatchers.IO) {
        if (devMode) return@withContext getMockDailyWeather(date)
        apiService.getDailyWeather(lat, lon, date, apiKey, units, lang).toDomain()
    }

    /**
     * Generate mock data for development.
     */
    private fun getMockDailyWeather(date: LocalDate): DailyWeatherModel {
        val minTemp = Random.nextDouble(253.15, 283.15)
        val maxTemp = Random.nextDouble(minTemp + 1, minTemp + 15)
        val morningTemp = Random.nextDouble(minTemp, maxTemp)
        val afternoonTemp = Random.nextDouble(minTemp, maxTemp)
        val eveningTemp = Random.nextDouble(minTemp, maxTemp)
        val nightTemp = Random.nextDouble(minTemp, maxTemp)

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