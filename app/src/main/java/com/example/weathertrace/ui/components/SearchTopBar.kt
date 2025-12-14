package com.example.weathertrace.ui.components
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.example.weathertrace.viewModel.MainViewModel
import com.example.weathertrace.domain.model.City
import com.example.weathertrace.R

import androidx.navigation.NavController

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SearchTopBar(
    viewModel: MainViewModel,
    navController: NavController
) {
    var query by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    // search results from ViewModel
    val searchResults = viewModel.searchResultsCity.collectAsState()
    val isSearching = viewModel.isSearchingCity.collectAsState()
    val isErrorSearchingCity = viewModel.isErrorSearchingCity.collectAsState()
    val favoriteCities by viewModel.favoriteCities.collectAsState()

    // mock
    // TODO: get favorite cities from database
    /*val favoriteCities = listOf<City>(
        City(name = "Paris", lat = 48.8566, lon = 2.3522),
        City(name = "London", lat = 51.5074, lon = -0.1278),
        City(name = "New York", lat = 40.7128, lon = -74.0060),
        City(name = "Tokyo", lat = 35.6895, lon = 139.6917),
        City(name = "Sydney", lat = -33.8688, lon = 151.2093),
    )
     */

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { isTraversalGroup = true }
            .testTag("SearchTopBar")
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
                    placeholder = { Text(stringResource(R.string.search_city)) },
                    leadingIcon = {
                        if (expanded) {
                            IconButton(onClick = {
                                expanded = false
                                query = ""
                                viewModel.clearSearchResultsCity()
                            }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.close_city_search)
                                )
                            }
                        } else {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = stringResource(R.string.search)
                            )
                        }
                    },
                    trailingIcon = {
                        MenuButton(
                            expanded = menuExpanded,
                            onExpandedChange = { menuExpanded = it },
                            navController = navController
                        )
                    }
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            SearchResultsContent(
                isSearching = isSearching.value,
                isErrorSearchingCity = isErrorSearchingCity.value,
                searchResults = searchResults.value,
                query = query,
                favoriteCities = favoriteCities,
                onCitySelected = { city ->
                    query = ""
                    expanded = false
                    viewModel.setCurrentCity(city)
                    viewModel.clearSearchResultsCity()
                },
                onFavoriteClick = { city ->
                    viewModel.toggleFavorite(city)
                }
            )
        }
    }
}

@Composable
private fun SearchResultsContent(
    isSearching: Boolean,
    isErrorSearchingCity: Boolean,
    searchResults: List<City>,
    query: String,
    favoriteCities: List<City>,
    onCitySelected: (City) -> Unit,
    onFavoriteClick: (City) -> Unit
) {
    val citiesToDisplay = if (searchResults.isEmpty() && query.isBlank()) {
        favoriteCities
    } else {
        searchResults
    }

    val showFavoriteTitle = searchResults.isEmpty() && query.isBlank()

    if (isErrorSearchingCity && searchResults.isEmpty()){
        Indicator(stringResource(R.string.error_loading_cities))
    }
    else if (isSearching && searchResults.isEmpty() && query.isNotBlank()) {
        Indicator(stringResource(R.string.searching))
    } else {
        CityList(
            cities = citiesToDisplay,
            showFavoriteTitle = showFavoriteTitle,
            onCitySelected = onCitySelected,
            onFavoriteClick = onFavoriteClick
        )
    }
}

@Composable
private fun Indicator(text : String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun CityList(
    cities: List<City>,
    showFavoriteTitle: Boolean,
    onCitySelected: (City) -> Unit,
    onFavoriteClick: (City) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showFavoriteTitle) {
            item {
                Text(
                    text = stringResource(R.string.favorite_cities),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        items(cities) { city ->
            CityCard(
                city = city,
                onClick = { onCitySelected(city) },
                onFavoriteClick = { onFavoriteClick(city) }
            )
        }
    }
}

@Composable
private fun CityCard(
    city: City,
    onClick: () -> Unit,
    onFavoriteClick: (City) -> Unit
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp), // Ajuster le padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = city.name,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { onFavoriteClick(city) },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = if (city.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (city.isFavorite) stringResource(R.string.remove_from_favorites) else stringResource(R.string.add_to_favorites),
                    tint = if (city.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MenuButton(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    navController: NavController

) {
    Box {
        IconButton(
            onClick = { onExpandedChange(true) },
            modifier = Modifier.testTag("MenuButton")
        ) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.menu)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            DropdownMenuItem(
                { Text(stringResource(R.string.settings_screen_title)) },
                onClick = {
                    navController.navigate("settingsScreen")
                }
            )

            DropdownMenuItem(
                text = { Text(stringResource(R.string.doc_screen_title)) },
                onClick = {
                    navController.navigate("readmeScreen")
                }
            )
        }
    }
}