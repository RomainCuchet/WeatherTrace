package com.example.weathertrace.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathertrace.BuildConfig
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

import java.time.LocalDate

import com.example.weathertrace.domain.model.TemperatureUnit
import com.example.weathertrace.domain.model.TemperatureType

class MainViewModel(
    devMode: Boolean
) : ViewModel() {

    private val cityRepository = CityRepository(devMode = devMode)
    private val weatherRepository = WeatherRepository(devMode = devMode)

    private val _searchResultsCity = MutableStateFlow<List<City>>(emptyList())
    val searchResultsCity: StateFlow<List<City>> = _searchResultsCity.asStateFlow()

    private val _currentResultsWeather = MutableStateFlow<List<DailyWeatherModel>>(emptyList())
    val currentResultsWeather: StateFlow<List<DailyWeatherModel>> = _currentResultsWeather.asStateFlow()

    private var _currentMaxTemps: List<Double> = emptyList()
    private var _currentMinTemps: List<Double> = emptyList()
    private val _currentProcessedTemps = MutableStateFlow<List<Double>>(emptyList())
    val currentProcessedTemps = _currentProcessedTemps.asStateFlow()

    private var _currentYears = MutableStateFlow<List<Int>>(emptyList())
    val currentYears = _currentYears.asStateFlow()

    private val _isSearchingCity = MutableStateFlow(false)
    val isSearchingCity: StateFlow<Boolean> = _isSearchingCity.asStateFlow()

    private val _isSearchingWeather = MutableStateFlow(false)
    val isSearchingWeather: StateFlow<Boolean> = _isSearchingWeather.asStateFlow()

    private val _isErrorSearchingCity = MutableStateFlow(false)
    val isErrorSearchingCity = _isErrorSearchingCity.asStateFlow()
    private val _isErrorFetchingWeather = MutableStateFlow(false)
    val isErrorFetchingWeather = _isErrorFetchingWeather.asStateFlow()

    private var lastSearchTimeCity = 0L
    private var searchJobCity: Job? = null
    private var fetchJobWeather: Job? = null
    private val _currentCity = MutableStateFlow<City?>(null)
    val currentCity: StateFlow<City?> = _currentCity.asStateFlow()

    // TODO: Change Temperature Unit to the one corresponding to usual one for the user (location, language etc, settings etc...)
    private val _currentTemperatureUnit = MutableStateFlow<TemperatureUnit>(TemperatureUnit.F)
    val currentTemperatureUnit = _currentTemperatureUnit.asStateFlow()

    private val _currentTemperatureTypeToDisplay = MutableStateFlow(TemperatureType.MAX)
    val currentTemperatureTypeToDisplay = _currentTemperatureTypeToDisplay.asStateFlow()

    fun setTemperatureToDisplay(newType: TemperatureType) {
        if (newType != _currentTemperatureTypeToDisplay.value) {
            _currentTemperatureTypeToDisplay.value = newType
            _currentProcessedTemps.value = convertTemperatures(
                getCurrentTempsForType(newType),
                _currentTemperatureUnit.value
            )
        }
    }

    /**
     * set a new temperature unit
     *
     * @property newUnit String must be listed as an available temperature unit to be set
     * @author Romain CUCHET
     */
    fun setTemperatureUnit(newUnit: TemperatureUnit) {
        if (newUnit != _currentTemperatureUnit.value) {
            _currentTemperatureUnit.value = newUnit
            _currentProcessedTemps.value = convertTemperatures(
                getCurrentTempsForType(_currentTemperatureTypeToDisplay.value),
                _currentTemperatureUnit.value
            )
        }
    }

    fun convertTemperatures(
        temperaturesInKelvin: List<Double>,
        targetUnit: TemperatureUnit
    ): List<Double> {
        return temperaturesInKelvin.map { kelvin ->
            when (targetUnit) {
                TemperatureUnit.C -> kelvin - 273.15
                TemperatureUnit.F -> (kelvin - 273.15) * 9 / 5 + 32
            }
        }
    }

    private fun getCurrentTempsForType(type: TemperatureType): List<Double> {
        return when (type) {
            TemperatureType.MIN -> _currentMinTemps
            TemperatureType.MAX -> _currentMaxTemps
        }
    }

    /**
     * Set a new initial city.
     *
     * @property city the new initial city
     * @author Camille ESPIEUX
     */
    fun setInitialCity(city: City) {
        if (_currentCity.value != city){
            setCurrentCity(city)
        }
    }

    /**
     * Set a new current city. If the changes then we fetch its historical weather data.
     *
     * @property city the new current city
     * @author Romain CUCHET
     */
    fun setCurrentCity(city: City) {
        if(city!=_currentCity.value){
            fetchJobWeather?.cancel()
            _currentCity.value = city

            fetchJobWeather = viewModelScope.launch {
                fetchHistoricalDailyWeathers(city)
            }
        }
    }

    /**
     * Search for cities with minimum 1 second delay between requests
     * @property query the name of the city to search
     * @author Leo GUERIN
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
                _isErrorSearchingCity.value = true
            } finally {
                _isSearchingCity.value = false
            }
        }
    }

    /**
     * Fetch historical daily weathers for the given city, convert temperature unit
     * @property city gives the longitude and latitude to search for
     * @author Romain CUCHET
     */
    suspend fun fetchHistoricalDailyWeathers(city: City?) {
        city?.let {
            try {
                _isSearchingWeather.value = true
                _currentResultsWeather.value = weatherRepository.getHistoricalDailyWeathers(
                    city.lat,
                    city.lon,
                    LocalDate.now(),
                    apiKey = BuildConfig.OPENWEATHER_API_KEY
                )

                val (years, maxTemps, minTemps) = processHistoricalDailyWeathers(_currentResultsWeather.value)

                _currentYears.value = years
                _currentMaxTemps = maxTemps
                _currentMinTemps = minTemps

                // Appliquer le type de température actuellement sélectionné
                val currentType = _currentTemperatureTypeToDisplay.value
                _currentProcessedTemps.value = convertTemperatures(
                    getCurrentTempsForType(currentType),
                    _currentTemperatureUnit.value
                )

                _isErrorFetchingWeather.value = false
            } catch (e: Exception) {
                e.printStackTrace()
                _isErrorFetchingWeather.value = true
            } finally {
                _isSearchingWeather.value = false
            }
        }
    }

    /**
     * Process dailyWeathers object to match ui requirements
     *
     * @property dailyWeathers The collection of daily weather data to extract information from
     * @author Romain CUCHET
     */
    fun processHistoricalDailyWeathers(
        dailyWeathers: List<DailyWeatherModel>
    ): Triple<List<Int>, List<Double>, List<Double>> {
        val sorted = dailyWeathers.sortedBy { it.date }
        val years = sorted.map { it.date.year }
        val maxTemperatures = sorted.map { it.temperature.max }
        val minTemperatures = sorted.map { it.temperature.min }
        return Triple(years, maxTemperatures, minTemperatures)
    }

    fun clearSearchResultsCity() {
        _searchResultsCity.value = emptyList()
    }
}