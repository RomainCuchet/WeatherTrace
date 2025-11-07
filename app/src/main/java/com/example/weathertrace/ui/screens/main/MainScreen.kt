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
            HomeScreen(viewModel, navController)
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
