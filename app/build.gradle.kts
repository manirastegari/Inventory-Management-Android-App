// This file contains configuration options specific to the app module. It includes dependencies,
// and some other settings specific to the app.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.10"
}


android {
    namespace = "com.example.property_app_g02"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.property_app_g02"
        minSdk = 24
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
    buildFeatures  {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("com.google.firebase:firebase-auth:21.0.1")
    implementation ("com.google.firebase:firebase-firestore:24.0.1")
    implementation ("com.google.android.gms:play-services-maps:19.0.0")

    // glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Kotlin serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // json converter
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")

    // used for debugging the network request
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    // Google Play
    implementation ("com.google.android.gms:play-services-location:18.0.0")
}