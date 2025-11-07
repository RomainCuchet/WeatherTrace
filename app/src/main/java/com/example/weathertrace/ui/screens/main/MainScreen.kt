package com.example.weathertrace.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable

import com.example.weathertrace.ui.components.SearchTopBar
import com.example.weathertrace.ui.components.WeatherChart
import com.example.weathertrace.ui.components.ReadMeDocScreen




@Composable
fun MainScreen(viewModel: MainViewModel) {

    val navController = rememberNavController()
    val currentCity by viewModel.currentCity.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
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
                    // Current city text
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

                    // Weather Graph
                    WeatherChart(viewModel)
                }
            }
        }

        // ReadMeDocScreen
        composable("readmeScreen") {
            ReadMeDocScreen(navController)
        }
        // SettingsScreen
        composable("settingsScreen"){
            SettingsScreen(viewModel,navController)
        }
    }
}
