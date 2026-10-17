# Campsite Reservation

Android baseline for the CMPUT 301 Part 3 prototype.

## Requirements

- Android Studio with JDK 17
- Android SDK 37

## Run

1. Open this directory in Android Studio.
2. Allow Gradle to sync.
3. Select the `app` configuration and an API 24+ emulator or device.
4. Run the app.

Command-line verification:

```bash
./gradlew testDebugUnitTest assembleDebug assembleDebugAndroidTest
```

## Current scope

- Kotlin and Jetpack Compose application shell
- Material 3 theme with light and dark palettes
- Launch-screen instrumented test

Firebase is intentionally not configured in this baseline; it is owned by a separate team task.
