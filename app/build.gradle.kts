plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.delta"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.delta"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    androidResources {
        noCompress.add("model")
    }

    buildTypes {

        debug {
            buildConfigField(
                "String",
                "GEMINI_API_KEY",
                "\"${project.findProperty("GEMINI_API_KEY") ?: "API_KEY_NOT_FOUND"}\""
            )
            buildConfigField(
                "String",
                "OPENWEATHER_API_KEY",
                "\"${project.findProperty("OPENWEATHER_API_KEY") ?: "API_KEY_NOT_FOUND"}\""
            )
            buildConfigField(
                "String",
                "STABILITY_API_KEY",
                "\"${project.findProperty("STABILITY_API_KEY") ?: "API_KEY_NOT_FOUND"}\""
            )
            buildConfigField(
                "String",
                "NEWS_API_KEY",
                "\"${project.findProperty("NEWS_API_KEY")?: "API_KEY_NOT_FOUND"}\""
            )
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField(
                "String",
                "GEMINI_API_KEY",
                "\"${project.findProperty("GEMINI_API_KEY") ?: "API_KEY_NOT_FOUND"}\""
            )
            buildConfigField(
                "String",
                "OPENWEATHER_API_KEY",
                "\"${project.findProperty("OPENWEATHER_API_KEY") ?: "API_KEY_NOT_FOUND"}\""
            )
            buildConfigField(
                "String",
                "STABILITY_API_KEY",
                "\"${project.findProperty("STABILITY_API_KEY") ?: "API_KEY_NOT_FOUND"}\""
            )
            buildConfigField(
                "String",
                "NEWS_API_KEY",
                "\"${project.findProperty("NEWS_API_KEY")?: "API_KEY_NOT_FOUND"}\""
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.room.external.antlr)
    implementation(libs.firebase.auth.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation ("com.squareup.okhttp3:okhttp:4.11.0")  // For API requests
    implementation ("org.json:json:20210307") // For handling JSON responses
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation ("androidx.compose.material:material-icons-extended:1.0.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation ("com.alphacephei:vosk-android:0.3.47")
    implementation ("net.java.dev.jna:jna:5.13.0@aar")
    implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("androidx.navigation:navigation-compose:2.7.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.11.0")
    implementation ("com.google.firebase:firebase-storage-ktx")
    implementation ("androidx.compose.foundation:foundation:1.4.0")
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.34.0")
    implementation("androidx.camera:camera-core:1.3.2")
    implementation("androidx.camera:camera-camera2:1.3.2")
    implementation("androidx.camera:camera-lifecycle:1.3.2")
    implementation("androidx.camera:camera-view:1.3.0-beta01")
    implementation("androidx.camera:camera-extensions:1.3.0-beta01")



}


