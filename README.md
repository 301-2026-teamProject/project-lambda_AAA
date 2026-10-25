# Campsite Reservation

Android baseline for the CMPUT 301 Part 3 prototype.

## Requirements

- Android Studio with JDK 17
- Android SDK 37

## Run

1. In the shared Firebase project's **Project settings → General → Your apps**, select the
   Android app with package name `com.example.lambda_aaa` and download `google-services.json`.
2. Place it at `app/google-services.json`. This local configuration is required to build and is
   ignored by Git; do not commit it or paste its API key into tracked files.
3. Open this directory in Android Studio and allow Gradle to sync.
4. Select the `app` configuration and an API 24+ emulator or device, then run the app.

When the team replaces a Firebase API key, download the updated configuration before building.
Ignoring this file does not remove keys from earlier commits or revoke them; exposed keys must
also be replaced and revoked in Google Cloud by a project administrator.

Command-line verification:

```bash
./gradlew testDebugUnitTest assembleDebug assembleDebugAndroidTest
```

## Current scope

- Kotlin and Jetpack Compose application shell
- Material 3 theme with light and dark palettes
- Launch-screen instrumented test

Firebase is connected to the shared `campsitereservation-6b24c` project. The Android app uses the
Google Services Gradle plugin and the Firebase BoM-managed Cloud Firestore SDK.
The Standard-edition Firestore database is hosted in Toronto (`northamerica-northeast2`). Initial
prototype security rules deny every read and write until story-specific schemas and access rules are
reviewed and implemented.
