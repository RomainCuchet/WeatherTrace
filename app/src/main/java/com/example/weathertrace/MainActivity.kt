package com.example.weathertrace
import com.example.weathertrace.BuildConfig
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
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainActivity : ComponentActivity() {

    private val repository = WeatherRepository(devMode = false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                var weatherText by remember { mutableStateOf("Loading...") }

                LaunchedEffect(Unit) {
                    try {
                        val weather = repository.getDailyWeather(
                            lat = 48.8566,
                            lon = 2.3522,
                            date = "2025-10-28",
                            apiKey = BuildConfig.OPENWEATHER_API_KEY,
                            units = "metric",
                            lang = "fr"
                        )

                        weatherText = """
                            ✅ Données météo :
                            --------------------
                            Date: ${weather.date}
                            Temp: ${weather.temperature.afternoon}°C
                            Humidité: ${weather.humidity.afternoon}%
                            Nuages: ${weather.cloudCover.afternoon}%
                            Précipitations: ${weather.precipitation.total} mm
                            Vent: ${weather.wind.max.speed} km/h (${weather.wind.max.direction}°)
                        """.trimIndent()

                        println(weatherText)

                    } catch (e: HttpException) {
                        val errorCode = e.code()
                        val errorBody = e.response()?.errorBody()?.string()
                        weatherText = "❌ Erreur API ($errorCode) : ${errorBody ?: e.message()}"
                        println(weatherText)
                        e.printStackTrace()

                    } catch (e: IOException) {
                        weatherText = "⚠️ Erreur réseau : ${e.message}"
                        println(weatherText)
                        e.printStackTrace()

                    } catch (e: Exception) {
                        weatherText = "❗Erreur inconnue : ${e.message}"
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
