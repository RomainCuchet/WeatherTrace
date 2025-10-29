package com.example.weathertrace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weathertrace.domain.repository.WeatherRepository
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate

class MainActivity : ComponentActivity() {

    private val repository = WeatherRepository(devMode = BuildConfig.DEV_MODE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (BuildConfig.DEV_MODE){
            println("⚙️ Dev Mod activated : using mocked data")
        }

        setContent {
            MaterialTheme {
                var weatherText by remember { mutableStateOf("Loading historical weather...") }

                LaunchedEffect(Unit) {
                    try {
                        // Get a list of historical weather entries
                        val historicalWeathers = repository.getHistoricalDailyWeathers(
                            lat = 48.8566,
                            lon = 2.3522,
                            baseDate = LocalDate.now(),
                            apiKey = BuildConfig.OPENWEATHER_API_KEY,
                            units = "metric",
                            lang = "en" // English
                        )

                        // Build a display string for all entries
                        weatherText = historicalWeathers.joinToString(separator = "\n\n") { weather ->
                            """
                            ✅ Weather Data:
                            --------------------
                            Date: ${weather.date}
                            Temp: ${weather.temperature.afternoon}°C
                            Humidity: ${weather.humidity.afternoon}%
                            Cloud Cover: ${weather.cloudCover.afternoon}%
                            Precipitation: ${weather.precipitation.total} mm
                            Wind: ${weather.wind.max.speed} km/h (${weather.wind.max.direction}°)
                            """.trimIndent()
                        }

                        println(weatherText)

                    } catch (e: HttpException) {
                        val errorCode = e.code()
                        val errorBody = e.response()?.errorBody()?.string()
                        weatherText = "❌ API Error ($errorCode): ${errorBody ?: e.message()}"
                        println(weatherText)
                        e.printStackTrace()

                    } catch (e: IOException) {
                        weatherText = "⚠️ Network Error: ${e.message}"
                        println(weatherText)
                        e.printStackTrace()

                    } catch (e: Exception) {
                        weatherText = "❗Unknown Error: ${e.message}"
                        println(weatherText)
                        e.printStackTrace()
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(text = weatherText)
                }
            }
        }
    }
}
