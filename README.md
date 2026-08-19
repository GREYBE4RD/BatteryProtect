# Battery Protect

Battery Protect is a small Android utility that schedules a phone's existing battery-protection feature overnight, then releases the charge limit before you need the phone.

For example:

- **21:00** — enable battery protection and hold at the device's protected charge level.
- **06:00** — disable battery protection and allow charging to continue toward 100%.

The app is intentionally minimal. It has no ads, analytics, network access, foreground service, or continuous polling.

## Current release

**v1.1.3**

## Quick compatibility check

Before installing, you can check whether your phone exposes the Samsung-style battery-protection control currently confirmed to work with Battery Protect.

With ADB connected, run:

```text
adb shell settings get global protect_battery
```

If the result is:

```text
0
```

or:

```text
1
```

the phone exposes the same `Settings.Global.protect_battery` control used by the confirmed Samsung backend, so it is likely compatible.

If the result is:

```text
null
```

that specific control is not exposed on the device. Battery Protect may report the phone as unsupported unless another compatible OEM control is detected.

This check only confirms that the setting exists. For a stronger test, manually toggle the phone's own battery-protection feature and confirm the value changes between `0` and `1`.

## Confirmed compatibility

Confirmed working on:

- Samsung Galaxy S10
- Android 12 / One UI 4
- Samsung `Settings.Global.protect_battery` charge protection

Other Samsung devices using the same control may work unchanged.

Battery Protect also includes conservative best-effort detection for a small number of OEM-style boolean battery-protection settings. Experimental methods are only accepted when the setting already exists on the phone and has a simple `0`/`1` value. Unknown devices remain unsupported rather than receiving blind low-level writes.

The app does **not** write kernel/sysfs charging controls and does not require root.

## One-time setup

Battery Protect needs permission to change protected Android settings. Install the app, connect the phone with ADB once, and run:

```text
adb shell pm grant com.batteryprotect.scheduler android.permission.WRITE_SECURE_SETTINGS
```

The ADB connection is not needed afterwards. The grant normally remains in place while updating the app with the same package name, but must be granted again after uninstalling/reinstalling it.

Battery Protect also declares `SCHEDULE_EXACT_ALARM`. Android may require **Alarms & reminders** special access depending on the Android version and device policy.

## Schedule behaviour

- **Protection starts** — enables the detected battery-protection mode at the selected time.
- **Resume full charging** — disables protection at the selected time so charging can continue toward 100%.
- The manual **Battery Protection** switch changes the same underlying system setting used by the phone's own battery-protection control.
- If the schedule is enabled, the next scheduled transition takes control again.
- Schedule times use 24-hour format.

v1.1.3 retains the `AlarmManager.setAlarmClock()` scheduling used in v1.1.2. This was selected after overnight testing on the Galaxy S10 showed that ordinary exact idle-capable alarms could remain registered yet fail to wake reliably after extended idle periods. `setAlarmClock()` gives the scheduled transitions stronger wall-clock delivery semantics without a continuously running service.

The app itself does not play an alarm, vibrate, or post an upcoming-alarm notification. Android or an OEM system UI may expose the next scheduled alarm-clock event because `setAlarmClock()` is intentionally visible to the operating system.

## Device diagnostics

Open **Details** in the app to see:

- app version;
- device/model and Android version;
- detected charging backend;
- control method;
- whether the backend is experimental.

## Building

Open the project in Android Studio with Android SDK 36 available and build normally.

For a public APK, use **Build → Generate Signed App Bundle or APK → APK** and keep the signing keystore outside the repository. Future releases must use the same signing key if users are to install them as updates.

## Privacy

Battery Protect requests no network permission and contains no ads, analytics, telemetry, or online account functionality.

## Licence

Battery Protect is licensed under the GNU General Public License v3.0. See [`LICENSE`](LICENSE).

## Development

Built with the assistance of ChatGPT.
