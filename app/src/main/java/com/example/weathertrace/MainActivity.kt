package com.example.weathertrace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme

import com.example.weathertrace.ui.screens.main.MainScreen
import com.example.weathertrace.ui.screens.main.MainViewModel
import com.example.weathertrace.ui.screens.main.MainViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (BuildConfig.DEV_MODE) {
            println("⚙️ Dev Mode activated: using mocked data")
        }
        setContent {
            val viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = MainViewModelFactory(BuildConfig.DEV_MODE)
            )
            MaterialTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}