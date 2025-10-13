plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Add the Google services Gradle plugin
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.apk.blogapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.apk.blogapp"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    
    // Import the Firebase BoM
    implementation(platform(libs.firebase.bom))
    
    // Firebase services for Blog App
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)      // Database untuk artikel, kategori, komentar
    implementation(libs.firebase.auth)           // Authentication untuk login admin/user
    implementation(libs.firebase.storage)        // Storage untuk upload gambar artikel
    implementation(libs.firebase.messaging)      // Push notifications untuk artikel baru
    // implementation(libs.firebase.appcheck.debug) // App Check untuk development - disabled for now
    // implementation("com.google.firebase:firebase-appcheck-debug:17.1.2") // Disabled for development
    
    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}