package kr.co.quickschool.agent;

import android.app.*;
import android.content.*;
import android.os.*;
import android.graphics.Color;
import java.util.*;
import org.json.*;

public class QuickSchoolAgentService extends Service {
    public static final String CHANNEL_ID = "quickschool_agent";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private volatile boolean running = false;
    private QuickSchoolClient client;
    private OverlayController overlay;
    private final Set<String> seenCommands = Collections.synchronizedSet(new LinkedHashSet<String>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) { return size() > 200; }
    });

    public static void start(Context c) {
        Intent i = new Intent(c, QuickSchoolAgentService.class);
        if (Build.VERSION.SDK_INT >= 26) c.startForegroundService(i);
        else c.startService(i);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createChannel();
        startForeground(1, buildNotification("QuickSchool Agent running"));
        client = new QuickSchoolClient(this);
        overlay = new OverlayController(this);
        running = true;
        registerAndLoop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        running = true;
        registerAndLoop();
        return START_STICKY;
    }

    private void registerAndLoop() {
        new Thread(() -> {
            try { client.registerDevice(); } catch (Exception e) { AgentLog.e("register failed", e); }
        }).start();
        handler.removeCallbacks(pollRunnable);
        handler.post(pollRunnable);
    }

    private final Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
            if (!running) return;
            new Thread(() -> {
                try {
                    client.heartbeat();
                    JSONObject payload = client.playerPayload();
                    JSONArray commands = payload.optJSONArray("commands");
                    if (commands != null) {
                        for (int i = 0; i < commands.length(); i++) {
                            JSONObject cmd = commands.optJSONObject(i);
                            if (cmd == null) continue;
                            String id = cmd.optString("id", cmd.optString("nonce", "") + cmd.optString("type", ""));
                            if (id.length() > 0 && seenCommands.contains(id)) continue;
                            if (id.length() > 0) seenCommands.add(id);
                            handleCommand(cmd);
                            if (id.length() > 0) client.ack(id);
                        }
                    }
                } catch (Exception e) {
                    AgentLog.e("poll failed", e);
                }
            }).start();
            handler.postDelayed(this, 1500);
        }
    };

    private void handleCommand(JSONObject cmd) {
        String type = cmd.optString("type", "");
        if ("emergency".equals(type) || "queue".equals(type)) {
            overlay.showEmergency(cmd);
            PlayerActivity.launch(this);
        } else if ("clearEmergency".equals(type)) {
            overlay.hideEmergency();
        } else if ("subtitle".equals(type)) {
            overlay.showSubtitle(cmd);
        } else if ("clearSubtitle".equals(type)) {
            overlay.hideSubtitle();
        } else if ("refresh".equals(type) || "appRestart".equals(type) || "publish".equals(type)) {
            PlayerActivity.launch(this);
        } else if ("blackout".equals(type)) {
            overlay.showBlackout(cmd);
        } else if ("clearBlackout".equals(type)) {
            overlay.hideBlackout();
        }
    }

    private Notification buildNotification(String text) {
        Intent launch = new Intent(this, SettingsActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, launch, PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder b = Build.VERSION.SDK_INT >= 26
                ? new Notification.Builder(this, CHANNEL_ID)
                : new Notification.Builder(this);
        return b.setContentTitle("QuickSchool Board Agent")
                .setContentText(text)
                .setSmallIcon(getApplicationInfo().icon == 0 ? android.R.drawable.ic_dialog_alert : getApplicationInfo().icon)
                .setContentIntent(pi)
                .setOngoing(true)
                .setColor(Color.rgb(6, 182, 212))
                .build();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID, getString(R.string.agent_channel_name), NotificationManager.IMPORTANCE_LOW);
            ch.setDescription("QuickSchool CMS 연결 유지 및 긴급송출 수신");
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(ch);
        }
    }

    @Override
    public void onDestroy() {
        running = false;
        handler.removeCallbacks(pollRunnable);
        super.onDestroy();
        if (AppConfig.autoStart(this)) {
            handler.postDelayed(() -> QuickSchoolAgentService.start(this), 1000);
        }
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
