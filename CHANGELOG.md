# Changelog

All notable changes to Battery Protect are documented here.

## [1.1.3] - 2026-08-19

### Added
- App version number to the in-app **Details** section.
- GitHub-ready README documentation covering setup, compatibility, scheduling behaviour, privacy, building and signing.
- `CHANGELOG.md`.
- GNU GPL v3 licence.
- Expanded `.gitignore` rules for build output, APK/AAB files and signing material.
- Project attribution: **Built with the assistance of ChatGPT.**

### Changed
- Documentation now identifies Galaxy S10 / Android 12 / One UI 4 as the confirmed configuration and describes other device support as best effort.

### Scheduling
- No scheduling behaviour changed from v1.1.2. The tested `AlarmManager.setAlarmClock()` implementation is retained.

## [1.1.2] - 2026-08-18

### Changed
- Replaced `setExactAndAllowWhileIdle()` schedule triggers with `AlarmManager.setAlarmClock()` after overnight testing showed long-idle delivery was unreliable on the test Galaxy S10.
- Added a show-alarm intent back to Battery Protect as required by the alarm-clock API.

### Notes
- The app itself does not create an audible alarm, vibration or notification, although Android/OEM system UI may expose a next-alarm indicator.

## [1.1.1] - 2026-08-17

### Added
- Conservative best-effort detection of existing OEM-style boolean battery-protection settings.
- Experimental backend labelling in **Details**.

### Safety
- Unknown controls are not created or written blindly.
- Kernel/sysfs charging controls are not touched.

## [1.1.0] - 2026-08-17

### Changed
- Refactored charging control into a generic backend architecture.
- Samsung `protect_battery` support moved behind a charging-control interface.

### Added
- Device/backend/control diagnostics in an expandable **Details** section.
- Safe unsupported-device state.

## [1.0.2] - 2026-08-17

### Changed
- Switched schedule transitions to exact idle-capable alarms.
- Added `SCHEDULE_EXACT_ALARM` handling.

### Added
- Minimalist battery/moon launcher icon.

## [1.0.1] - 2026-08-17

### Added
- Manual Battery Protection toggle that controls the same system setting as Samsung's own toggle.
- 24-hour time display.

### Changed
- Renamed the end-time control to **Resume full charging** and clarified that this is when charging from the protected limit toward 100% begins.

## [1.0.0] - 2026-08-17

### Added
- Initial Android 12 implementation.
- Configurable overnight protection start and full-charge resume times.
- Samsung `Settings.Global.protect_battery` control.
- One-time `WRITE_SECURE_SETTINGS` ADB setup.
- Reboot, time-change and time-zone rescheduling.
- Minimal background operation with no foreground service, analytics, ads or network access.
