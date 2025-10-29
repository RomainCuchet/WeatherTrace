package com.example.weathertrace.data.remote.geo.api

import retrofit2.http.GET
import retrofit2.http.Query

import com.example.weathertrace.data.remote.geo.dto.CityResponse

interface NominatimService {

    @GET("search?format=json")
    suspend fun searchCity(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
        @Query("countrycodes") countryCodes: String? = null
    ): List<CityResponse>
}
