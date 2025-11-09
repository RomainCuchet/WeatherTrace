package com.example.weathertrace.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shape.Shape
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.common.Fill

import com.example.weathertrace.viewModel.MainViewModel
import com.example.weathertrace.R

val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH)

/**
 * Displays a weather chart showing temperature trends across different years.
 *
 * This composable observes state from the provided [MainViewModel] to render a
 * temperature line chart. It handles various UI states:
 * - Shows an error message if weather data cannot be loaded.
 * - Displays a loading message while weather data is being fetched.
 * - Renders a line chart of temperatures over the years once data is available.
 *
 * @param viewModel The [MainViewModel] providing weather data and state flows:
 *  - [MainViewModel.currentProcessedTemps]: The list of processed temperature values.
 *  - [MainViewModel.currentYears]: The corresponding list of years for the temperatures.
 *  - [MainViewModel.isSearchingWeather]: Indicates if weather data is being fetched.
 *  - [MainViewModel.isErrorFetchingWeather]: Indicates if an error occurred while fetching the weather.
 *  - [MainViewModel.currentTemperatureUnit]: The current temperature unit (°C or °F).
 *
 * @author Romain CUCHET
 * @see MainViewModel
 */
@Composable
fun WeatherChart(viewModel: MainViewModel) {
    val temps = viewModel.currentProcessedTemps.collectAsState()
    val years = viewModel.currentYears.collectAsState()
    val isSearchingWeather = viewModel.isSearchingWeather.collectAsState()
    val isErrorFetchingWeather = viewModel.isErrorFetchingWeather.collectAsState()

    if(isErrorFetchingWeather.value){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.error_loading_weather_data), style = MaterialTheme.typography.bodyMedium)
        }
        return
    } else if (isSearchingWeather.value || temps.value.isEmpty() || years.value.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.loading_weather_data), style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(temps.value) {
        modelProducer.runTransaction {
            lineSeries { series(temps.value) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "${LocalDate.now().format(formatter)} ${stringResource(R.string.weather_chart_title)}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center,
        )

        val line = LineCartesianLayer.Line(
            fill = LineCartesianLayer.LineFill.single(Fill(Color(0xFF6200EE).toArgb())),
            thicknessDp = 1.4f,
            pointProvider = LineCartesianLayer.PointProvider.single(
                point = LineCartesianLayer.Point(
                    component = rememberShapeComponent(
                        shape = Shape.rounded(allPercent = 50),
                        color = Color(0xFF6200EE),
                        strokeColor = Color.White,
                        strokeThickness = 0.5.dp
                    ),
                    sizeDp = 6f
                )
            )
        )

        val lineProvider = LineCartesianLayer.LineProvider.series(listOf(line))
        val currentUnit = viewModel.currentTemperatureUnit.collectAsState()

        val startAxis = rememberStartAxis(
            label = rememberTextComponent(color = Color.Black, padding = Dimensions.of(horizontal = 2.dp)),
            title = "${stringResource(R.string.weather_chart_start_axis_title)} (°${currentUnit.value})",
            titleComponent = rememberTextComponent(color = Color.Black, padding = Dimensions.of(horizontal = 4.dp, vertical = 8.dp)),
            guideline = null
        )

        val bottomAxis = rememberBottomAxis(
            label = rememberTextComponent(color = Color.Black, padding = Dimensions.of(vertical = 2.dp)),
            title = "${stringResource(R.string.weather_chart_bottom_axis_title)} ${years.value.first()} ➔ ${years.value.last()}",
            titleComponent = rememberTextComponent(color = Color.Black, padding = Dimensions.of(horizontal = 4.dp, vertical = 2.dp)),
            guideline = null,
            valueFormatter = { value, _, _ ->
                val index = value.toInt()
                if (index in years.value.indices) years.value[index].toString() else ""
            }
        )

        val zoomState = rememberVicoZoomState(
            zoomEnabled = true,
            initialZoom = Zoom.Content,
            minZoom = Zoom.Content,
        )

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(lineProvider = lineProvider),
                startAxis = startAxis,
                bottomAxis = bottomAxis
            ),
            modelProducer = modelProducer,
            zoomState = zoomState,
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        )
    }
}
