# AdWon BLE 수신부 SDK (adwon-ble-receiver)

매장 테이블에 배치된 기기에서 BLE 신호로 고객 체류를 감지해 서버로 전송하는 Android
라이브러리(수신부 SDK)입니다.

이 저장소는 **수신부 SDK 전용**입니다. 신호를 내보내는 송신부 SDK는 별도 저장소에서
배포됩니다 — 두 SDK를 실제로 도입해 사용하는 회사가 서로 다르기 때문입니다.

> [!WARNING]
> **현재 배포판은 테스트(개발) 서버를 사용합니다 — 추후 SDK 교체가 필요합니다.**
>
> 이 SDK(연동규격서 v1.0 / AAR 1.0.0)가 내부적으로 통신하는 서버는 아직 정식 상용 서버가
> 아니라 **테스트(개발) 서버**입니다. 상용 서버가 준비되면 그 주소로 전환된 **새 버전의
> SDK가 이 저장소를 통해 배포될 예정**이며, 그 시점에는 지금 연동해두신 AAR을 반드시
> **새 버전으로 교체**해주셔야 합니다.
>
> 현재 버전을 교체하지 않고 계속 사용하시면, 상용 서버가 열린 뒤에도 이 SDK는 여전히
> 테스트 서버로 요청을 보내기 때문에 App Code 검증/방문 기록 전송이 정상적으로 동작하지
> 않습니다. 새 버전이 배포되면 이 저장소(및 별도 안내 채널)를 통해 공지드릴 예정이니,
> **SDK 업데이트 공지를 반드시 확인하고 적용**해주세요.

## 이 저장소에 있는 것

- **연동규격서**: 바로 이 페이지 아래([연동규격서 (v1.0)](#연동규격서-v10)) — 이 SDK를
  자신의 앱에 붙이는 데 필요한 모든 내용(권한, 공개 API, 예외 상황 등).
  **가장 먼저 이 문서를 읽어주세요.**
- **release AAR**: [`library/`](./library/) 폴더에 실제 배포 파일이 그대로 들어있습니다
  (예: `library/adwon-ble-receiver-1.0.0.aar`) — 버전은 파일명으로 구분합니다.
- **샘플 앱**: [`sample/`](./sample/) — `library/`의 release AAR만 참조해 최소 연동
  코드가 어떤 모습인지 보여주는 예제 프로젝트. SDK 소스는 포함되어 있지 않습니다.

## 빠른 시작

1. [`library/adwon-ble-receiver-<version>.aar`](./library/)을 자신의 앱 프로젝트로
   복사합니다(저장소를 clone하거나 GitHub에서 파일을 직접 내려받으면 됩니다).
2. 연동규격서 [2절](#2-배포-구조)을 참고해 자신의 앱 `build.gradle.kts`에 추가합니다.
3. 연동규격서 [3절](#3-필요-권한-및-매니페스트-설정)~[4절](#4-공개-api-레퍼런스)을 참고해
   `AdWonReceiverSdk`를 연동합니다.
4. 직접 동작을 확인해보고 싶다면 [`sample/`](./sample/) 프로젝트의 `sample/README.md`를
   따라 샘플 앱을 빌드/실행해보세요 — `library/`의 AAR을 그대로 참조하므로 별도 준비가
   필요 없습니다.

---

## 연동규격서 (v1.0)

> 대상 독자: 이 SDK(`adwon-ble-receiver`)를 자신의 앱에 붙이는 호스트 앱 개발자

### 목차

1. [개요](#1-개요)
2. [배포 구조](#2-배포-구조)
3. [필요 권한 및 매니페스트 설정](#3-필요-권한-및-매니페스트-설정)
4. [공개 API 레퍼런스](#4-공개-api-레퍼런스)
5. [데이터 모델](#5-데이터-모델)
6. [Logger 연동](#6-logger-연동)
7. [배포 산출물 및 난독화 (ProGuard/R8)](#7-배포-산출물-및-난독화-proguardr8)
8. [동작 예외 상황](#8-동작-예외-상황)
9. [버전 정보](#9-버전-정보)
부록 A. [샘플 앱](#부록-a-샘플-앱)

---

### 1. 개요

AdWon BLE 수신부 SDK(`adwon-ble-receiver`)는 매장 테이블에 배치되는 기기에 탑재하는
Android 라이브러리입니다. 고객 폰에서 신호를 내보내는 별도의 송신부 SDK와 쌍을 이루어
동작하며, 송신부는 별도 저장소로 배포됩니다 — 두 SDK를 도입해 사용하는 회사가 서로
다르기 때문입니다.

### 2. 배포 구조

| 항목 | 값 |
|---|---|
| 패키지 | `com.adwon.ble.receiver` |
| minSdk | 26 (Android 8.0) |
| compileSdk / targetSdk | 35 |
| 진입점 | `AdWonReceiverSdk` |
| Release 버전 | 이 저장소 [`library/`](./library/) 폴더의 AAR 파일명을 따릅니다(예: `adwon-ble-receiver-1.0.0.aar`). |

호스트 앱 `build.gradle.kts`에 아래와 같이 추가하세요.

```kotlin
dependencies {
    implementation(files("libs/adwon-ble-receiver-<version>.aar"))

    // AAR을 파일로 직접 참조하면 POM이 없어 아래 두 의존성이 자동으로 딸려오지 않으므로,
    // 호스트 앱이 직접 선언해야 합니다.
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.work:work-runtime-ktx:2.9.1")
}
```

자세한 예시는 부록 A(샘플 앱)를 참조하세요.

### 3. 필요 권한 및 매니페스트 설정

SDK가 필요한 권한을 자신의 AAR 매니페스트에 이미 선언해뒀기 때문에, Android Gradle
Plugin의 매니페스트 병합(manifest merge)으로 호스트 앱에 **자동으로 반영**됩니다. 호스트
앱이 직접 `<uses-permission>`을 추가할 필요는 없습니다 — 아래 표는 참고용입니다.

| 권한 | 용도 |
|---|---|
| `BLUETOOTH_SCAN` (`neverForLocation`) | Android 12+(API 31) BLE 스캔, 위치 권한 불필요 |
| `BLUETOOTH`, `BLUETOOTH_ADMIN` (maxSdk 30) | API 30 이하 레거시 |
| `ACCESS_FINE_LOCATION` (maxSdk 30) | API 30 이하 레거시 스캔 요건 |
| `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_CONNECTED_DEVICE` | 24시간 상시 스캔 파이프라인 |
| `POST_NOTIFICATIONS` | Android 13+ Foreground Service 알림 |
| `RECEIVE_BOOT_COMPLETED` | 재부팅/업데이트 후 자동 재시작 |
| `INTERNET`, `ACCESS_NETWORK_STATE` | 서버 통신(인증, 방문 레코드 업로드) |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | 배터리 최적화(Doze) 예외 자동 요청 |

**런타임 권한 요청은 SDK가 자동으로 처리합니다.** `AdWonReceiverSdk.start()`에 현재 화면에
떠 있는 `Activity`(`hostActivity`)를 전달하면, SDK가 이 기기의 OS 버전에 맞는 필요 권한을
스스로 판단해 시스템 권한 다이얼로그를 직접 띄우고 결과까지 처리합니다 — 호스트 앱이 권한
목록을 따로 관리하거나 요청 결과 콜백을 구현할 필요가 없습니다.

```kotlin
sdk.start(
    serialId = "table-01",
    appCode = "발급받은 App Code",
    hostActivity = this // 현재 Activity — 권한/배터리 최적화 자동 요청에 사용됨
)
```

`hostActivity`를 전달하지 않으면 이 자동 요청이 생략되고 경고 로그만 남습니다 — 이 경우
필요할 때 `sdk.requestRequiredPermissions(activity)`를 직접 호출하면 됩니다.
`sdk.hasRequiredPermissions()`로 현재 권한 보유 여부만 따로 확인할 수도 있습니다.

**중요**: 배터리 최적화 예외(`REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`)도 같은 `hostActivity`를
통해 SDK가 자동으로 요청합니다(4절 `requestBatteryOptimizationExemption` 참조). 이 권한은
Google Play 정책상 제한된 카테고리에서만 허용되므로, 호스트 앱을 퍼블릭 Play 스토어로
배포할 경우 정책 적합성을 별도로 확인해야 합니다.

### 4. 공개 API 레퍼런스

```kotlin
val sdk = AdWonReceiverSdk(context) // 로깅 없이 최소 연동, logger 기본값은 NoOpLogger
val sdk = AdWonReceiverSdk(context, myLogger) // Logger를 직접 주입하고 싶을 때
```

```kotlin
fun start(
    serialId: String,
    appCode: String,
    hostActivity: Activity? = null,
    listener: ReceiverStartListener = ReceiverStartListener {}
)

fun stop()
fun setOnLowBatteryListener(listener: (batteryPercent: Int) -> Unit)
fun setOnScanHealthWarningListener(listener: (consecutiveZeroCycles: Int) -> Unit)
fun hasRequiredPermissions(): Boolean
fun requestRequiredPermissions(activity: Activity, onResult: (allGranted: Boolean) -> Unit = {})
fun isBatteryOptimizationExempted(): Boolean
fun requestBatteryOptimizationExemption(activity: Activity)
```

| 파라미터 | 설명 |
|---|---|
| `serialId` | 이 수신부(테이블)를 식별하는 시리얼ID. |
| `appCode` | **(필수)** 호출 앱(파트너)이 SDK 사용 승인 시 발급받은 App Code. **유효하지 않은 값을 넣으면 실제로 거부됩니다.** 정식 App Code는 별도 채널로 발급받으세요. |
| `hostActivity` | 전달하면 SDK가 부족한 런타임 권한과 배터리 최적화 예외를 이 `Activity` 위에서 자동으로 요청합니다(3절 참조). `null`이면 두 자동 요청을 모두 생략하고 경고 로그만 남기므로, 필요하면 `requestRequiredPermissions()`/`requestBatteryOptimizationExemption()`을 직접 호출해야 합니다. |
| `listener` | 시작 판정 결과 콜백. **항상 메인 스레드**에서 호출됩니다 — 5절 `ReceiverStartResult` 참조. |

호출 앱이 조정할 수 있는 값은 `serialId`/`appCode`/`hostActivity`/`listener` 네 개뿐입니다.
그 외 신호 판별 기준, 신호 집계 윈도우, 체류/이탈 판정 시간, 방문 레코드 업로드 주기,
배터리 임계값 등은 모두 배포 시점 상수로 고정되어 있으며, 매장 환경에 맞게 조정이
필요하면 SDK 쪽에 문의해 주세요.

**`start()`는 동기로 즉시 완료되지 않습니다.** 내부 인증 캐시가 없거나 만료됐으면 서버
왕복이 필요하므로, 판정 결과는 `listener`로 비동기 전달됩니다(항상 메인 스레드에서 호출).
검증이 끝나기 전에 `start()`를 다시 호출하거나 `stop()`을 호출하면 이전 대기 중이던 검증은
취소되고 그 결과로는 스캔이 시작되지 않습니다.

`stop()`은 스캔/서비스를 중지하고 진행 중인 검증도 함께 취소합니다.

`setOnLowBatteryListener`는 배터리가 위험 수준까지 떨어졌을 때 호출됩니다.

**`setOnScanHealthWarningListener`**: 주기적 진단 스캔이 연속 여러 회 동안 주변 BLE 광고를
전혀 받지 못했을 때 호출됩니다 — "매장이 비어서 감지가 0건"이 아니라 "이 기기의 BLE 스캔
자체가 멈춘 것으로 의심됨"이라는 신호입니다. SDK가 스스로 복구할 수 없는 OS/제조사 레벨
문제(예: 제조사 자체 배터리 관리로 인한 백그라운드 강제 종료)일 수 있어, 운영자 알림 등으로
사람이 직접 기기를 점검하게 하는 용도로 제공됩니다.

`hasRequiredPermissions()` / `requestRequiredPermissions(activity, onResult)`와
`isBatteryOptimizationExempted()` / `requestBatteryOptimizationExemption(activity)`는 각각
런타임 권한과 배터리 최적화 예외를 확인/재요청하는 보조 API입니다 — `start()`가
`hostActivity` 없이 호출됐거나 사용자가 설정을 되돌린 경우를 위한 것으로, 보통은 직접 호출할
필요가 없습니다.

`receiverDiagnosticLogFile(context): File` — SDK 내부 진단 로그 파일 위치를 알려주는
top-level 함수. 6.2절 참조.

### 5. 데이터 모델

```kotlin
fun interface ReceiverStartListener {
    fun onResult(result: ReceiverStartResult)
}

sealed class ReceiverStartResult {
    object Started : ReceiverStartResult()
    data class Rejected(val reason: String) : ReceiverStartResult()
    data class ValidationError(val message: String) : ReceiverStartResult()
}
```

`ReceiverStartResult`는 `start()`의 `listener`로 전달되는 판정 결과입니다.

- `Started` — 정상적으로 스캔/수신 파이프라인이 시작됨.
- `Rejected(reason)` — 서버가 명시적으로 거부함(구체적인 거부 사유가 `reason`에 담겨
  전달됩니다).
- `ValidationError(message)` — 네트워크 오류 등으로 판정을 받지 못해 차단됨.

### 6. Logger 연동

#### 6.1 기본 사용법

```kotlin
fun interface Logger {
    fun log(level: Level, tag: String, message: String, throwable: Throwable?)
    enum class Level { DEBUG, INFO, WARN, ERROR }
}
```

- Logger 주입은 **선택 사항**입니다 — 아무것도 넘기지 않으면 아무 동작도 하지 않는
  `NoOpLogger`가 기본값이라, 로깅에 관심 없다면 별도 연동 없이 `AdWonReceiverSdk(context)`만
  호출해도 됩니다.
- `android.util.Log`로 실제 출력하는 기본 제공 구현체 `LogcatLogger`도 있어, 커스텀 로깅
  싱크가 필요 없다면 `AdWonReceiverSdk(context, LogcatLogger)`처럼 그대로 주입해 쓸 수
  있습니다.
- SDK가 남기는 모든 태그에는 `"adwon_ble:"` prefix가 붙어(`adwon_ble:AdWonReceiverSdk` 등)
  `adb logcat | grep adwon_ble` (PowerShell에서는 `adb logcat | findstr adwon_ble`)로 SDK
  로그만 걸러볼 수 있습니다.

호스트가 주입한 `Logger`가 받는 로그와는 별개로, SDK는 배포 시점에 정해지는 내부 전용
진단 로그(파일 기록)를 갖고 있습니다. 호스트는 이 내부 로그의 on/off를 알거나 바꿀 수
없습니다 — 대신 그 파일의 "위치"만 아래 방법으로 얻을 수 있습니다.

#### 6.2 진단 로그 파일 공유 가이드

```kotlin
val logFile = receiverDiagnosticLogFile(context)
```

이 함수는 앱 내부 저장소의 `filesDir/adwon_ble_logs/rx_log.txt`를 가리키는 `File`을
반환합니다. 실제로 로그를 기록할지는 SDK 배포 시점 상수로 결정되며 호스트가 알 수 없으므로,
**공유 전에 반드시 파일 존재/크기를 확인**해야 합니다.

```kotlin
if (!logFile.exists() || logFile.length() == 0L) {
    // "저장된 로그가 없습니다" 안내 후 종료
}
```

앱 내부 저장소 파일이라 Android 7+(API 24+)에서 `file://` URI를 그대로 노출하면
`FileUriExposedException`이 발생합니다. 반드시 **호스트 앱 자신의 `FileProvider`**로
`content://` URI로 감싸야 합니다 — SDK가 제공하는 FileProvider가 아니라, 호스트 앱마다
패키지명이 달라 호스트가 직접 선언해야 하는 부분입니다.

**1) `AndroidManifest.xml`**

```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

**2) `res/xml/file_paths.xml`**

```xml
<paths>
    <files-path name="ble_logs" path="adwon_ble_logs/" />
</paths>
```

**3) 공유 Intent**

```kotlin
val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", logFile)
val sendIntent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_STREAM, uri)
    putExtra(Intent.EXTRA_SUBJECT, "진단 로그")
    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
}
context.startActivity(Intent.createChooser(sendIntent, "진단 로그 공유"))
```

이 흐름은 SDK가 제공하는 기능이 아니라 "파일 위치만 알려주니 공유 UI는 호스트가 원하는
대로 구현하라"는 설계입니다. 전체 동작 예시는 부록 A 샘플 앱의 `MainActivity.shareLogFile()`을
참조하세요.

### 7. 배포 산출물 및 난독화 (ProGuard/R8)

수신부 SDK의 release AAR은 R8로 압축·난독화되어 배포됩니다. 4절에서 설명한 퍼블릭
API(파사드 클래스, `ReceiverStartResult`/`ReceiverStartListener`,
`Logger`/`NoOpLogger`/`LogcatLogger`, 판별 로직 `core` 패키지 등)는 이름이 그대로
유지되고, 그 외 내부 구현 클래스는 이름이 스크램블된 채로 담겨 있습니다.

호스트 앱이 별도로 해야 할 일은 없습니다 — AAR에는 `consumer-rules.pro`가 함께
번들링되어 있어, 호스트 앱이 자신의 코드를 R8/ProGuard로 빌드하더라도 위 퍼블릭 API와
`AndroidManifest.xml` 등록 컴포넌트가 자동으로 보호되어 다시 삭제·개명되지 않습니다.

### 8. 동작 예외 상황

| 상황 | 동작 |
|---|---|
| 필요 권한이 없는 상태로 `start()` 호출 | `hostActivity`가 전달됐으면 SDK가 자동으로 권한을 요청합니다(3절). 그래도 거부되거나 `hostActivity`가 없으면 WARN 로그만 남기고 스캔은 OS 레벨에서 조용히 실패할 수 있습니다. |
| App Code가 유효하지 않다고 서버가 명시적으로 응답 | `listener`로 `ReceiverStartResult.Rejected(reason)`이 전달되고 스캔이 시작되지 않습니다. |
| 서버 인증 확인 중 네트워크 오류 | 직전에 유효했던 판정이 유예 기간 내면 자동으로 `Started` 처리되고, 유예 기간을 넘겼거나 유효 이력이 없으면 `ValidationError`로 차단됩니다. |
| 앱 프로세스가 강제 종료/재부팅됨 | 재부팅 또는 앱 업데이트를 감지해 마지막 세션 설정으로 자동 재시작합니다. 재기동 전 진행 중이던 체류 건의 체크인 시각도 로컬 영속화된 값으로 복구됩니다. |
| 호스트 앱 태스크가 최근 앱 목록에서 스와이프로 제거됨 | 서비스가 함께 종료되지 않고 24시간 상시 동작을 유지하도록 고정돼 있습니다. 다만 제조사 자체 배터리 관리 기능(일부 제조사)에 의한 강제 종료까지 막지는 못합니다 — 아래 운영 가이드 참조. |
| 진단 스캔이 연속 여러 회 무신호 | `setOnScanHealthWarningListener`로 통지됩니다 — SDK가 자체 복구할 수 없는 상태일 수 있습니다. |
| 방문 레코드 업로드 실패 | 실패한 건만 로컬 큐에 남아 자동으로 재시도됩니다(재시도 간격은 실패할수록 점점 길어짐, 최대 1시간). 성공한 건은 즉시 큐에서 제거되어 중복 전송되지 않습니다. |

수신부가 예상대로 동작하지 않을 때(특히 "로그가 갑자기 안 보인다")의 실전 점검 순서는
별도 운영 가이드 문서(`AdWon_BLE_SDK_수신부_로그_점검_가이드.md`)를 참조하세요.

### 9. 버전 정보

| 연동규격서 버전 | SDK 배포 버전(AAR) | 비고 |
|---|---|---|
| v1.0 | 1.0.0 | 최초 공개 배포. **테스트(개발) 서버를 사용하는 버전** — 위 상단 안내 참조. |

> **참고**: 상용 서버로 전환된 버전이 나오면 이 표에 새 행으로 추가되고 별도로
> 공지됩니다. 그 전까지 연동을 시작하신 분들은 새 버전 공지 시 AAR 교체가 필요합니다.

### 부록 A. 샘플 앱

`sample/` 디렉터리 — SDK 소스가 아니라 release AAR만 참조하는 최소 연동 예제
프로젝트입니다.

> **주의**: 이 부록에서 설명하는 샘플 앱 코드가 최신 API 변경사항을 모두 반영하지 못했을
> 수 있습니다 — 정확한 시그니처는 항상 4절을 기준으로 확인하세요.

- `app/build.gradle.kts`: AAR을 직접 참조하는 방식과 전이 의존성 선언 예시(2절)를 보여줍니다.
- `MainActivity.kt`: `AdWonReceiverSdk`의 생성·시작/중지·콜백 등록·진단 로그 공유까지,
  퍼블릭 API만으로 구성된 최소 연동 코드입니다.
- `AndroidManifest.xml` / `res/xml/file_paths.xml`: 6.2절 진단 로그 공유 가이드의 실제
  구현.

빌드 방법은 이 저장소 최상위 `README.md`를 참조하세요.

---

## 라이선스

[`LICENSE`](./LICENSE) 참조 — 오픈소스 라이선스가 아닙니다. 연동규격서에 따른 AAR
사용은 허용되지만, 소스 재배포·역공학 등은 별도 동의가 필요합니다.
