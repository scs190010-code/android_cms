package kr.co.quickschool.agent;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import java.util.UUID;

public final class AppConfig {
    private static final String PREF = "quickschool_agent";
    private AppConfig() {}

    public static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public static String serverUrl(Context c) {
        return prefs(c).getString("serverUrl", BuildConfig.DEFAULT_SERVER_URL).replaceAll("/+$", "");
    }

    public static void setServerUrl(Context c, String url) {
        if (url == null || url.trim().isEmpty()) return;
        prefs(c).edit().putString("serverUrl", url.trim().replaceAll("/+$", "")).apply();
    }

    public static String deviceId(Context c) {
        String v = prefs(c).getString("deviceId", "");
        if (v == null || v.isEmpty()) {
            String androidId = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
            v = "android-board-" + (androidId == null ? UUID.randomUUID().toString() : androidId);
            prefs(c).edit().putString("deviceId", v).apply();
        }
        return v;
    }

    public static String deviceName(Context c) {
        String v = prefs(c).getString("deviceName", "");
        if (v == null || v.isEmpty()) {
            v = "Android Board " + android.os.Build.MODEL;
            prefs(c).edit().putString("deviceName", v).apply();
        }
        return v;
    }

    public static void setDeviceName(Context c, String name) {
        if (name == null || name.trim().isEmpty()) return;
        prefs(c).edit().putString("deviceName", name.trim()).apply();
    }

    public static boolean autoStart(Context c) {
        return prefs(c).getBoolean("autoStart", true);
    }

    public static void setAutoStart(Context c, boolean enabled) {
        prefs(c).edit().putBoolean("autoStart", enabled).apply();
    }

    public static String playerUrl(Context c) {
        return serverUrl(c) + "/player?id=" + deviceId(c) + "&name=" + UriUtil.enc(deviceName(c)) + "&androidfs=1&fullscreen=1&agent=apk";
    }
}
