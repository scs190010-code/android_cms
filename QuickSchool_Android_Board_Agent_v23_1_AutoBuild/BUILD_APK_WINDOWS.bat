@echo off
chcp 65001 >nul
echo ==================================================
echo QuickSchool Android Board Agent APK Build
echo ==================================================
echo Android Studio / Android SDK / Gradle 환경이 필요합니다.
echo.
if exist gradlew.bat (
  call gradlew.bat assembleDebug
) else (
  echo gradlew.bat가 없습니다.
  echo Android Studio에서 프로젝트를 열고 Gradle Sync 후 Build APK를 실행하세요.
  echo 또는 Gradle 설치 후: gradle wrapper && gradlew.bat assembleDebug
)
echo.
pause
