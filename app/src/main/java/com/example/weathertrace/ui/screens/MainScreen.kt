package com.example.weathertrace.ui.screens

import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.compose.runtime.Composable
import androidx.navigation.compose.composable
import com.example.weathertrace.viewModel.MainViewModel


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
