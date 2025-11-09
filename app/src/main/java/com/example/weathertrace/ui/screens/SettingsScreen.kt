package com.example.weathertrace.ui.screens

import android.os.Build

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalConfiguration

import java.util.Locale

import com.example.weathertrace.ui.components.ComeBackArrow
import com.example.weathertrace.viewModel.MainViewModel
import com.example.weathertrace.R

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    navController: NavController
) {
    val currentUnit = viewModel.currentTemperatureUnit.collectAsState()
    val configuration = LocalConfiguration.current

    // Récupère la langue actuelle du système
    val systemLanguage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        configuration.locales.get(0)?.displayName ?: Locale.getDefault().displayName
    } else {
        configuration.locale?.displayName ?: Locale.getDefault().displayName
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 16.dp)
    ) {
        ComeBackArrow(
            title = stringResource(R.string.settings_screen_title),
            navController = navController
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ---- TEMPERATURE SECTION ----
        Text(
            text = stringResource(R.string.settings_temperature_unit),
            style = MaterialTheme.typography.titleMedium
        )

        val units = listOf("C" to "Celsius (°C)", "F" to "Fahrenheit (°F)")
        units.forEach { (value, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
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

        Spacer(modifier = Modifier.height(24.dp))

        // ---- LANGUAGE SECTION ----
        Text(
            text = stringResource(R.string.settings_language),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${stringResource(R.string.language_system)}: $systemLanguage",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}