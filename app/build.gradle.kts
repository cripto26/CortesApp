plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// 👇 añade este bloque (puede ir debajo de plugins)
kotlin {
    jvmToolchain(17)
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
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures { compose = true }

    // 👇 alinea Java a 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    // 👇 opcional si no usas el bloque `kotlin { jvmToolchain(17) }`
    kotlinOptions { jvmTarget = "17" }

    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}


dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.material.icons)

    // Deja SOLO UNA forma de ui-text:
    implementation(libs.compose.ui.text)
    // implementation("androidx.compose.ui:ui-text") // (borra esta si usas el alias)

    debugImplementation(libs.compose.ui.tooling)



    implementation(libs.compose.foundation)   // <- NUEVO







}
