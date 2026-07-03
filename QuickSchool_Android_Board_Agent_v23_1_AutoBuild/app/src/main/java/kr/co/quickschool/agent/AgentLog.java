package kr.co.quickschool.agent;

import android.util.Log;

public final class AgentLog {
    private static final String TAG = "QSAgent";
    private AgentLog() {}
    public static void i(String msg) { Log.i(TAG, msg); }
    public static void w(String msg) { Log.w(TAG, msg); }
    public static void e(String msg, Throwable t) { Log.e(TAG, msg, t); }
}
