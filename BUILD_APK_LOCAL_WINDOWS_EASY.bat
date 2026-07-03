@echo off
chcp 65001 >nul
echo ============================================================
echo QuickSchool Android Board Agent - Local APK Build
echo ============================================================
echo.
echo 이 스크립트는 PC에 Android Studio 또는 Android SDK/Gradle이 있을 때 동작합니다.
echo Android Studio가 없다면 GitHub Actions 자동빌드 방식을 쓰는 것이 더 쉽습니다.
echo.

set PROJECT_DIR=%~dp0
cd /d "%PROJECT_DIR%"

where gradle >nul 2>nul
if errorlevel 1 (
  echo [오류] gradle 명령을 찾지 못했습니다.
  echo.
  echo 가장 쉬운 방법:
  echo 1. Android Studio 설치
  echo 2. 이 폴더 열기
  echo 3. Build APKs 클릭
  echo.
  echo 또는 GitHub Actions 자동빌드를 사용하세요:
  echo ONE_CLICK_GITHUB_APK_BUILD_KR.md 문서 참고
  echo.
  pause
  exit /b 1
)

echo Gradle 빌드 시작...
gradle :app:assembleDebug --no-daemon --stacktrace
if errorlevel 1 (
  echo.
  echo [실패] APK 빌드 실패
  pause
  exit /b 1
)

echo.
echo [성공] APK 생성 완료:
echo %PROJECT_DIR%app\build\outputs\apk\debug\app-debug.apk
echo.
pause
