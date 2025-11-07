package com.example.weathertrace.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.safeDrawingPadding
import com.example.weathertrace.ui.components.ComeBackArrow


import com.example.weathertrace.ui.screens.main.MainViewModel


@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    navController: NavController
) {
    val currentUnit = viewModel.currentTemperatureUnit.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 16.dp)
    ) {
        ComeBackArrow(title = "Settings", navController = navController)

        Text("Temperature Unit", style = MaterialTheme.typography.titleMedium)

        // Options Celsius / Fahrenheit
        val units = listOf("C" to "Celsius (°C)", "F" to "Fahrenheit (°F)")
        units.forEach { (value, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = currentUnit.value == value,
                        onClick = { viewModel.setTemperatureUnit(value) }
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = currentUnit.value == value,
                    onClick = { viewModel.setTemperatureUnit(value) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(label, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}