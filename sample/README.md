# AdWon BLE 수신부 SDK 연동 샘플 앱

수신부 SDK(`adwon-ble-receiver`)를 release AAR로 실제 앱에 붙이는 최소 연동 예제입니다.
SDK 소스는 포함하지 않습니다 — release AAR(R8로 압축·난독화됨)만 참조합니다.

## 1. 빌드/실행

이 샘플은 저장소 최상위 [`library/`](../library/)에 있는 release AAR을 상대 경로로 바로
참조합니다(`app/build.gradle.kts` 참조) — 별도로 AAR을 내려받아 넣을 필요가 없습니다.

```bash
./gradlew :app:assembleDebug
# 또는 Android Studio에서 이 sample/ 폴더를 열어 Run
```

## 이 샘플이 보여주는 것

- `app/build.gradle.kts`: AAR을 `implementation(files(...))`로 직접 참조하는 방식과,
  이 방식에서는 전이 의존성이 자동으로 따라오지 않아 `kotlinx-coroutines-android`/
  `androidx.work`를 호스트 쪽에서 직접 선언해야 한다는 점(연동규격서 2절 참조).
- `MainActivity.kt`: `AdWonReceiverSdk` 생성 → `start()`(현재 화면 `Activity`를 넘겨
  권한/배터리 최적화 자동 요청까지 위임) → 저배터리/스캔 헬스 경고 콜백 → 진단 로그 파일
  공유까지, 퍼블릭 API만으로 구성된 최소 연동 코드입니다. release AAR(R8 난독화 적용)만
  참조하고도 정상 컴파일·동작한다는 것 자체가 "내부 구현은 감춰지고 퍼블릭 API는 그대로
  쓸 수 있는지"에 대한 실측 검증이기도 합니다.
- `AndroidManifest.xml` / `res/xml/file_paths.xml`: 진단 로그 파일 공유 가이드(연동규격서
  6.2절)의 실제 구현.

> 정확한 API 시그니처와 각 파라미터의 의미는 항상 저장소 최상위 `README.md`를
> 기준으로 확인하세요 — 이 샘플은 그 문서가 설명하는 최소 사용법을 코드로 보여주는
> 용도입니다.
