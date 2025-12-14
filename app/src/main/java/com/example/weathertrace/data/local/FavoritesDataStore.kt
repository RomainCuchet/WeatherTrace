package com.example.weathertrace.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.weathertrace.domain.model.City
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

// Extension property to create DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favorites")

/**
 * Serializable version of City for DataStore storage
 */
@Serializable
data class SerializableCity(
    val name: String,
    val lat: Double,
    val lon: Double,
    val displayName: String? = null,
    val country: String? = null,
    val state: String? = null
)

class FavoritesDataStore(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    companion object {
        private val FAVORITES_KEY = stringPreferencesKey("favorite_cities")
    }

    /**
     * Flow that emits the list of favorite cities whenever it changes
     */
    val favoritesFlow: Flow<List<City>> = context.dataStore.data
        .map { preferences ->
            val jsonString = preferences[FAVORITES_KEY] ?: "[]"
            try {
                val serializableCities = json.decodeFromString<List<SerializableCity>>(jsonString)
                serializableCities.map { it.toDomain() }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }

    /**
     * Save the list of favorite cities to DataStore
     */
    suspend fun saveFavorites(cities: List<City>) {
        context.dataStore.edit { preferences ->
            val serializableCities = cities.map { it.toSerializable() }
            val jsonString = json.encodeToString(serializableCities)
            preferences[FAVORITES_KEY] = jsonString
        }
    }

    /**
     * Load the list of favorite cities from DataStore
     */
    suspend fun loadFavorites(): List<City> {
        return try {
            context.dataStore.data.map { preferences ->
                val jsonString = preferences[FAVORITES_KEY] ?: "[]"
                val serializableCities = json.decodeFromString<List<SerializableCity>>(jsonString)
                serializableCities.map { it.toDomain() }
            }.collect { it }
            emptyList() // This won't be reached but needed for compilation
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Clear all favorites
     */
    suspend fun clearFavorites() {
        context.dataStore.edit { preferences ->
            preferences.remove(FAVORITES_KEY)
        }
    }
}

// Extension functions for conversion
private fun City.toSerializable() = SerializableCity(
    name = name,
    lat = lat,
    lon = lon,
)

private fun SerializableCity.toDomain() = City(
    name = name,
    lat = lat,
    lon = lon,
    isFavorite = true
)