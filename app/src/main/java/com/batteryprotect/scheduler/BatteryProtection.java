package com.batteryprotect.scheduler;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

final class BatteryProtection {
    private BatteryProtection() {}

    static boolean hasPermission(Context context) {
        return context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS)
                == PackageManager.PERMISSION_GRANTED;
    }

    static ChargingBackend backend(Context context) {
        return ChargingBackends.detect(context);
    }

    static boolean isSupported(Context context) {
        return backend(context).isSupported(context);
    }

    static boolean isEnabled(Context context) {
        return backend(context).isEnabled(context);
    }

    static boolean setEnabled(Context context, boolean enabled) {
        return backend(context).setEnabled(context, enabled);
    }
}
