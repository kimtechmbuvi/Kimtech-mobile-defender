
package ke.co.kimtech.mobiledefender

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.app.KeyguardManager
import org.json.JSONArray
import org.json.JSONObject

object AppRiskScanner {

    fun scan(context: Context): JSONObject {

        val findings = JSONArray()
        var riskScore = 0

        // Android version assessment
        val sdkVersion = Build.VERSION.SDK_INT

        if (sdkVersion < 29) {
            riskScore += 30

            findings.put(
                finding(
                    "OLD_ANDROID_VERSION",
                    "High",
                    "Android version may be outdated.",
                    "Check for an available Android system update."
                )
            )
        } else if (sdkVersion < 31) {
            riskScore += 10

            findings.put(
                finding(
                    "ANDROID_UPDATE_ADVISORY",
                    "Low",
                    "Consider checking for newer Android updates.",
                    "Install security updates when available."
                )
            )
        }

        // Screen-lock assessment
        val keyguardManager =
            context.getSystemService(
                Context.KEYGUARD_SERVICE
            ) as KeyguardManager

        if (!keyguardManager.isDeviceSecure) {
            riskScore += 25

            findings.put(
                finding(
                    "NO_SECURE_SCREEN_LOCK",
                    "High",
                    "No secure screen lock is configured.",
                    "Set a PIN, password, or supported biometric lock."
                )
            )
        }

        // Basic installed-app inventory
        val packageManager = context.packageManager

        val installedApps = try {
            if (Build.VERSION.SDK_INT >= 33) {
                packageManager.getInstalledApplications(
                    PackageManager.ApplicationInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getInstalledApplications(0)
            }
        } catch (_: Exception) {
            emptyList()
        }

        val appInventory = JSONArray()

        for (app in installedApps) {
            val appName = try {
                packageManager.getApplicationLabel(app).toString()
            } catch (_: Exception) {
                app.packageName
            }

            appInventory.put(
                JSONObject().apply {
                    put("name", appName)
                    put("package", app.packageName)
                }
            )
        }

        val riskLevel = when {
            riskScore >= 40 -> "HIGH"
            riskScore >= 20 -> "MEDIUM"
            else -> "LOW"
        }

        return JSONObject().apply {
            put("scanner", "KIMTECH Mobile Defender")
            put("version", "3.0.0")
            put("android_version", Build.VERSION.RELEASE ?: "Unknown")
            put("sdk_version", sdkVersion)
            put("manufacturer", Build.MANUFACTURER ?: "Unknown")
            put("model", Build.MODEL ?: "Unknown")
            put("risk_score", riskScore.coerceAtMost(100))
            put("risk_level", riskLevel)
            put("finding_count", findings.length())
            put("findings", findings)
            put("visible_app_inventory", appInventory)
            put("scan_timestamp", System.currentTimeMillis())
            put(
                "note",
                "Heuristic security assessment. Not a full malware scan."
            )
        }
    }

    private fun finding(
        code: String,
        severity: String,
        title: String,
        recommendation: String
    ): JSONObject {
        return JSONObject().apply {
            put("code", code)
            put("severity", severity)
            put("title", title)
            put("recommendation", recommendation)
        }
    }
}
