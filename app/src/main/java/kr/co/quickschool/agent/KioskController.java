package kr.co.quickschool.agent;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.widget.Toast;

public final class KioskController {
    private KioskController() {}

    public static boolean isDeviceOwner(Context c) {
        DevicePolicyManager dpm = (DevicePolicyManager) c.getSystemService(Context.DEVICE_POLICY_SERVICE);
        return dpm != null && dpm.isDeviceOwnerApp(c.getPackageName());
    }

    public static void tryStartLockTask(Activity a) {
        try {
            if (isDeviceOwner(a)) {
                DevicePolicyManager dpm = (DevicePolicyManager) a.getSystemService(Context.DEVICE_POLICY_SERVICE);
                ComponentName admin = new ComponentName(a, QuickSchoolDeviceAdminReceiver.class);
                dpm.setLockTaskPackages(admin, new String[]{a.getPackageName()});
                a.startLockTask();
                Toast.makeText(a, "LockTask 시작", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(a, "Device Owner가 아닙니다. MDM/ADB 등록 필요", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(a, "LockTask 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
