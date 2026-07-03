# APK 파일이 직접 포함되지 않은 이유

APK를 실제로 만들려면 다음 도구가 필요합니다.

```text
Android SDK
Android Build Tools
Gradle
Android Gradle Plugin
android.jar
aapt2
d8
apksigner
```

현재 ChatGPT 실행 환경에는 Java는 있지만 Android SDK, Gradle, sdkmanager, adb가 없습니다.  
또한 외부 인터넷 접속이 차단되어 있어 Google Android SDK를 다운로드할 수도 없습니다.

그래서 여기서 APK 바이너리 자체를 생성하는 것은 불가능합니다.

대신 이 패키지에는 다음을 포함했습니다.

```text
1. Android Studio에서 바로 열 수 있는 전체 소스
2. GitHub Actions 자동 APK 빌드 workflow
3. 로컬 Windows 빌드 스크립트
4. 설치/권한/Device Owner 안내 문서
```

가장 쉬운 방식은 GitHub Actions 자동빌드입니다.
