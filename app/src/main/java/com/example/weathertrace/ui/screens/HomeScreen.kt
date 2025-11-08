package com.example.weathertrace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.example.weathertrace.viewModel.MainViewModel
import com.example.weathertrace.ui.components.SearchTopBar
import com.example.weathertrace.ui.components.WeatherChart

@Composable
fun HomeScreen(viewModel: MainViewModel, navController: NavController) {
    val currentCity by viewModel.currentCity.collectAsState()

    Scaffold(
        topBar = { SearchTopBar(viewModel = viewModel, navController = navController) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Current city display
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = currentCity?.name ?: "Hello WeatherTrace!",
                    style = MaterialTheme.typography.bodyMedium
                )
                currentCity?.let {
                    Text(
                        text = "${it.lat}, ${it.lon}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Weather chart
            WeatherChart(viewModel)
        }
    }
}