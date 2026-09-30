pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        // adwon-ble-receiver SDK를 빌드한 것과 같은 버전으로 맞춰뒀다 — AAR 소비 자체는
        // 버전이 달라도 되지만(AAR은 바이트코드+리소스라 소스 호환성 문제가 없음), 불필요한
        // 혼란을 피하기 위함이다. 실제 호스트 앱은 이 버전에 맞출 필요 없이 각자 쓰는
        // AGP/Kotlin 버전을 그대로 쓰면 된다.
        id("com.android.application") version "8.6.1"
        id("org.jetbrains.kotlin.android") version "2.0.21"
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "AdWonBleReceiverSample"

// 이 프로젝트는 SDK 소스와 완전히 분리되어 있다 — SDK 모듈을 project(...)로 참조하지
// 않는다. 소스를 노출하지 않으므로, 저장소 최상위 library/ 폴더의 release AAR을 상대
// 경로로 순수 바이너리 의존성으로만 참조한다(app/build.gradle.kts 참조).
include(":app")
