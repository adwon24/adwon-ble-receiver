package com.adwon.ble.receiver.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.adwon.ble.receiver.AdWonReceiverSdk
import com.adwon.ble.receiver.ReceiverStartListener
import com.adwon.ble.receiver.ReceiverStartResult
import com.adwon.ble.receiver.internal.common.LogcatLogger
import com.adwon.ble.receiver.receiverDiagnosticLogFile

/**
 * 수신부 SDK(`adwon-ble-receiver`) 최소 연동 예제.
 *
 * 이 샘플이 참조하는 건 release AAR(R8로 압축·난독화됨)뿐이다 — SDK 소스는 포함하지
 * 않는다. 여기서 쓰는 API는 전부 연동규격서 4절 "공개 API 레퍼런스"에 나온 것과
 * 정확히 같다. 정확한 시그니처/파라미터 의미는 항상 그 문서를 기준으로 확인할 것.
 *
 * (주의) 위 `LogcatLogger` import는 예시일 뿐이다 — `android.util.Log`로 그대로
 * 출력하는 SDK 기본 제공 구현체이며, 로깅이 필요 없다면 `AdWonReceiverSdk(context)`
 * 한 줄로 충분하다(연동규격서 6.1절).
 */
class MainActivity : AppCompatActivity() {

    // 데모 목적의 하드코딩 값. 실제 연동에서는 테이블마다 고유한 값을 넣어야 한다.
    private val demoSerialId = "table-01"

    // 유효하지 않은 값을 넣으면 실제로 거부된다(연동규격서 4절) — 정식 App Code는
    // 별도 채널로 발급받아 교체해야 한다.
    private val demoAppCode = "e80a61a8-b924-4c17-bb3c-d8dafe27f106" // 테스트 키입니다. 여기에_발급받은_App_Code를_넣으세요

    // Logger 주입은 선택 사항이다 — 아무것도 넘기지 않으면 아무 동작도 하지 않는
    // NoOpLogger가 기본값이다(연동규격서 6.1절).
    //
    // (버그 수정) 이걸 프로퍼티 초기화식(= 클래스 본문에서 바로 대입)으로 두면 Activity의
    // 생성자에서 실행되는데, 이 시점엔 아직 Android 프레임워크가 Context를 붙이기(attach)
    // 전이라 `this`가 완전한 Context가 아니다 — AdWonReceiverSdk 생성자 내부의
    // `context.applicationContext` 호출이 NullPointerException을 낸다("Attempt to invoke
    // virtual method ... getApplicationContext() on a null object reference"). onCreate()
    // 시점에는 Context가 이미 붙어있어 안전하므로, 선언만 여기서 하고 초기화는 onCreate()로
    // 옮긴다.
    private lateinit var sdk: AdWonReceiverSdk

    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sdk = AdWonReceiverSdk(this, LogcatLogger)

        tvStatus = findViewById(R.id.tvStatus)
        findViewById<Button>(R.id.btnStart).setOnClickListener { startReceiver() }
        findViewById<Button>(R.id.btnStop).setOnClickListener { stopReceiver() }
        findViewById<Button>(R.id.btnShareLog).setOnClickListener { shareLogFile() }

        // start()를 굳이 호출하지 않아도 현재 권한/배터리 최적화 예외 보유 여부만 먼저
        // 확인해볼 수 있다(연동규격서 4절 보조 API).
        updateStatus(
            "권한: ${sdk.hasRequiredPermissions()} / " +
                "배터리 최적화 예외: ${sdk.isBatteryOptimizationExempted()}"
        )
    }

    private fun startReceiver() {
        // hostActivity로 this를 넘기면, 부족한 런타임 권한과 배터리 최적화 예외를 SDK가
        // 이 Activity 위에서 직접 자동으로 요청한다 — 별도 권한 요청 코드가 필요 없다
        // (연동규격서 3절).
        sdk.start(
            serialId = demoSerialId,
            appCode = demoAppCode,
            hostActivity = this,
            listener = ReceiverStartListener { result ->
                // 항상 메인 스레드에서 호출되므로 곧바로 UI를 갱신해도 안전하다.
                when (result) {
                    is ReceiverStartResult.Started ->
                        updateStatus("시작됨")
                    is ReceiverStartResult.Rejected ->
                        updateStatus("거부됨: ${result.reason}")
                    is ReceiverStartResult.ValidationError ->
                        updateStatus("검증 실패: ${result.message}")
                }
            }
        )

        sdk.setOnLowBatteryListener { batteryPercent ->
            Toast.makeText(this, "배터리 부족: $batteryPercent%", Toast.LENGTH_SHORT).show()
        }

        sdk.setOnScanHealthWarningListener { consecutiveZeroCycles ->
            // 주변 BLE 광고를 연속으로 못 받았다는 신호 — "매장이 비어서 0건"이 아니라
            // 이 기기의 스캔 자체가 멈췄을 수 있다는 의미다(연동규격서 4절). 실제
            // 운영에서는 담당자 알림 등으로 사람이 기기를 점검하게 하는 용도.
            Toast.makeText(
                this,
                "스캔 헬스 경고: 연속 $consecutiveZeroCycles 회 무신호",
                Toast.LENGTH_LONG
            ).show()
        }

        updateStatus("시작 요청됨 — 판정 대기 중")
    }

    private fun stopReceiver() {
        sdk.stop()
        updateStatus("중지됨")
    }

    /**
     * 진단 로그 파일 공유 예시(연동규격서 6.2절). 앱 내부 저장소 파일이라 이 앱 자신의
     * FileProvider로 content:// URI로 감싸야 한다 — SDK가 제공하는 FileProvider가
     * 아니다.
     */
    private fun shareLogFile() {
        val logFile = receiverDiagnosticLogFile(this)
        if (!logFile.exists() || logFile.length() == 0L) {
            Toast.makeText(this, "저장된 로그가 없습니다", Toast.LENGTH_SHORT).show()
            return
        }

        val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", logFile)
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "진단 로그")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(sendIntent, "진단 로그 공유"))
    }

    private fun updateStatus(message: String) {
        tvStatus.text = message
    }
}
