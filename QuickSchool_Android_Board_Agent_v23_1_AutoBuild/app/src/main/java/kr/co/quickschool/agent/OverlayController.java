package kr.co.quickschool.agent;

import android.content.*;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import org.json.JSONObject;

public class OverlayController {
    private final Context context;
    private final WindowManager wm;
    private View emergencyView;
    private View subtitleView;
    private View blackoutView;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public OverlayController(Context c) {
        context = c.getApplicationContext();
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void showEmergency(JSONObject cmd) {
        handler.post(() -> {
            if (!canOverlay()) return;
            hideEmergency();
            FrameLayout root = new FrameLayout(context);
            root.setBackgroundColor(parseColor(cmd.optString("bgColor", "#dc2626"), Color.rgb(220, 38, 38)));
            TextView tv = new TextView(context);
            tv.setText(cmd.optString("message", cmd.optString("title", "긴급 안내")));
            tv.setTextColor(parseColor(cmd.optString("textColor", "#ffffff"), Color.WHITE));
            tv.setTextSize(56);
            tv.setGravity(Gravity.CENTER);
            tv.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            root.addView(tv, new FrameLayout.LayoutParams(-1, -1));
            emergencyView = root;
            wm.addView(emergencyView, fullParams(true));
            wake();
        });
    }

    public void hideEmergency() {
        handler.post(() -> {
            if (emergencyView != null) {
                try { wm.removeView(emergencyView); } catch (Exception ignored) {}
                emergencyView = null;
            }
        });
    }

    public void showBlackout(JSONObject cmd) {
        handler.post(() -> {
            if (!canOverlay()) return;
            hideBlackout();
            FrameLayout root = new FrameLayout(context);
            root.setBackgroundColor(Color.BLACK);
            blackoutView = root;
            wm.addView(blackoutView, fullParams(false));
            wake();
        });
    }

    public void hideBlackout() {
        handler.post(() -> {
            if (blackoutView != null) {
                try { wm.removeView(blackoutView); } catch (Exception ignored) {}
                blackoutView = null;
            }
        });
    }

    public void showSubtitle(JSONObject cmd) {
        handler.post(() -> {
            if (!canOverlay()) return;
            hideSubtitle();
            JSONObject sub = cmd.optJSONObject("subtitle");
            if (sub == null) sub = cmd;
            String pos = sub.optString("position", "bottom");
            int gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            if ("top".equals(pos)) gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            if ("left".equals(pos)) gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            if ("right".equals(pos)) gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            if ("center".equals(pos)) gravity = Gravity.CENTER;

            TextView tv = new TextView(context);
            tv.setText(sub.optString("text", cmd.optString("message", "")));
            tv.setTextColor(parseColor(sub.optString("textColor", "#ffffff"), Color.WHITE));
            tv.setTextSize((float) sub.optDouble("fontSize", 48));
            tv.setGravity(Gravity.CENTER);
            tv.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            tv.setSingleLine(true);
            tv.setPadding(24, 14, 24, 14);
            tv.setBackgroundColor(Color.argb(180, 0, 0, 0));
            subtitleView = tv;

            WindowManager.LayoutParams lp = overlayParams(false);
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            if ("left".equals(pos) || "right".equals(pos)) {
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                tv.setSingleLine(false);
            }
            lp.gravity = gravity;
            wm.addView(subtitleView, lp);
            wake();

            long duration = Math.max(1, sub.optLong("durationSec", 30)) * 1000L;
            handler.postDelayed(this::hideSubtitle, duration);
        });
    }

    public void hideSubtitle() {
        handler.post(() -> {
            if (subtitleView != null) {
                try { wm.removeView(subtitleView); } catch (Exception ignored) {}
                subtitleView = null;
            }
        });
    }

    private boolean canOverlay() {
        return Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(context);
    }

    private WindowManager.LayoutParams fullParams(boolean focusable) {
        WindowManager.LayoutParams lp = overlayParams(focusable);
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;
        return lp;
    }

    private WindowManager.LayoutParams overlayParams(boolean focusable) {
        int type = Build.VERSION.SDK_INT >= 26
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;
        int flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        if (!focusable) flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        return new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                type,
                flags,
                PixelFormat.TRANSLUCENT
        );
    }

    private int parseColor(String v, int fallback) {
        try { return Color.parseColor(v); }
        catch (Exception e) { return fallback; }
    }

    private void wake() {
        try {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    "QuickSchool:OverlayWake");
            wl.acquire(5000);
        } catch (Exception ignored) {}
    }
}
