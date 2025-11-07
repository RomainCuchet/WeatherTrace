package com.example.weathertrace.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.io.IOException

import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.m3.markdownTypography

@Composable
fun ReadMeDocScreen(navController: NavController) {
    val context = LocalContext.current

    val markdownText by remember {
        mutableStateOf(
            try {
                context.assets.open("README.md")
                    .bufferedReader()
                    .use { it.readText() }
            } catch (e: IOException) {
                "# ❌ Error\nImpossible to reload the documentation from README.md"
            }
        )
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        ComeBackArrow(title = "Doc", navController = navController)

        Markdown(
            content = markdownText,
            colors = markdownColor(
                text = MaterialTheme.colorScheme.onBackground,
                linkText = MaterialTheme.colorScheme.primary,
                codeText = MaterialTheme.colorScheme.secondary,
                codeBackground = MaterialTheme.colorScheme.surfaceVariant,
            ),
            typography = markdownTypography(
                h1 = MaterialTheme.typography.headlineLarge.copy(lineHeight = 18.sp),
                h2 = MaterialTheme.typography.headlineMedium.copy(lineHeight = 16.sp),
                h3 = MaterialTheme.typography.headlineSmall.copy(lineHeight = 14.sp),
                text = MaterialTheme.typography.bodyMedium.copy(lineHeight = 8.sp),
                code = MaterialTheme.typography.bodySmall.copy(lineHeight = 7.sp),
                list = MaterialTheme.typography.bodyMedium.copy(lineHeight = 8.sp),
                bullet = MaterialTheme.typography.bodyMedium.copy(lineHeight = 8.sp),
                ordered = MaterialTheme.typography.bodyMedium.copy(lineHeight = 8.sp),
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}