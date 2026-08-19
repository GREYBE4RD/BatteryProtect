package com.batteryprotect.scheduler;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MainActivity extends Activity {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private TextView statusText;
    private LinearLayout permissionPanel;
    private Switch protectionSwitch;
    private Switch scheduleSwitch;
    private Button startTimeButton;
    private Button endTimeButton;
    private TextView detailsToggle;
    private LinearLayout detailsPanel;
    private TextView versionText;
    private TextView deviceText;
    private TextView backendText;
    private TextView controlText;
    private boolean binding;
    private boolean waitingForExactAlarmAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        permissionPanel = findViewById(R.id.permissionPanel);
        protectionSwitch = findViewById(R.id.protectionSwitch);
        scheduleSwitch = findViewById(R.id.scheduleSwitch);
        startTimeButton = findViewById(R.id.startTimeButton);
        endTimeButton = findViewById(R.id.endTimeButton);
        detailsToggle = findViewById(R.id.detailsToggle);
        detailsPanel = findViewById(R.id.detailsPanel);
        versionText = findViewById(R.id.versionText);
        deviceText = findViewById(R.id.deviceText);
        backendText = findViewById(R.id.backendText);
        controlText = findViewById(R.id.controlText);
        TextView adbCommand = findViewById(R.id.adbCommand);
        Button copyButton = findViewById(R.id.copyButton);

        String command = "adb shell pm grant " + getPackageName()
                + " android.permission.WRITE_SECURE_SETTINGS";
        adbCommand.setText(command);
        copyButton.setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText("ADB command", command));
            Toast.makeText(this, "Command copied", Toast.LENGTH_SHORT).show();
        });

        startTimeButton.setOnClickListener(v -> pickTime(true));
        endTimeButton.setOnClickListener(v -> pickTime(false));
        detailsToggle.setOnClickListener(v -> {
            boolean show = detailsPanel.getVisibility() != View.VISIBLE;
            detailsPanel.setVisibility(show ? View.VISIBLE : View.GONE);
            detailsToggle.setText(show ? "Details ▾" : "Details ▸");
        });

        protectionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (binding) return;
            if (!BatteryProtection.hasPermission(this)) {
                refresh();
                Toast.makeText(this, "Complete the one-time ADB setup first", Toast.LENGTH_LONG).show();
                return;
            }
            if (!BatteryProtection.isSupported(this)) {
                refresh();
                Toast.makeText(this, "No compatible charging control was detected", Toast.LENGTH_LONG).show();
                return;
            }
            if (!BatteryProtection.setEnabled(this, isChecked)) {
                Toast.makeText(this, "Could not change Battery Protection", Toast.LENGTH_LONG).show();
            }
            refresh();
        });

        scheduleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (binding) return;
            if (isChecked && !BatteryProtection.hasPermission(this)) {
                refresh();
                Toast.makeText(this, "Complete the one-time ADB setup first", Toast.LENGTH_LONG).show();
                return;
            }
            if (isChecked && !BatteryProtection.isSupported(this)) {
                refresh();
                Toast.makeText(this, "No compatible charging control was detected", Toast.LENGTH_LONG).show();
                return;
            }
            if (isChecked && !Scheduler.canScheduleExactAlarms(this)) {
                refresh();
                waitingForExactAlarmAccess = true;
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                Toast.makeText(this,
                        "Allow Alarms & reminders for accurate schedule timing",
                        Toast.LENGTH_LONG).show();
                return;
            }

            Prefs.setEnabled(this, isChecked);
            if (isChecked) {
                Scheduler.reschedule(this, true);
            } else {
                Scheduler.cancel(this);
                BatteryProtection.setEnabled(this, false);
            }
            refresh();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Prefs.enabled(this)
                && (!Scheduler.canScheduleExactAlarms(this) || !BatteryProtection.isSupported(this))) {
            Prefs.setEnabled(this, false);
            Scheduler.cancel(this);
        }

        if (waitingForExactAlarmAccess
                && Scheduler.canScheduleExactAlarms(this)
                && BatteryProtection.isSupported(this)) {
            waitingForExactAlarmAccess = false;
            Prefs.setEnabled(this, true);
            Scheduler.reschedule(this, true);
        }
        refresh();
    }

    private void pickTime(boolean start) {
        int hour = start ? Prefs.startHour(this) : Prefs.endHour(this);
        int minute = start ? Prefs.startMinute(this) : Prefs.endMinute(this);

        new TimePickerDialog(this, (view, h, m) -> {
            if (start) Prefs.setStart(this, h, m); else Prefs.setEnd(this, h, m);
            if (Prefs.enabled(this)) Scheduler.reschedule(this, true);
            refresh();
        }, hour, minute, true).show();
    }

    private void refresh() {
        boolean permission = BatteryProtection.hasPermission(this);
        ChargingBackend backend = BatteryProtection.backend(this);
        boolean supported = backend.isSupported(this);
        permissionPanel.setVisibility(permission ? View.GONE : View.VISIBLE);

        binding = true;
        boolean protection = supported && backend.isEnabled(this);
        protectionSwitch.setChecked(protection);
        protectionSwitch.setEnabled(permission && supported);
        scheduleSwitch.setChecked(Prefs.enabled(this));
        scheduleSwitch.setEnabled(permission && supported);
        binding = false;

        startTimeButton.setText(format(Prefs.startHour(this), Prefs.startMinute(this)));
        endTimeButton.setText(format(Prefs.endHour(this), Prefs.endMinute(this)));

        if (!permission) {
            statusText.setText("Setup required before charging control can be enabled");
        } else if (!supported) {
            statusText.setText("No compatible charging control found");
        } else if (Prefs.enabled(this)) {
            statusText.setText(protection
                    ? "Protection ON · charge limit active"
                    : "Protection OFF · charging allowed to 100%");
        } else {
            statusText.setText("Schedule off · Battery Protection " + (protection ? "ON" : "OFF"));
        }

        versionText.setText("Version: " + BuildConfig.VERSION_NAME);
        deviceText.setText("Device: " + ChargingBackends.deviceSummary());
        backendText.setText("Charging control: " + backend.getName()
                + (backend.isExperimental() ? " (experimental)" : ""));
        controlText.setText("Control method: " + backend.getControlDescription());
    }

    private String format(int hour, int minute) {
        return LocalTime.of(hour, minute).format(TIME_FORMAT);
    }
}
