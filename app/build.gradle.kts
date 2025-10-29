plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "2.0.21"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
    id("com.google.devtools.ksp") version "2.0.21-1.0.27"
}

android {
    namespace = "com.example.weathertrace"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.weathertrace"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        val devMail = project.findProperty("DEV_MAIL") ?: ""
        buildConfigField("String", "DEV_MAIL", "\"$devMail\"")

        val apiKey = project.findProperty("OPENWEATHER_API_KEY") ?: ""
        buildConfigField("String", "OPENWEATHER_API_KEY", "\"$apiKey\"")

        val pastYearsToFetchCount = project.findProperty("PAST_YEARS_TO_FETCH_COUNT")?.toString() ?: "1"
        buildConfigField("int", "PAST_YEARS_TO_FETCH_COUNT", pastYearsToFetchCount)

        val pastYearsToFetchCountMax = project.findProperty("PAST_YEARS_TO_FETCH_COUNT_MAX")?.toString() ?: "1"
        buildConfigField("int", "PAST_YEARS_TO_FETCH_COUNT_MAX", pastYearsToFetchCountMax)

        val devModeProp = project.findProperty("DEV_MODE")?.toString() ?: "false"
        buildConfigField("boolean", "DEV_MODE", devModeProp)

    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // --- Networking: Retrofit + Moshi ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.moshi:moshi:1.15.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")

    // --- Coroutines ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // --- Lifecycle (pour lifecycleScope) ---
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")

    // --- Activity (pour ComponentActivity & enableEdgeToEdge) ---
    implementation("androidx.activity:activity:1.9.3")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.activity:activity-compose:1.9.3")

    // --- Jetpack Compose ---
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // --- Tests ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}