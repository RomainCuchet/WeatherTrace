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
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.of
import com.patrykandpatrick.vico.compose.common.shape.rounded
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.shape.Shape
import androidx.core.graphics.toColorInt
import androidx.compose.ui.graphics.toArgb
import com.patrykandpatrick.vico.core.common.Fill

@Composable
fun WeatherChart() {
    val temps = listOf(7.3f, 8.7f, 12.1f, 6.9f, 20.3f, 22.8f, 21.0f, 24.9f,7.3f, 8.7f, 12.1f, 6.9f, 20.3f, 22.8f, 21.0f, 24.9f,7.3f, 8.7f, 12.1f, 6.9f, 20.3f, 22.8f, 21.0f, 24.9f)
    val years = listOf(2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022,2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022,2015, 2016, 2017, 2018, 2019, 2020, 2021, 2022)

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            lineSeries { series(temps) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Évolution annuelle des températures 🌡️",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val line = LineCartesianLayer.Line(
            fill = LineCartesianLayer.LineFill.single(
                Fill(Color(0xFF6200EE).toArgb())
            ),
            thicknessDp = 3f,
            pointProvider = LineCartesianLayer.PointProvider.single(
                point = LineCartesianLayer.Point(
                    component = rememberShapeComponent(
                        shape = Shape.rounded(allPercent = 50),
                        color = Color(0xFFFF5722),
                        strokeColor = Color.White,
                        strokeThickness = 2.dp
                    ),
                    sizeDp = 10f
                )
            )
        )

        val lineProvider = LineCartesianLayer.LineProvider.series(listOf(line))

        val startAxis = rememberStartAxis(
            label = rememberTextComponent(
                color = Color.Black,
                padding = Dimensions.of(horizontal = 8.dp)
            ),
            title = "Temperature (°C)", //TODO: switch to the used metric
            titleComponent = rememberTextComponent(
                color = Color.Black,
                padding = Dimensions.of(horizontal = 4.dp, vertical = 8.dp)
            ),
            guideline = null,
            itemPlacer = remember {
                com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis.ItemPlacer.step(
                    step = { 5.0 }
                )
            }
        )

        val bottomAxis = rememberBottomAxis(
            label = rememberTextComponent(
                color = Color.Black,
                padding = Dimensions.of(vertical = 8.dp)
            ),
            title = "Années",
            titleComponent = rememberTextComponent(
                color = Color.Black,
                padding = Dimensions.of(horizontal = 4.dp, vertical = 8.dp)
            ),
            guideline = null,
            valueFormatter = { value, _, _ ->
                val index = value.toInt()
                if (index in years.indices) years[index].toString() else ""
            }
        )

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider = lineProvider
                ),
                startAxis = startAxis,
                bottomAxis = bottomAxis
            ),
            modelProducer = modelProducer,
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        )
    }
}