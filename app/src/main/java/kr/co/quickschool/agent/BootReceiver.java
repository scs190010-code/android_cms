package kr.co.quickschool.agent;

import android.content.*;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context c, Intent i) {
        if (AppConfig.autoStart(c)) {
            QuickSchoolAgentService.start(c);
        }
    }
}
