# QuickSchool Android Board Agent v23.1 Source

이 패키지는 안드로이드 전자칠판에서 브라우저 Player만으로 부족한 부분을 보완하기 위한 **Native Android Agent APK 소스 프로젝트**입니다.

## 핵심 기능

- Foreground Service로 CMS 서버 연결 유지
- `/api/device/register` 자동 기기등록
- `/api/player/{deviceId}` polling으로 명령 수신
- 긴급송출 수신 시 Android Overlay 표시
- 자막송출 수신 시 Android Overlay 표시
- clearEmergency / clearSubtitle 처리
- refresh / appRestart / publish 명령 시 Player Activity 전면 실행
- Player WebView 전체화면/Immersive Sticky 실행
- BOOT_COMPLETED 자동시작
- Overlay 권한 요청
- 배터리 최적화 제외 요청
- Device Owner / LockTask skeleton 포함

## 빌드 상태

현재 ChatGPT 실행 환경에는 Android SDK/Gradle이 없어 여기서 실제 APK 바이너리까지 컴파일하지 못했습니다.  
대신 Android Studio에서 바로 열어 빌드할 수 있는 전체 소스 프로젝트를 제공합니다.

## Android Studio 빌드

1. Android Studio 설치
2. 이 폴더 열기
3. Gradle Sync
4. Build > Build Bundle(s) / APK(s) > Build APK(s)
5. 생성 위치:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 현장 설치 후 필수 권한

전자칠판에서 앱 실행 후:

```text
1. CMS 서버 URL 입력
2. Overlay 권한 허용
3. 배터리 최적화 제외
4. Agent Service 시작
5. Player 전체화면 실행
```

## Overlay 권한

Android 15에서는 `SYSTEM_ALERT_WINDOW` 정책이 엄격합니다. 앱 설치 후 반드시 사용자가 Overlay 권한을 허용해야 다른 앱 위에 긴급송출이 뜹니다.

## Device Owner / Kiosk 권장

학교 전자칠판 상용 운영에서는 Device Owner / MDM 등록이 가장 안정적입니다.

ADB 등록 예시:

```bash
adb shell dpm set-device-owner kr.co.quickschool.agent/.QuickSchoolDeviceAdminReceiver
```

이미 Google 계정이 등록된 장비에서는 초기화 후 등록해야 할 수 있습니다.

## 서버 연동 API

필요 서버 API는 QS2375에 이미 포함되어 있습니다.

```http
POST /api/device/register
POST /api/device/heartbeat
GET  /api/player/{deviceId}
POST /api/command/ack
```

## 한계

Android 앱은 Android 화면 위에 Overlay를 띄울 수 있지만, HDMI/OPS 입력으로 화면이 전환된 상태에서 물리 입력을 Android로 강제 전환하는 것은 제조사 API, RS232, CEC, ADB, MDM API가 필요합니다.
