package com.example.weathertrace.domain.repository

import android.content.Context
import com.example.weathertrace.data.local.FavoritesDataStore
import com.example.weathertrace.data.mapper.toDomainList
import com.example.weathertrace.data.remote.geo.api.NominatimApiClient
import com.example.weathertrace.domain.model.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first

class CityRepository(
    context: Context,
    private val devMode: Boolean = false
) {
    private val dataStore = FavoritesDataStore(context)
    private val api = NominatimApiClient.nominatimService

    // Internal mutable list for operations
    private val _favoriteCities = MutableStateFlow<List<City>>(emptyList())

    // Public immutable flow for observers
    val favoriteCities: Flow<List<City>> = _favoriteCities.asStateFlow()

    /**
     * Initialize repository by loading favorites from DataStore
     * Call this at app startup
     */
    suspend fun initialize() {
        withContext(Dispatchers.IO) {
            try {
                dataStore.favoritesFlow.collect { cities ->
                    _favoriteCities.value = cities
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _favoriteCities.value = emptyList()
            }
        }
    }

    /**
     * Load favorites once (alternative to initialize if you don't need flow updates)
     */
    suspend fun loadFavorites() {
        withContext(Dispatchers.IO) {
            try {
                dataStore.favoritesFlow.first().let { loadedCities ->
                    _favoriteCities.value = loadedCities
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _favoriteCities.value = emptyList()
            }
        }
    }

    suspend fun addFavorite(city: City) {
        if (!isFavorite(city)) {
            val updatedList = _favoriteCities.value.toMutableList()
            updatedList.add(city.copy(isFavorite = true))
            _favoriteCities.value = updatedList

            // Persist to DataStore
            withContext(Dispatchers.IO) {
                dataStore.saveFavorites(updatedList)
            }
        }
    }

    suspend fun removeFavorite(city: City) {
        val updatedList = _favoriteCities.value.toMutableList()
        updatedList.removeAll { it.name == city.name && it.lat == city.lat && it.lon == city.lon }
        _favoriteCities.value = updatedList

        // Persist to DataStore
        withContext(Dispatchers.IO) {
            dataStore.saveFavorites(updatedList)
        }
    }

    fun isFavorite(city: City): Boolean {
        return _favoriteCities.value.any {
            it.name == city.name && it.lat == city.lat && it.lon == city.lon
        }
    }

    fun getFavorites(): List<City> {
        return _favoriteCities.value.toList()
    }

    suspend fun reorderFavorites(fromIndex: Int, toIndex: Int): Boolean {
        val currentList = _favoriteCities.value

        if (fromIndex == toIndex ||
            fromIndex !in currentList.indices ||
            toIndex !in currentList.indices) {
            return false
        }

        val updatedList = currentList.toMutableList()
        val cityToMove = updatedList.removeAt(fromIndex)
        updatedList.add(toIndex, cityToMove)

        _favoriteCities.value = updatedList

        // Persist to DataStore
        withContext(Dispatchers.IO) {
            dataStore.saveFavorites(updatedList)
        }

        return true
    }

    /**
     * Clear all favorites
     */
    suspend fun clearFavorites() {
        _favoriteCities.value = emptyList()
        withContext(Dispatchers.IO) {
            dataStore.clearFavorites()
        }
    }

    /**
     * Search for cities using OpenStreetMap (Nominatim) API.
     *
     * @param query Name of the city or text to search.
     * @param limit Maximum number of results (default = 5).
     * @param countryCodes Optional country filter ("fr", "us", etc.).
     * @return List of matching [City] objects.
     */
    suspend fun searchCities(
        query: String,
        limit: Int = 5,
        countryCodes: String? = null
    ): List<City> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()

        try {
            val response = api.searchCity(query, limit, countryCodes)
            response.toDomainList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}