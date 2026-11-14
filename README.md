KIMTECH Mobile Defender V3.0

Android Device Security Management

KIMTECH Mobile Defender is an Android companion application for KIMTECH Cyber Defense, designed to connect Android devices to a centralized security management platform.

Project Information

Item| Details
Product| KIMTECH Mobile Defender
Version| 3.0.0
Platform| Android
Package| "ke.co.kimtech.mobiledefender"
Minimum Android| Android 8.0 (API 26)
Target SDK| 35
Language| Kotlin
Backend| KIMTECH Cyber Defense
Website| https://security.kimtech.co.ke

Features

- Secure account sign-in.
- Android device enrollment through the mobile API.
- Persistent device identification.
- Secure authentication token storage using Android Keystore.
- Device security information checks.
- Background monitoring using Android WorkManager.
- Server session verification.
- HTTPS communication with the KIMTECH backend.

Project Structure

KIMTECH-Mobile-Defender/
│
├── README.md
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
│
├── .github/
│   └── workflows/
│       └── android.yml
│
└── app/
    ├── build.gradle.kts
    │
    └── src/
        └── main/
            ├── AndroidManifest.xml
            │
            ├── java/
            │   └── ke/
            │       └── co/
            │           └── kimtech/
            │               └── mobiledefender/
            │                   ├── MainActivity.kt
            │                   ├── ApiClient.kt
            │                   ├── SecureTokenStore.kt
            │                   ├── AppRiskScanner.kt
            │                   └── HealthWorker.kt
            │
            └── res/
                └── values/
                    ├── strings.xml
                    ├── colors.xml
                    └── themes.xml

Backend API

The application communicates with the KIMTECH Cyber Defense mobile API.

API base URL:

https://security.kimtech.co.ke/api/mobile/

Authentication

POST /api/mobile/login.php

Used for account authentication and device enrollment.

Session Verification

GET /api/mobile/me.php

Used to verify the authenticated device session.

Authentication uses a Bearer token.

Technology Stack

- Kotlin
- Android SDK
- AndroidX Core
- AndroidX Activity
- Android WorkManager
- Android Keystore
- HTTPS
- JSON API communication
- GitHub Actions

Building the APK

This repository includes a GitHub Actions workflow:

.github/workflows/android.yml

The workflow is configured to:

1. Check out the repository.
2. Install Java 17.
3. Configure Gradle 8.7.
4. Build the debug APK.
5. Upload the APK as a GitHub Actions artifact.

Downloading the APK

After a successful workflow:

1. Open the repository on GitHub.
2. Select Actions.
3. Open the successful Android build.
4. Scroll to Artifacts.
5. Download "KIMTECH-Mobile-Defender-V3.0-APK".

Security Notes

- API communication uses HTTPS.
- Authentication tokens are stored using Android Keystore-backed encryption.
- Passwords must not be stored in application source code.
- Authentication tokens must not be committed to GitHub.
- Device monitoring is subject to Android operating-system restrictions.
- The application does not claim to provide complete antivirus or malware-removal protection.

Important Limitations

Background monitoring is scheduled through WorkManager and is subject to Android battery, network, and scheduling restrictions.

The application currently provides account authentication, device enrollment, session verification, and preliminary device security checks. Additional protection features require further implementation and backend integration.

License

Copyright © 2026 KIMTECH Cyber Defense.

All rights reserved.

KIMTECH Cyber Defense

https://security.kimtech.co.ke
