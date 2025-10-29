package com.example.weathertrace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.example.weathertrace.ui.screens.main.MainScreen
import com.example.weathertrace.ui.screens.main.MainViewModel
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.example.weathertrace.domain.model.City
//import com.example.weathertrace.domain.model.DailyWeatherModel
//import com.example.weathertrace.domain.repository.CityRepository
//import com.example.weathertrace.domain.repository.WeatherRepository
//import kotlinx.coroutines.launch
//import java.time.LocalDate

class MainActivity : ComponentActivity() {

//    private val cityRepository = CityRepository(devMode = BuildConfig.DEV_MODE)
//    private val weatherRepository = WeatherRepository(devMode = BuildConfig.DEV_MODE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (BuildConfig.DEV_MODE) {
            println("⚙️ Dev Mode activated: using mocked data")
        }

        val viewModel = MainViewModel(devMode = BuildConfig.DEV_MODE)

        setContent {
            MaterialTheme {
                MainScreen(viewModel = viewModel)
            }
        }

//        setContent {
//            MaterialTheme {
//                val scope = rememberCoroutineScope()
//
//                var cityQuery by remember { mutableStateOf("") }
//                var citySuggestions by remember { mutableStateOf(listOf<City>()) }
//                var selectedCity by remember { mutableStateOf<City?>(null) }
//                var weatherData by remember { mutableStateOf(listOf<DailyWeatherModel>()) }
//                var loading by remember { mutableStateOf(false) }
//                var errorMessage by remember { mutableStateOf<String?>(null) }
//
//                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//
//                    // --- Search Bar ---
//                    OutlinedTextField(
//                        value = cityQuery,
//                        onValueChange = { cityQuery = it },
//                        label = { Text("Search city") },
//                        modifier = Modifier.fillMaxWidth()
//                    )
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    // --- Search Button aligned to the right ---
//                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
//                        Button(
//                            onClick = {
//                                if (cityQuery.isNotBlank()) {
//                                    scope.launch {
//                                        loading = true
//                                        try {
//                                            citySuggestions = cityRepository.searchCities(cityQuery, limit = 5)
//                                            errorMessage = null
//                                        } catch (e: Exception) {
//                                            e.printStackTrace()
//                                            citySuggestions = emptyList()
//                                            errorMessage = "Error searching cities: ${e.message}"
//                                        } finally {
//                                            loading = false
//                                        }
//                                    }
//                                }
//                            }
//                        ) {
//                            Text("Search")
//                        }
//                    }
//
//                    Spacer(modifier = Modifier.height(8.dp))
//
//                    // --- Autocomplete suggestions ---
//                    LazyColumn(
//                        modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)
//                    ) {
//                        items(citySuggestions) { city ->
//                            Text(
//                                text = city.name,
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .clickable {
//                                        selectedCity = city
//                                        cityQuery = city.name
//                                        citySuggestions = emptyList()
//
//                                        // Load weather for selected city
//                                        scope.launch {
//                                            loading = true
//                                            try {
//                                                weatherData = weatherRepository.getHistoricalDailyWeathers(
//                                                    lat = city.lat,
//                                                    lon = city.lon,
//                                                    baseDate = LocalDate.now(),
//                                                    apiKey = BuildConfig.OPENWEATHER_API_KEY,
//                                                    units = "metric",
//                                                    lang = "en"
//                                                )
//                                                errorMessage = null
//                                            } catch (e: Exception) {
//                                                e.printStackTrace()
//                                                weatherData = emptyList()
//                                                errorMessage = "Error fetching weather: ${e.message}"
//                                            } finally {
//                                                loading = false
//                                            }
//                                        }
//                                    }
//                                    .padding(8.dp)
//                            )
//                            Divider()
//                        }
//                    }
//
//                    Spacer(modifier = Modifier.height(16.dp))
//
//                    // --- Loading / Error ---
//                    if (loading) {
//                        Text("Loading...", style = MaterialTheme.typography.bodyLarge)
//                    }
//                    errorMessage?.let { Text("❌ $it", color = MaterialTheme.colorScheme.error) }
//
//                    // --- Weather display ---
//                    LazyColumn(modifier = Modifier.fillMaxSize()) {
//                        items(weatherData) { weather ->
//                            Column(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(8.dp)
//                            ) {
//                                Text("✅ Weather Data:")
//                                Text("Date: ${weather.date}")
//                                Text("Temp: ${weather.temperature.afternoon}°C")
//                                Text("Humidity: ${weather.humidity.afternoon}%")
//                                Text("Cloud Cover: ${weather.cloudCover.afternoon}%")
//                                Text("Precipitation: ${weather.precipitation.total} mm")
//                                Text("Wind: ${weather.wind.max.speed} km/h (${weather.wind.max.direction}°)")
//                                Divider(modifier = Modifier.padding(vertical = 4.dp))
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }
}