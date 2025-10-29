package com.example.weathertrace.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import com.example.weathertrace.ui.components.SearchTopBar

@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    val currentCity = viewModel.currentCity.collectAsState()

    Scaffold(
        topBar = { SearchTopBar(viewModel = viewModel) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {

            Column {
                Text(
                    text = currentCity.value?.name ?: "Hello WeatherTrace!",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (currentCity.value != null) {
                    Text(
                        text = "${currentCity.value?.lat }, ${currentCity.value?.lon}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
