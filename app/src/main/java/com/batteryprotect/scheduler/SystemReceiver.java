package com.batteryprotect.scheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SystemReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Prefs.enabled(context)) {
            Scheduler.reschedule(context, true);
        }
    }
}
