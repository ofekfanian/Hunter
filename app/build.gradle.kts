plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ofek.hunter"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ofek.hunter"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

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

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    lint {
        baseline = file("lint-baseline.xml")
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    // ============================================
    // ANDROIDX CORE LIBRARIES
    // ============================================
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // ============================================
    // MATERIAL DESIGN
    // ============================================
    implementation("com.google.android.material:material:1.11.0")

    // ============================================
    // FIREBASE (BOM for version management)
    // ============================================
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Firebase UI Authentication
    implementation("com.firebaseui:firebase-ui-auth:8.0.2")

    // ============================================
    // KOTLIN COROUTINES
    // ============================================
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // ============================================
    // UI COMPONENTS
    // ============================================
    // RecyclerView & CardView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    // SwipeRefreshLayout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // ViewPager2 (for onboarding screens)
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    // ============================================
    // LIFECYCLE COMPONENTS
    // ============================================
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    // ============================================
    // IMAGE LOADING
    // ============================================
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // ============================================
    // DATA SERIALIZATION
    // ============================================
    implementation("com.google.code.gson:gson:2.10.1")

    // ============================================
    // GOOGLE PLAY SERVICES
    // ============================================
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // ============================================
    // GEOSPATIAL (H3 for location indexing)
    // ============================================
    implementation("com.uber:h3:4.1.1")

    // ============================================
    // CHARTS & VISUALIZATION (Vico)
    // ============================================
    // Using latest stable version 2.0.0-alpha.28
    implementation("com.patrykandpatrick.vico:compose:2.0.0-alpha.28")
    implementation("com.patrykandpatrick.vico:compose-m3:2.0.0-alpha.28")
    implementation("com.patrykandpatrick.vico:views:2.0.0-alpha.28")
    implementation("com.patrykandpatrick.vico:core:2.0.0-alpha.28")

    // ============================================
    // TESTING LIBRARIES
    // ============================================
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
