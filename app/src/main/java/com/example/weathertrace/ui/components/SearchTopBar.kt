package com.example.weathertrace.ui.components
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SearchTopBar(
    viewModel: com.example.weathertrace.ui.screens.main.MainViewModel
) {
    var query by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    // search results from ViewModel
    val searchResults = viewModel.searchResults.collectAsState()
    val isSearching = viewModel.isSearching.collectAsState()

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
                        IconButton(onClick = { /* TODO: open menu */ }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            // Display search results
            if (isSearching.value) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Searching...", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(searchResults.value) { city ->
                        ListItem(
                            headlineContent = { Text(city.name) },
                            modifier = Modifier
                                .clickable {
                                    query = ""
                                    expanded = false
                                    // TODO: load weather data for selected city
                                }
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

// Preview removed - requires ViewModel instance
