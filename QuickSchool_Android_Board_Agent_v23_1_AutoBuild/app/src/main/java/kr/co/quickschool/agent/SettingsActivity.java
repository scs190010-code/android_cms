package kr.co.quickschool.agent;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.graphics.Color;

public class SettingsActivity extends Activity {
    private EditText serverInput;
    private EditText nameInput;
    private TextView status;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        buildUi();
        requestNotificationPermission();
        QuickSchoolAgentService.start(this);
    }

    private void buildUi() {
        ScrollView sv = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 32, 32, 32);
        sv.addView(root);

        TextView title = new TextView(this);
        title.setText("QuickSchool Android Board Agent v23.1");
        title.setTextSize(24);
        title.setTextColor(Color.rgb(2, 6, 23));
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        root.addView(title);

        serverInput = new EditText(this);
        serverInput.setHint("CMS 서버 URL 예: http://192.168.0.133:4500");
        serverInput.setText(AppConfig.serverUrl(this));
        root.addView(serverInput);

        nameInput = new EditText(this);
        nameInput.setHint("기기명");
        nameInput.setText(AppConfig.deviceName(this));
        root.addView(nameInput);

        Button save = new Button(this);
        save.setText("설정 저장 + Agent 시작");
        save.setOnClickListener(v -> {
            AppConfig.setServerUrl(this, serverInput.getText().toString());
            AppConfig.setDeviceName(this, nameInput.getText().toString());
            QuickSchoolAgentService.start(this);
            status.setText("저장 완료 / Agent 시작 요청");
        });
        root.addView(save);

        Button overlay = new Button(this);
        overlay.setText("Overlay 권한 열기");
        overlay.setOnClickListener(v -> openOverlayPermission());
        root.addView(overlay);

        Button battery = new Button(this);
        battery.setText("배터리 최적화 제외 요청");
        battery.setOnClickListener(v -> requestIgnoreBattery());
        root.addView(battery);

        Button player = new Button(this);
        player.setText("Player 전체화면 실행");
        player.setOnClickListener(v -> PlayerActivity.launch(this));
        root.addView(player);

        Button service = new Button(this);
        service.setText("Agent Service 재시작");
        service.setOnClickListener(v -> QuickSchoolAgentService.start(this));
        root.addView(service);

        Button kiosk = new Button(this);
        kiosk.setText("Kiosk / LockTask 시도");
        kiosk.setOnClickListener(v -> KioskController.tryStartLockTask(this));
        root.addView(kiosk);

        status = new TextView(this);
        status.setTextSize(14);
        status.setText(statusText());
        root.addView(status);

        setContentView(sv);
    }

    private String statusText() {
        boolean overlay = Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this);
        boolean owner = KioskController.isDeviceOwner(this);
        return "Device ID: " + AppConfig.deviceId(this)
                + "\nOverlay Permission: " + overlay
                + "\nDevice Owner: " + owner
                + "\nServer: " + AppConfig.serverUrl(this);
    }

    private void openOverlayPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(i);
        }
    }

    private void requestIgnoreBattery() {
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
                    Intent i = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                            Uri.parse("package:" + getPackageName()));
                    startActivity(i);
                }
            } catch (Exception e) {
                Toast.makeText(this, "배터리 최적화 요청 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
        }
    }
}
