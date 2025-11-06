package com.example.weathertrace.ui.components
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.example.weathertrace.domain.model.City

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SearchTopBar(
    viewModel: com.example.weathertrace.ui.screens.main.MainViewModel
) {
    var query by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    // search results from ViewModel
    val searchResults = viewModel.searchResultsCity.collectAsState()
    val isSearching = viewModel.isSearchingCity.collectAsState()

    // mock
    // TODO: get favorite cities from database
    val favoriteCities = listOf<City>(
        City(name = "Paris", lat = 48.8566, lon = 2.3522),
        City(name = "London", lat = 51.5074, lon = -0.1278),
        City(name = "New York", lat = 40.7128, lon = -74.0060),
        City(name = "Tokyo", lat = 35.6895, lon = 139.6917),
        City(name = "Sydney", lat = -33.8688, lon = 151.2093),
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = {
                        query = it
                        if (it.isNotBlank()) {
                            viewModel.searchCities(it)
                            expanded = true
                        } else {
                            expanded = false
                        }
                    },
                    onSearch = { searchQuery ->
                        viewModel.searchCities(searchQuery)
                        expanded = true
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text("Search city") },
                    leadingIcon = {
                        if (expanded) {
                            IconButton(onClick = {
                                expanded = false
                                query = ""
                                viewModel.clearSearchResults()
                            }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Close search"
                                )
                            }
                        } else {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                    },
                    trailingIcon = {
                        SettingsMenu(
                            expanded = menuExpanded,
                            onExpandedChange = { menuExpanded = it }
                        )
                    }
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            SearchResultsContent(
                isSearching = isSearching.value,
                searchResults = searchResults.value,
                query = query,
                favoriteCities = favoriteCities,
                onCitySelected = { city ->
                    query = ""
                    expanded = false
                    viewModel.setCurrentCity(city)
                    viewModel.clearSearchResults()
                }
            )
        }
    }
}

@Composable
private fun SearchResultsContent(
    isSearching: Boolean,
    searchResults: List<City>,
    query: String,
    favoriteCities: List<City>,
    onCitySelected: (City) -> Unit
) {
    val citiesToDisplay = if (searchResults.isEmpty() && query.isBlank()) {
        favoriteCities
    } else {
        searchResults
    }

    val showFavoriteTitle = searchResults.isEmpty() && query.isBlank()

    // Show loading only if no results yet
    if (isSearching && searchResults.isEmpty() && query.isNotBlank()) {
        LoadingIndicator()
    } else {
        CityList(
            cities = citiesToDisplay,
            showFavoriteTitle = showFavoriteTitle,
            onCitySelected = onCitySelected
        )
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Searching...", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun CityList(
    cities: List<City>,
    showFavoriteTitle: Boolean,
    onCitySelected: (City) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showFavoriteTitle) {
            item {
                Text(
                    text = "Favorite Cities",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        items(cities) { city ->
            CityCard(
                city = city,
                onClick = { onCitySelected(city) }
            )
        }
    }
}

@Composable
private fun CityCard(
    city: City,
    onClick: () -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = city.name,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SettingsMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    Box {
        IconButton(onClick = { onExpandedChange(true) }) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "Menu"
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            DropdownMenuItem(
                text = { Text("Celsius (°C)") },
                onClick = {
                    // TODO: set temperature unit to Celsius
                    onExpandedChange(false)
                }
            )
            DropdownMenuItem(
                text = { Text("Fahrenheit (°F)") },
                onClick = {
                    // TODO: set temperature unit to Fahrenheit
                    onExpandedChange(false)
                }
            )
        }
    }
}