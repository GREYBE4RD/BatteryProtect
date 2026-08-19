package com.batteryprotect.scheduler;

import android.content.Context;
import android.os.Build;

final class ChargingBackends {
    private ChargingBackends() {}

    // Ordered from confirmed to increasingly speculative. Experimental entries are only
    // accepted when the setting already exists on the device and is a boolean-like 0/1 value.
    private static final ChargingBackend[] CANDIDATES = new ChargingBackend[] {
            new SettingsChargingBackend(SettingsChargingBackend.Namespace.GLOBAL,
                    "protect_battery", 1, 0, "Samsung Battery Protection", false),

            // Conservative best-effort aliases seen or plausibly used by OEM battery-protection UIs.
            // These are intentionally limited to clear boolean names; no kernel/sysfs writes are attempted.
            new SettingsChargingBackend(SettingsChargingBackend.Namespace.GLOBAL,
                    "battery_protection", 1, 0, "OEM Battery Protection", true),
            new SettingsChargingBackend(SettingsChargingBackend.Namespace.SECURE,
                    "battery_protection", 1, 0, "OEM Battery Protection", true),
            new SettingsChargingBackend(SettingsChargingBackend.Namespace.GLOBAL,
                    "charging_protection", 1, 0, "OEM Charging Protection", true),
            new SettingsChargingBackend(SettingsChargingBackend.Namespace.SECURE,
                    "charging_protection", 1, 0, "OEM Charging Protection", true),
            new SettingsChargingBackend(SettingsChargingBackend.Namespace.GLOBAL,
                    "charge_limit", 1, 0, "OEM Charge Limit", true),
            new SettingsChargingBackend(SettingsChargingBackend.Namespace.SECURE,
                    "charge_limit", 1, 0, "OEM Charge Limit", true),
            new SettingsChargingBackend(SettingsChargingBackend.Namespace.GLOBAL,
                    "charging_limit", 1, 0, "OEM Charging Limit", true),
            new SettingsChargingBackend(SettingsChargingBackend.Namespace.SECURE,
                    "charging_limit", 1, 0, "OEM Charging Limit", true),
            new SettingsChargingBackend(SettingsChargingBackend.Namespace.GLOBAL,
                    "optimized_charging", 1, 0, "OEM Optimized Charging", true),
            new SettingsChargingBackend(SettingsChargingBackend.Namespace.SECURE,
                    "optimized_charging", 1, 0, "OEM Optimized Charging", true)
    };

    static ChargingBackend detect(Context context) {
        for (ChargingBackend backend : CANDIDATES) {
            if (backend.isSupported(context)) return backend;
        }
        return new UnsupportedChargingBackend();
    }

    static String deviceSummary() {
        return Build.MANUFACTURER + " " + Build.MODEL + " · Android " + Build.VERSION.RELEASE;
    }
}
