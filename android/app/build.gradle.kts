plugins {

    id("com.android.application")

    id("org.jetbrains.kotlin.android")

    id(
        "org.jetbrains.kotlin.plugin.compose"
    )

    id(
        "com.google.devtools.ksp"
    )
}

android {

    namespace =
        "com.olcs.agent"

    compileSdk =
        36

    defaultConfig {

        applicationId =
            "com.olcs.agent"

        minSdk =
            26

        targetSdk =
            36

        versionCode =
            1

        versionName =
            "1.0.0"
    }
}

dependencies {

    implementation(
        "androidx.core:core-ktx:1.17.0"
    )

    implementation(
        "androidx.activity:activity-compose:1.10.1"
    )

    implementation(
        "androidx.compose.ui:ui:1.9.0"
    )

    implementation(
        "androidx.compose.ui:ui-tooling-preview:1.9.0"
    )

    implementation(
        "androidx.compose.material3:material3:1.3.2"
    )

    implementation(
        "androidx.lifecycle:lifecycle-runtime-ktx:2.9.2"
    )

    implementation(
        "androidx.room:room-runtime:2.7.2"
    )

    implementation(
        "androidx.room:room-ktx:2.7.2"
    )

    ksp(
        "androidx.room:room-compiler:2.7.2"
    )

    implementation(
        "androidx.work:work-runtime-ktx:2.10.3"
    )

    implementation(
        "com.google.android.gms:play-services-location:21.3.0"
    )

    implementation(
        "com.squareup.retrofit2:retrofit:2.11.0"
    )

    implementation(
        "com.squareup.retrofit2:converter-gson:2.11.0"
    )

    implementation(
        "com.squareup.okhttp3:okhttp:4.12.0"
    )

    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2"
    )
}
