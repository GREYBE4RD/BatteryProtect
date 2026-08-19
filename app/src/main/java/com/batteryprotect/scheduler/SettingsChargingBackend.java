package com.batteryprotect.scheduler;

import android.content.Context;
import android.provider.Settings;

final class SettingsChargingBackend implements ChargingBackend {
    enum Namespace { GLOBAL, SECURE, SYSTEM }

    private final Namespace namespace;
    private final String key;
    private final int enabledValue;
    private final int disabledValue;
    private final String name;
    private final boolean experimental;

    SettingsChargingBackend(Namespace namespace, String key, int enabledValue, int disabledValue,
                            String name, boolean experimental) {
        this.namespace = namespace;
        this.key = key;
        this.enabledValue = enabledValue;
        this.disabledValue = disabledValue;
        this.name = name;
        this.experimental = experimental;
    }

    private String read(Context context) {
        switch (namespace) {
            case GLOBAL:
                return Settings.Global.getString(context.getContentResolver(), key);
            case SECURE:
                return Settings.Secure.getString(context.getContentResolver(), key);
            case SYSTEM:
                return Settings.System.getString(context.getContentResolver(), key);
            default:
                return null;
        }
    }

    @Override
    public boolean isSupported(Context context) {
        String value = read(context);
        if (value == null) return false;
        try {
            int numeric = Integer.parseInt(value.trim());
            return numeric == enabledValue || numeric == disabledValue;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean isEnabled(Context context) {
        String value = read(context);
        if (value == null) return false;
        try {
            return Integer.parseInt(value.trim()) == enabledValue;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean setEnabled(Context context, boolean enabled) {
        if (!BatteryProtection.hasPermission(context)) return false;
        int value = enabled ? enabledValue : disabledValue;
        try {
            switch (namespace) {
                case GLOBAL:
                    return Settings.Global.putInt(context.getContentResolver(), key, value);
                case SECURE:
                    return Settings.Secure.putInt(context.getContentResolver(), key, value);
                case SYSTEM:
                    return Settings.System.putInt(context.getContentResolver(), key, value);
                default:
                    return false;
            }
        } catch (SecurityException e) {
            return false;
        }
    }

    @Override public String getName() { return name; }
    @Override public String getControlDescription() {
        return namespace.name().toLowerCase() + ":" + key;
    }
    @Override public boolean isExperimental() { return experimental; }
}
