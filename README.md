
# KIMTECH Mobile Defender V3.0

**Android Device Security Management**

KIMTECH Mobile Defender is an Android companion application for KIMTECH Cyber Defense.

It connects Android devices to the existing KIMTECH Cyber Defense platform using authenticated HTTPS API requests.

## Project Information

- Product: KIMTECH Mobile Defender
- Version: 3.0.0
- Platform: Android
- Language: Kotlin
- Minimum Android version: Android 8.0 (API 26)
- Target SDK: 35
- Package: ke.co.kimtech.mobiledefender
- Backend: https://security.kimtech.co.ke

## Features

### Account Authentication
- Existing KIMTECH Cyber Defense account login.
- Device UUID generation.
- Device enrollment through the mobile API.
- Bearer token authentication.

### Secure Token Storage
- Android Keystore integration.
- AES-GCM token encryption.
- No password storage in the application.

### Local Security Assessment
- Android version advisory.
- Secure screen-lock assessment.
- Visible installed application inventory.
- Basic heuristic risk scoring.

### Background Monitoring
- Android WorkManager integration.
- Periodic authentication verification.
- Periodic local security assessments.
- Network connectivity constraints.

## Backend API

The application uses these existing endpoints:

POST /api/mobile/login.php

GET /api/mobile/me.php

Base URL:

https://security.kimtech.co.ke

Authentication uses HTTPS bearer tokens.

## Project Structure

```text
KIMTECH-Mobile-Defender/
├── README.md
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── .github/
│   └── workflows/
│       └── android.yml
└── app/
    ├── build.gradle.kts
    └── src/
        └── main/
            ├── AndroidManifest.xml
            ├── java/
            │   └── ke/co/kimtech/mobiledefender/
            │       ├── MainActivity.kt
            │       ├── ApiClient.kt
            │       ├── SecureTokenStore.kt
            │       ├── AppRiskScanner.kt
            │       └── HealthWorker.kt
            └── res/
                └── values/
                    ├── strings.xml
                    ├── colors.xml
                    └── themes.xml
```

## Build Requirements

- JDK 17
- Gradle 8.7
- Android Gradle Plugin 8.6.1
- Kotlin Android plugin 2.0.21
- Android SDK 35

## Build APK

GitHub Actions can build the debug APK.

1. Open the repository's Actions tab.
2. Select KIMTECH Mobile Defender Android Build.
3. Run or open a workflow execution.
4. Download the APK from Artifacts after a successful build.

## Security Limitations

- The local scanner is heuristic-based, not a full antivirus engine.
- Android restricts installed application visibility.
- Background execution is controlled by Android and is not guaranteed at exact intervals.
- Security findings are currently stored locally and are not uploaded to the backend.
- MFA login support must be implemented separately if enabled on the account.
- This project is not yet a signed production release.

## Development Status

Version 3.0 source project.

Compilation, device testing, API integration testing, and production signing must be completed before production deployment.

## Copyright

Copyright (c) 2026 KIMTECH.

All rights reserved.

KIMTECH Cyber Defense
https://security.kimtech.co.ke

Build verification: KIMTECH Mobile Defender V3.0
