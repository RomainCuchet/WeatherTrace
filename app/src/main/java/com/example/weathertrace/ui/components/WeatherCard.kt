package com.example.weathertrace.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weathertrace.domain.model.*
import com.example.weathertrace.R
import kotlin.math.roundToInt

/**
 * Grid of weather cards displaying today's weather information
 * Each card opens a bottom sheet with historical chart when clicked
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherCardsGrid(
    weatherData: DailyWeatherModel?,
    historicalWeatherData: List<DailyWeatherModel>,
    isLoading: Boolean = false,
    isError : Boolean,
    modifier: Modifier = Modifier
) {
    var selectedCard by remember { mutableStateOf<WeatherCardType?>(null) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Column(modifier = modifier) {
        if (weatherData != null && !isError) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    WeatherCard(
                        type = WeatherCardType.Precipitation(weatherData.precipitation.total),
                        onClick = { selectedCard = it },
                        modifier = Modifier.weight(1f)
                    )

                    WeatherCard(
                        type = WeatherCardType.CloudCover(weatherData.cloudCover.afternoon),
                        onClick = { selectedCard = it },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Second row: Wind and Cloud Cover
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    WeatherCard(
                        type = WeatherCardType.Wind(weatherData.wind),
                        onClick = { selectedCard = it },
                        modifier = Modifier.weight(1f)
                    )

                    WeatherCard(
                        type = WeatherCardType.Humidity(weatherData.humidity.afternoon),
                        onClick = { selectedCard = it },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    // Bottom Sheet with historical chart
    selectedCard?.let { cardType ->
        val configuration = LocalConfiguration.current
        val modalHeight = configuration.screenHeightDp * 0.7f

        ModalBottomSheet(
            onDismissRequest = { selectedCard = null },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(modalHeight.dp)
            ) {
                WeatherDetailBottomSheet(
                    cardType = cardType,
                    weatherData = historicalWeatherData,
                    isLoading = isLoading,
                    isError = isError,
                    onDismiss = { selectedCard = null }
                )
            }
        }
    }
}

/**
 * Individual weather card with icon and value
 */
@Composable
fun WeatherCard(
    type: WeatherCardType,
    onClick: (WeatherCardType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick(type) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = type.icon,
                contentDescription = type.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Column {
                Text(
                    text = type.value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = type.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Bottom sheet content with historical weather chart
 */
@Composable
fun WeatherDetailBottomSheet(
    cardType: WeatherCardType,
    weatherData: List<DailyWeatherModel>,
    isLoading: Boolean,
    isError: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = cardType.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp)
            )
            Column {
                Text(
                    text = cardType.title,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = cardType.value,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Display historical chart based on card type
        val years = weatherData.map { it.date.year }
        val (chartTitle, chartValues, yAxisTitle) = when (cardType) {
            is WeatherCardType.Temperature -> Triple(
                "Historical Temperature Trends",
                weatherData.map { convertTemp(it.temperature.afternoon, cardType.unit).toDouble() },
                "Temperature (${getUnitSymbol(cardType.unit)})"
            )
            is WeatherCardType.Wind -> Triple(
                "Historical Wind Speed Trends",
                weatherData.map { it.wind.max.speed },
                "Wind Speed (km/h)"
            )
            is WeatherCardType.Humidity -> Triple(
                "Historical Humidity Trends",
                weatherData.map { it.humidity.afternoon },
                "Humidity (%)"
            )
            is WeatherCardType.Precipitation -> Triple(
                "Historical Precipitation Trends",
                weatherData.map { it.precipitation.total },
                "Precipitation (mm)"
            )
            is WeatherCardType.CloudCover -> Triple(
                "Historical Cloud Cover Trends",
                weatherData.map {it.cloudCover.afternoon},
                "Cloud Cover (%)"

            )
        }

        GenericWeatherChart(
            title = chartTitle,
            dataValues = chartValues,
            years = years,
            yAxisTitle = yAxisTitle,
            isLoading = isLoading,
            isError = isError,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Sealed class representing different weather card types
 */
sealed class WeatherCardType {
    abstract val title: String
    abstract val value: String
    abstract val icon: ImageVector

    data class Temperature(
        val temp: com.example.weathertrace.domain.model.Temperature,
        val unit: TemperatureUnit
    ) : WeatherCardType() {
        override val title = "Temperature"
        override val value = "${convertTemp(temp.afternoon, unit)}${getUnitSymbol(unit)}"
        override val icon = Icons.Default.DeviceThermostat
    }

    data class Wind(val wind: com.example.weathertrace.domain.model.Wind) : WeatherCardType() {
        override val title = "Wind"
        override val value = "${wind.max.speed.roundToInt()} km/h"
        override val icon = Icons.Default.Air
    }

    data class Humidity(val humidity: Double) : WeatherCardType() {
        override val title = "Humidity"
        override val value = "${humidity.roundToInt()}%"
        override val icon = Icons.Default.Opacity
    }

    data class Precipitation(val precipitation: Double) : WeatherCardType() {
        override val title = "Precipitation"
        override val value = "${precipitation.roundToInt()} mm"
        override val icon = Icons.Default.WaterDrop
    }

    data class CloudCover(val cloudCover: Double): WeatherCardType(){
        override val title = "Could Cover";
        override val value = "${cloudCover.roundToInt()}%"
        override val icon = Icons.Default.Cloud
    }
}

/**
 * Convert temperature from Kelvin to the specified unit
 */
fun convertTemp(kelvin: Double, unit: TemperatureUnit): Int {
    return when (unit) {
        TemperatureUnit.C -> (kelvin - 273.15).roundToInt()
        TemperatureUnit.F -> ((kelvin - 273.15) * 9 / 5 + 32).roundToInt()
    }
}

/**
 * Get temperature unit symbol
 */
fun getUnitSymbol(unit: TemperatureUnit): String {
    return when (unit) {
        TemperatureUnit.C -> "°C"
        TemperatureUnit.F -> "°F"
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherCardPreview() {
    MaterialTheme {
        WeatherCard(
            type = WeatherCardType.Temperature(
                com.example.weathertrace.domain.model.Temperature(
                    min = 280.0,
                    max = 295.0,
                    morning = 285.0,
                    afternoon = 293.0,
                    evening = 288.0,
                    night = 282.0
                ),
                TemperatureUnit.C
            ),
            onClick = {},
            modifier = Modifier
                .padding(16.dp)
                .width(150.dp)
        )
    }
}
