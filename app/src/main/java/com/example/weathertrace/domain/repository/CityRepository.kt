package com.example.weathertrace.domain.repository

import com.example.weathertrace.data.mapper.toDomainList
import com.example.weathertrace.data.remote.geo.api.NominatimApiClient
import com.example.weathertrace.domain.model.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Collections

class CityRepository(
    private val devMode: Boolean = false
) {
    private val favoriteCities = mutableListOf<City>() //TODO simulation for now, later on persistance
    private val api = NominatimApiClient.nominatimService

    fun addFavorite(city : City){
        if (!isFavorite(city)) {
            favoriteCities.add(city.copy(isFavorite = true))
        }
    }

    fun removeFavorite(city : City) {
        favoriteCities.removeAll { it.name == city.name && it.lat == city.lat && it.lon == city.lon }
    }

    fun isFavorite(city : City) : Boolean {
        //return favoriteCities.contains(city)
        return favoriteCities.any { it.name == city.name && it.lat == city.lat && it.lon == city.lon }
    }

    fun getFavorites(): List<City> {
        return favoriteCities.toList()
    }

    fun reorderFavorites(fromIndex: Int, toIndex: Int): Boolean {
        if (fromIndex == toIndex ||
            fromIndex !in favoriteCities.indices ||
            toIndex !in favoriteCities.indices) {
            return false
        }
        val cityToMove = favoriteCities.removeAt(fromIndex)
        favoriteCities.add(toIndex, cityToMove)

        return true
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
