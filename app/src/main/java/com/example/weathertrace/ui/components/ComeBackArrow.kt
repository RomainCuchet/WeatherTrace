package com.example.weathertrace.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource

import com.example.weathertrace.R

@Composable
fun ComeBackArrow(
    title: String,
    navController: NavController,
    onClick: (() -> Unit)? = null
) {


    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = { onClick?.invoke() ?: navController.popBackStack() },
            modifier = Modifier.testTag("ComeBackArrow")
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.close_city_search))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.headlineSmall)
    }

    Spacer(modifier = Modifier.height(24.dp))
}