package com.example.weathertrace.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.platform.testTag

import com.example.weathertrace.viewModel.MainViewModel
import com.example.weathertrace.ui.components.SearchTopBar
import com.example.weathertrace.ui.components.WeatherChart
import com.example.weathertrace.ui.components.WeatherCardsGrid
import com.example.weathertrace.R
import com.example.weathertrace.domain.model.TemperatureType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: MainViewModel, navController: NavController) {
    val currentCity by viewModel.currentCity.collectAsState()
    val currentTemperatureType by viewModel.currentTemperatureTypeToDisplay.collectAsState()
    val currentWeatherData by viewModel.currentResultsWeather.collectAsState()


    Scaffold(
        topBar = { SearchTopBar(viewModel = viewModel, navController = navController) },
        modifier = Modifier.fillMaxSize().testTag("HomeScreen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // --- City display ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = currentCity?.name ?: stringResource(R.string.missing_city_message),
                    style = MaterialTheme.typography.bodyMedium
                )
                currentCity?.let {
                    Text(
                        text = "${it.lat}, ${it.lon}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // --- Weather chart ---
            WeatherChart(viewModel)

            Spacer(modifier = Modifier.height(4.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SegmentedButtonGroup(
                    selectedType = currentTemperatureType,
                    onTypeSelected = { newType -> viewModel.setTemperatureToDisplay(newType) }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- Weather Cards Grid ---
            val isSearchingWeather by viewModel.isSearchingWeather.collectAsState()
            val isErrorFetchingWeather by viewModel.isErrorFetchingWeather.collectAsState()

            WeatherCardsGrid(
                weatherData = currentWeatherData.lastOrNull(),
                historicalWeatherData = currentWeatherData,
                isLoading = isSearchingWeather,
                isError = isErrorFetchingWeather,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SegmentedButtonGroup(
    selectedType: TemperatureType,
    onTypeSelected: (TemperatureType) -> Unit
) {
    val options = listOf(
        TemperatureType.MIN to stringResource(R.string.temperature_min),
        TemperatureType.MAX to stringResource(R.string.temperature_max)
    )

    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .wrapContentWidth()
            .height(32.dp)
            .padding(horizontal = 4.dp)
    ) {
        options.forEachIndexed { index, (type, label) ->
            SegmentedButton(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                shape = SegmentedButtonDefaults.itemShape(index, options.size),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )
            }
        }
    }
}

