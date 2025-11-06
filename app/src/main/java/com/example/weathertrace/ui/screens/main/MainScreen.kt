package com.example.weathertrace.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import com.example.weathertrace.ui.components.SearchTopBar
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import com.example.weathertrace.ui.components.WeatherChart
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    val currentCity = viewModel.currentCity.collectAsState()

    Scaffold(
        topBar = { SearchTopBar(viewModel = viewModel) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp) // padding interne
        ) {
            // Texte en haut
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = currentCity.value?.name ?: "Hello WeatherTrace!",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (currentCity.value != null) {
                    Text(
                        text = "${currentCity.value?.lat}, ${currentCity.value?.lon}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            WeatherChart()
        }
    }
}