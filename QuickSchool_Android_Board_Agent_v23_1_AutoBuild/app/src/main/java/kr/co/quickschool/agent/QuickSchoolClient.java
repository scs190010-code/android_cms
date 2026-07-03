package kr.co.quickschool.agent;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import org.json.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class QuickSchoolClient {
    private final Context context;

    public QuickSchoolClient(Context c) { context = c.getApplicationContext(); }

    public JSONObject registerDevice() throws Exception {
        JSONObject b = baseDeviceJson();
        b.put("autoRegister", true);
        return post("/api/device/register", b);
    }

    public JSONObject heartbeat() throws Exception {
        JSONObject b = baseDeviceJson();
        b.put("playback", new JSONObject().put("agent", "android-apk").put("version", "23.1"));
        return post("/api/device/heartbeat", b);
    }

    public JSONObject playerPayload() throws Exception {
        String id = UriUtil.enc(AppConfig.deviceId(context));
        return get("/api/player/" + id);
    }

    public JSONObject ack(String commandId) {
        try {
            JSONObject b = new JSONObject();
            b.put("commandId", commandId);
            b.put("deviceId", AppConfig.deviceId(context));
            return post("/api/command/ack", b);
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    private JSONObject baseDeviceJson() throws Exception {
        JSONObject b = new JSONObject();
        b.put("deviceId", AppConfig.deviceId(context));
        b.put("browserId", AppConfig.deviceId(context));
        b.put("name", AppConfig.deviceName(context));
        b.put("role", "android-board-agent");
        b.put("userAgent", "QuickSchoolAndroidBoardAgent/23.1 Android/" + android.os.Build.VERSION.RELEASE);
        b.put("screen", screenJson());
        return b;
    }

    private JSONObject screenJson() throws Exception {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealMetrics(dm);
        return new JSONObject()
                .put("w", dm.widthPixels)
                .put("h", dm.heightPixels)
                .put("dpr", dm.density)
                .put("orientation", dm.widthPixels >= dm.heightPixels ? "landscape" : "portrait");
    }

    private JSONObject get(String path) throws Exception {
        HttpURLConnection c = open(path, "GET");
        return readJson(c);
    }

    private JSONObject post(String path, JSONObject body) throws Exception {
        HttpURLConnection c = open(path, "POST");
        c.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        c.setDoOutput(true);
        try (OutputStream os = c.getOutputStream()) {
            os.write(body.toString().getBytes(StandardCharsets.UTF_8));
        }
        return readJson(c);
    }

    private HttpURLConnection open(String path, String method) throws Exception {
        URL url = new URL(AppConfig.serverUrl(context) + path);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        c.setRequestMethod(method);
        c.setConnectTimeout(5000);
        c.setReadTimeout(10000);
        c.setRequestProperty("User-Agent", "QuickSchoolAndroidBoardAgent/23.1");
        return c;
    }

    private JSONObject readJson(HttpURLConnection c) throws Exception {
        int code = c.getResponseCode();
        InputStream is = code >= 200 && code < 400 ? c.getInputStream() : c.getErrorStream();
        String s = readAll(is);
        if (s == null || s.isEmpty()) return new JSONObject().put("ok", false).put("status", code);
        return new JSONObject(s);
    }

    private String readAll(InputStream is) throws Exception {
        if (is == null) return "";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = is.read(buf)) > 0) bos.write(buf, 0, n);
        return bos.toString("UTF-8");
    }
}
