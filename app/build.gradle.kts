plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
}

android {
    namespace = "com.techmania.grocify"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.techmania.grocify"
        minSdk = 26
        targetSdk = 34 //34
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
        sourceCompatibility = JavaVersion.VERSION_17 //1_8
        targetCompatibility = JavaVersion.VERSION_17  //1_8
    }
    kotlinOptions {
        jvmTarget = "17" //1_8
    }

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
//    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("androidx.annotation:annotation:1.9.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")


    //text dimension
    implementation("com.intuit.sdp:sdp-android:1.1.1")
    implementation("com.intuit.ssp:ssp-android:1.1.1")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.1")
    //lifecycle
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-common-java8:2.9.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.1")


    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth-api-phone:18.2.0")
    implementation ("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-messaging-ktx:24.1.2")
    implementation("com.google.firebase:firebase-auth")

    //for email/password
//    implementation("com.google.firebase:firebase-auth-ktx")

    //image slider
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")

    //shimmer effect
    implementation("com.facebook.shimmer:shimmer:0.5.0@aar")

    //room database
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    //glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

//    // phone pay
//    implementation("phonepe.intentsdk.android.release:IntentSDK:2.3.0")
//
    //retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")




}