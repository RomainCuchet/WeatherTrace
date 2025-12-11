package com.example.weathertrace.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.navigation.compose.composable
import com.example.weathertrace.domain.model.City
import com.example.weathertrace.viewModel.MainViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {

    val context = LocalContext.current
    val navController = rememberNavController()
    val currentCity by viewModel.currentCity.collectAsState()

    val defaultCity = remember { City("Biarritz", 43.483, -1.558) }

    val locationPermission = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    /**
     * Implements the home city screen (by default: Biarritz) based on location permissions.
     * Runs on first launch and whenever the location permission changes
     *
     * @author Camille ESPIEUX
     */
    //On first launch
    LaunchedEffect(locationPermission) {
        if(currentCity == null) {
            if (locationPermission.allPermissionsGranted) {
                fetchDeviceLocation(context) { city ->
                    viewModel.setInitialCity(city ?: defaultCity)
                }
            } else if (locationPermission.shouldShowRationale) {
                //in case loc already refused before: no pop up
                viewModel.setInitialCity(defaultCity)
            } else {
                locationPermission.launchMultiplePermissionRequest()
            }
        }
    }

    //If changes in permissions later
    LaunchedEffect(locationPermission.allPermissionsGranted) {
        if (currentCity == null) {
            if (locationPermission.allPermissionsGranted) {
                fetchDeviceLocation(context) { city ->
                    viewModel.setInitialCity(city ?: defaultCity)
                }
            } else if (!locationPermission.shouldShowRationale && !locationPermission.allPermissionsGranted) {
                viewModel.setInitialCity(defaultCity)
            }
        } else {
            if (!locationPermission.allPermissionsGranted && currentCity?.name != defaultCity.name) {
                viewModel.setCurrentCity(defaultCity)
            } else if (locationPermission.allPermissionsGranted){
                fetchDeviceLocation(context) { city ->
                    viewModel.setCurrentCity(city ?: defaultCity)
                }
            }
        }
    }

    //Loading until access to a city (default: Paris if no permissions)
    if (currentCity == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
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
            composable("settingsScreen") {
                SettingsScreen(viewModel, navController)
            }
        }
    }
}

/**
 * Private function to fetch the device location
 *
 * @param context
 * @param onResult
 * @author Camille ESPIEUX
 */
@SuppressLint("MissingPermission")
private fun fetchDeviceLocation(context: Context, onResult: (City?) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val defaultCity = City("Biarritz", 43.483, -1.558)

    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                try {
                    //using geocoder to get a city name to create a city object after
                    val geocoder = Geocoder(context, java.util.Locale.getDefault())

                    @Suppress("DEPRECATION")
                    val userAddresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val cityName = userAddresses?.firstOrNull()?.locality

                    if (cityName != null) {
                        val userCity = City(name = cityName, lat = location.latitude, lon = location.longitude)
                        onResult(userCity)
                    } else {
                        onResult(defaultCity)
                    }
                } catch (e: Exception) {
                    onResult(defaultCity)
                }
            } else {
                onResult(defaultCity)
            }
        }
        .addOnFailureListener {
            onResult(defaultCity)
        }
}