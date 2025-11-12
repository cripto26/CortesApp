plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.quirozsolucions.cortesapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.quirozsolucions.cortesapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures { compose = true }

    // ⚠️ Asegura que Java (javac) compile al MISMO nivel que Kotlin
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // ❌ Si tenías esto, elimínalo (causa 1.8 vs 21):
    // kotlinOptions { jvmTarget = "1.8" }
}

// ⚠️ Y fija el toolchain de Kotlin al mismo nivel
kotlin {
    jvmToolchain(17)   // usa 21 si prefieres, pero entonces cambia ambos a 21
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.lifecycle:lifecycle-runtime-compose-android:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose-android:2.9.0")

    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.navigation:navigation-compose:2.8.3")
}
