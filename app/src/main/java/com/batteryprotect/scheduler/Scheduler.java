package com.batteryprotect.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

final class Scheduler {
    static final String ACTION_ON = "com.batteryprotect.scheduler.PROTECT_ON";
    static final String ACTION_OFF = "com.batteryprotect.scheduler.PROTECT_OFF";
    private static final int REQUEST_ON = 1001;
    private static final int REQUEST_OFF = 1002;

    private Scheduler() {}

    static boolean canScheduleExactAlarms(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        return am.canScheduleExactAlarms();
    }

    static void reschedule(Context context, boolean applyNow) {
        cancel(context);
        if (!Prefs.enabled(context) || !canScheduleExactAlarms(context) || !BatteryProtection.isSupported(context)) return;

        if (applyNow && BatteryProtection.hasPermission(context) && BatteryProtection.isSupported(context)) {
            BatteryProtection.setEnabled(context, shouldProtectNow(context));
        }

        scheduleNext(context, ACTION_ON, REQUEST_ON,
                Prefs.startHour(context), Prefs.startMinute(context));
        scheduleNext(context, ACTION_OFF, REQUEST_OFF,
                Prefs.endHour(context), Prefs.endMinute(context));
    }

    static void cancel(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pending(context, ACTION_ON, REQUEST_ON));
        am.cancel(pending(context, ACTION_OFF, REQUEST_OFF));
    }

    static boolean shouldProtectNow(Context context) {
        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.of(Prefs.startHour(context), Prefs.startMinute(context));
        LocalTime end = LocalTime.of(Prefs.endHour(context), Prefs.endMinute(context));

        if (start.equals(end)) return true;
        if (start.isBefore(end)) {
            return !now.isBefore(start) && now.isBefore(end);
        }
        return !now.isBefore(start) || now.isBefore(end);
    }

    private static void scheduleNext(Context context, String action, int requestCode,
                                     int hour, int minute) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        LocalDate today = LocalDate.now();
        LocalDateTime when = LocalDateTime.of(today, LocalTime.of(hour, minute));
        if (!when.isAfter(LocalDateTime.now())) when = when.plusDays(1);
        long millis = when.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        PendingIntent operation = pending(context, action, requestCode);
        AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(
                millis, showIntent(context));
        am.setAlarmClock(info, operation);
    }

    private static PendingIntent pending(Context context, String action, int requestCode) {
        Intent i = new Intent(context, ScheduleReceiver.class).setAction(action);
        return PendingIntent.getBroadcast(context, requestCode, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private static PendingIntent showIntent(Context context) {
        Intent i = new Intent(context, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, 2001, i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}
