package com.example.weathertrace.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathertrace.domain.model.City
import com.example.weathertrace.domain.repository.CityRepository
import com.example.weathertrace.domain.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    devMode: Boolean
) : ViewModel() {

    private val cityRepository = CityRepository(devMode = devMode)
    private val weatherRepository = WeatherRepository(devMode = devMode)

//    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
//    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    // City search results
    private val _searchResults = MutableStateFlow<List<City>>(emptyList())
    val searchResults: StateFlow<List<City>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var lastSearchTime = 0L
    private var searchJob: Job? = null

    /**
     * Search for cities with minimum 1 second delay between requests
     */
    fun searchCities(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            val now = System.currentTimeMillis()
            val timeSinceLastSearch = now - lastSearchTime
            if (timeSinceLastSearch < 1000) {
                delay(1000 - timeSinceLastSearch)
            }

            _isSearching.value = true
            lastSearchTime = System.currentTimeMillis()

            try {
                val results = cityRepository.searchCities(query, limit = 5)
                _searchResults.value = results
            } catch (e: Exception) {
                e.printStackTrace()
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }


}
