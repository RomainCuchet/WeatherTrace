package com.example.weathertrace.domain.repository

import com.example.weathertrace.data.mapper.toDomainList
import com.example.weathertrace.data.remote.geo.api.NominatimApiClient
import com.example.weathertrace.domain.model.City
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CityRepository(
    private val devMode: Boolean = false
) {

    private val api = NominatimApiClient.nominatimService

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
