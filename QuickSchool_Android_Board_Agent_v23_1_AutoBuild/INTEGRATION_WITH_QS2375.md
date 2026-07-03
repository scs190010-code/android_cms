# QS2375 CMS 연동 안내

## Agent 등록

Agent는 다음 API를 호출합니다.

```http
POST /api/device/register
```

body 예시:

```json
{
  "deviceId": "android-board-xxxx",
  "browserId": "android-board-xxxx",
  "name": "Android Board",
  "role": "android-board-agent",
  "autoRegister": true
}
```

## 명령 수신

```http
GET /api/player/{deviceId}
```

명령 배열 `commands`에서 다음 type을 처리합니다.

```text
emergency
clearEmergency
subtitle
clearSubtitle
blackout
clearBlackout
refresh
publish
appRestart
```

## ACK

```http
POST /api/command/ack
{
  "commandId": "cmd_xxx",
  "deviceId": "android-board-xxx"
}
```
