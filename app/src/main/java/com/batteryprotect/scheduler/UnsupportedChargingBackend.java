package com.batteryprotect.scheduler;

import android.content.Context;

final class UnsupportedChargingBackend implements ChargingBackend {
    @Override public boolean isSupported(Context context) { return false; }
    @Override public boolean isEnabled(Context context) { return false; }
    @Override public boolean setEnabled(Context context, boolean enabled) { return false; }
    @Override public String getName() { return "Unsupported"; }
    @Override public String getControlDescription() { return "No compatible control detected"; }
    @Override public boolean isExperimental() { return false; }
}
