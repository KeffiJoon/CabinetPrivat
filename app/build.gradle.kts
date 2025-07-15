plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}


android {
    namespace = "com.example.cabinetprivat"
    compileSdk = 36


    defaultConfig {
        applicationId = "com.example.cabinetprivat"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
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
    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src\\main\\assets", "src\\main\\assets")
            }
        }
    }
}

dependencies {
// Material Design (pentru CardView, etc.)
    implementation ("com.google.android.material:material:1.12.0") // Versiunea poate varia

    // RecyclerView
    implementation ("androidx.recyclerview:recyclerview:1.3.3")

    // CardView
    implementation ("androidx.cardview:cardview:1.0.0")

    // ConstraintLayout (pentru layout-uri flexibile)
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.x.x")
    // Gson (pentru parsarea JSON)
    implementation ("com.google.code.gson:gson:2.11.0")
    implementation("com.airbnb.android:lottie:6.0.0")
    implementation("androidx.cardview:cardview:1.0.0")
    // Glide (pentru încărcarea imaginilor de pe URL)
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.recyclerview)
    // Dependențe de bază Android

    implementation("com.google.firebase:firebase-auth:22.x.x")

    implementation("com.google.firebase:firebase-firestore:24.x.x")
    implementation ("androidx.core:core-ktx:1.13.1")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.appcompat:appcompat:1.6.1")
    // RecyclerView și CardView
    implementation ("androidx.recyclerview:recyclerview:1.3.3")
    implementation ("androidx.cardview:cardview:1.0.0")


    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.material:material:1.12.0")

    // Glide pentru încărcarea imaginilor de pe URL
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation(libs.play.services.maps)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.google.android.material:material:1.12.0")
    // Testare



    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    implementation(libs.navigation.fragment)
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(libs.navigation.ui)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.android.material:material:1.11.0")
}