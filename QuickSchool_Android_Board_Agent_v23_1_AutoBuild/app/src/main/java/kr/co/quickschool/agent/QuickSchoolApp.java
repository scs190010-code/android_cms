package kr.co.quickschool.agent;

import android.app.Application;

public class QuickSchoolApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AgentLog.i("QuickSchoolApp started");
    }
}
