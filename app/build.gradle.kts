plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
//    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.sky.oa"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.sky.oa"
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
    buildFeatures {
//        compose = true
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
//    jetpack 组件
//    implementation(libs.androidx.activity.compose)
//    implementation(platform(libs.androidx.compose.bom))
//    implementation(libs.androidx.compose.ui)
//    implementation(libs.androidx.compose.ui.graphics)
//    implementation(libs.androidx.compose.ui.tooling.preview)
//    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.core.ktx)
//  正常xml 开发组件
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)


    implementation(libs.androidx.appcompat)
    // Material Design
    implementation(libs.material)
    implementation(libs.androidx.recyclerview)

    implementation(project(":base"))
    // lifecycle
    // Flow 支持
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    // SavedStateHandle
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.swiperefreshlayout)

    implementation(libs.glide)

    implementation(libs.gson)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.paging.common.ktx)
    implementation(libs.androidx.paging.runtime.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    androidTestImplementation(platform(libs.androidx.compose.bom))
//    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
//    debugImplementation(libs.androidx.compose.ui.tooling)
//    debugImplementation(libs.androidx.compose.ui.test.manifest)
//    implementation("androidx.activity:activity-ktx:1.9.0")
//    implementation("androidx.fragment:fragment-ktx:1.7.0")
}