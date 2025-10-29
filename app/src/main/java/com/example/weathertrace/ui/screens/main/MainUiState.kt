package com.example.weathertrace.ui.screens.main

import com.example.weathertrace.domain.model.DailyWeatherModel

sealed class MainUiState {
    data object Loading : MainUiState()
    data class Success(val weatherData: List<DailyWeatherModel>) : MainUiState()
    data class Error(val message: String) : MainUiState()
}
