# 가장 쉬운 APK 자동 생성 방법: GitHub Actions

현재 ChatGPT 실행 서버에는 Android SDK/Gradle이 없어서 여기서 APK 바이너리를 직접 컴파일할 수 없습니다.  
대신 이 패키지에는 GitHub가 자동으로 APK를 만들어 주는 workflow가 포함되어 있습니다.

## 준비물

- GitHub 계정
- 이 압축파일

## 방법

### 1. GitHub에서 새 저장소 만들기

GitHub 접속 후:

```text
New repository
→ 이름 예: quickschool-android-agent
→ Public 또는 Private 아무거나
→ Create repository
```

### 2. 압축파일 내용을 저장소에 업로드

이 폴더 안의 모든 파일을 GitHub 저장소에 업로드합니다.

반드시 아래 파일이 포함되어야 합니다.

```text
.github/workflows/build-apk.yml
settings.gradle
build.gradle
app/build.gradle
app/src/main/AndroidManifest.xml
```

### 3. Actions 탭 열기

GitHub 저장소에서:

```text
Actions
→ Build QuickSchool Android Agent APK
→ Run workflow
```

또는 파일을 업로드하면 자동으로 실행됩니다.

### 4. APK 다운로드

빌드가 완료되면:

```text
Actions
→ 완료된 build 클릭
→ Artifacts
→ QuickSchool-Android-Board-Agent-v23.1-debug-apk 다운로드
```

압축을 풀면 APK가 있습니다.

```text
QuickSchool-Android-Board-Agent-v23.1-debug.apk
```

이 APK를 전자칠판에 설치하면 됩니다.

## APK 설치 후

앱 실행 후:

```text
1. CMS 서버 URL 입력
2. 설정 저장 + Agent 시작
3. Overlay 권한 허용
4. 배터리 최적화 제외
5. Player 전체화면 실행
```

## 참고

Debug APK는 테스트/현장검증용입니다.  
학교 현장 정식 배포용은 나중에 release signing을 적용해야 합니다.
