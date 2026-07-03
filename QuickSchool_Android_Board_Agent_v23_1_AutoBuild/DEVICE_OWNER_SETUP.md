# Device Owner / Kiosk 등록

## ADB 등록

공장초기화 직후 또는 계정 등록 전 상태에서:

```bash
adb install app-debug.apk
adb shell dpm set-device-owner kr.co.quickschool.agent/.QuickSchoolDeviceAdminReceiver
```

## Lock Task

앱 실행 후 `Kiosk / LockTask 시도` 버튼을 누르면 Device Owner 상태에서 LockTask를 시작합니다.

## 현장 권장

- 전자칠판 전용 운영이면 MDM 또는 Device Owner 등록
- 부팅 자동실행 허용
- 배터리 최적화 제외
- Overlay 권한 허용
- Player Activity를 기본 운영 화면으로 유지
