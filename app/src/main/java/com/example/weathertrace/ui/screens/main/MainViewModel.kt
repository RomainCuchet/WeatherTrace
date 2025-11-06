package com.example.weathertrace.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathertrace.domain.model.City
import com.example.weathertrace.domain.model.DailyWeatherModel
import com.example.weathertrace.domain.repository.CityRepository
import com.example.weathertrace.domain.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import java.time.LocalDate
import com.example.weathertrace.BuildConfig

class MainViewModel(
    devMode: Boolean
) : ViewModel() {

    private val cityRepository = CityRepository(devMode = devMode)
    private val weatherRepository = WeatherRepository(devMode = devMode)

    private val _searchResultsCity = MutableStateFlow<List<City>>(emptyList())
    val searchResultsCity: StateFlow<List<City>> = _searchResultsCity.asStateFlow()

    private val _searchResultsWeather = MutableStateFlow<List<DailyWeatherModel>>(emptyList())
    val searchResultsWeather: StateFlow<List<DailyWeatherModel>> = _searchResultsWeather.asStateFlow()

    private val _isSearchingCity = MutableStateFlow(false)
    private val _isSearchingWeather = MutableStateFlow(false)
    val isSearchingCity: StateFlow<Boolean> = _isSearchingCity.asStateFlow()
    val isSearchingWeather: StateFlow<Boolean> = _isSearchingWeather.asStateFlow()

    private val _isErrorSearchingCity = MutableStateFlow(false)
    private val _isErrorFetchingWeather = MutableStateFlow(false)

    private var lastSearchTimeCity = 0L
    private var searchJobCity: Job? = null

    private val _currentCity = MutableStateFlow<City?>(null)
    val currentCity: StateFlow<City?> = _currentCity.asStateFlow()

    /**
     * Set current city
     */
    fun setCurrentCity(city: City) {
        _currentCity.value = city
    }

    /**
     * Search for cities with minimum 1 second delay between requests
     */
    fun searchCities(query: String) {
        searchJobCity?.cancel()

        if (query.isBlank()) {
            _searchResultsCity.value = emptyList()
            _isSearchingCity.value = false
            return
        }

        searchJobCity = viewModelScope.launch {
            val now = System.currentTimeMillis()
            val timeSinceLastSearch = now - lastSearchTimeCity
            if (timeSinceLastSearch < 1000) {
                delay(1000 - timeSinceLastSearch)
            }

            _isSearchingCity.value = true
            lastSearchTimeCity = System.currentTimeMillis()

            try {
                val results = cityRepository.searchCities(query, limit = 5)
                _searchResultsCity.value = results
                _isErrorSearchingCity.value = false
            } catch (e: Exception) {
                e.printStackTrace()
                // Keep previous results
            } finally {
                _isSearchingCity.value = false
            }
        }
    }

    /**
     * Fetch historical daily weathers for the given city
     */
    suspend fun fetchHistoricalDailyWeathers(city: City?) {
        city?.let { city ->
            try {
                _isSearchingWeather.value = true
                _searchResultsWeather.value = weatherRepository.getHistoricalDailyWeathers(
                    city.lat,
                    city.lon,
                    LocalDate.now(),
                    apiKey = BuildConfig.OPENWEATHER_API_KEY
                )
                _isErrorFetchingWeather.value = false
                println("Fetched data weather")
            } catch (e: Exception) {
                e.printStackTrace()
                _isErrorFetchingWeather.value = true
            } finally {
                _isSearchingWeather.value = false
            }
        }
    }

    fun clearSearchResults() {
        _searchResultsCity.value = emptyList()
    }
}
