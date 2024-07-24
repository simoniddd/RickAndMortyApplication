plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.rickandmortyapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.rickandmortyapplication"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.logging.interceptor)
    implementation (libs.androidx.room.runtime)
    implementation (libs.kotlinx.coroutines.core)
    implementation (libs.kotlinx.coroutines.android)
    // Kotlin Coroutines Adapter for Retrofit
    implementation (libs.retrofit2.kotlin.coroutines.adapter)
    // Glide
    implementation (libs.glide)
    implementation(libs.androidx.navigation.fragment)
    annotationProcessor(libs.compiler)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.swiperefreshlayout)
    kapt(libs.androidx.room.compiler)
    annotationProcessor(libs.androidx.room.compiler)
    implementation (libs.androidx.room.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}