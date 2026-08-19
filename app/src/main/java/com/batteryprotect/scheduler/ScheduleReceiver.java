package com.batteryprotect.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScheduleReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Prefs.enabled(context) || !BatteryProtection.hasPermission(context) || !BatteryProtection.isSupported(context)) return;

        String action = intent.getAction();
        if (Scheduler.ACTION_ON.equals(action)) {
            BatteryProtection.setEnabled(context, true);
        } else if (Scheduler.ACTION_OFF.equals(action)) {
            BatteryProtection.setEnabled(context, false);
        }

        Scheduler.reschedule(context, false);
    }
}
