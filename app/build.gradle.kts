plugins {
    // Only applies the Android Application plugin
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.vocabularybuilder"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.vocabularybuilder"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false // TODO: Set to true for your release build
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        // UPGRADE to Java 17, which is required by modern Android
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // buildFeatures to enable ViewBinding
    // This removes the need for findViewById()
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // --- Core UI & Test Dependencies ---
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // --- Room components (UPDATED) ---
    implementation(libs.room.runtime)
    // --- Uses annotationProcessor as requested ---
    annotationProcessor(libs.room.compiler)

    // --- Lifecycle components ---
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.common.java8)

    // --- Retrofit for API calls ---
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
}