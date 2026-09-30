plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.adwon.ble.receiver.sample"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.adwon.ble.receiver.sample"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // 연동규격서 2절과 동일한 방식 — release AAR을 파일로 직접 참조한다. 저장소 최상위
    // library/ 폴더의 AAR을 상대 경로로 그대로 참조하므로(별도 복사 불필요), 버전을 올릴 때는
    // library/ 폴더의 파일명과 이 경로를 함께 갱신하면 된다.
    implementation(files("../../library/adwon-ble-receiver-1.0.0.aar"))

    // AAR을 파일로 직접 참조하면 POM이 없어 전이 의존성이 자동으로 따라오지 않으므로,
    // 호스트 앱(이 샘플)이 직접 선언한다(연동규격서 2절 참조).
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}
