package com.batteryprotect.scheduler;

import android.content.Context;
import android.content.SharedPreferences;

final class Prefs {
    private static final String FILE = "battery_protect";
    private static final String ENABLED = "enabled";
    private static final String START_HOUR = "start_hour";
    private static final String START_MIN = "start_min";
    private static final String END_HOUR = "end_hour";
    private static final String END_MIN = "end_min";

    private Prefs() {}

    private static SharedPreferences p(Context c) {
        return c.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    static boolean enabled(Context c) { return p(c).getBoolean(ENABLED, false); }
    static void setEnabled(Context c, boolean v) { p(c).edit().putBoolean(ENABLED, v).apply(); }
    static int startHour(Context c) { return p(c).getInt(START_HOUR, 22); }
    static int startMinute(Context c) { return p(c).getInt(START_MIN, 0); }
    static int endHour(Context c) { return p(c).getInt(END_HOUR, 6); }
    static int endMinute(Context c) { return p(c).getInt(END_MIN, 15); }
    static void setStart(Context c, int h, int m) { p(c).edit().putInt(START_HOUR,h).putInt(START_MIN,m).apply(); }
    static void setEnd(Context c, int h, int m) { p(c).edit().putInt(END_HOUR,h).putInt(END_MIN,m).apply(); }
}
