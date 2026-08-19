package com.batteryprotect.scheduler;

import android.content.Context;

interface ChargingBackend {
    boolean isSupported(Context context);
    boolean isEnabled(Context context);
    boolean setEnabled(Context context, boolean enabled);
    String getName();
    String getControlDescription();
    boolean isExperimental();
}
