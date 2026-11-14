
package ke.co.kimtech.mobiledefender

import android.content.Context
import android.os.Build
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object ApiClient {

    private const val BASE_URL =
        "https://security.kimtech.co.ke/api/mobile/"

    private const val LOGIN_ENDPOINT = "login.php"
    private const val ME_ENDPOINT = "me.php"

    fun login(
        context: Context,
        email: String,
        password: String,
        deviceUuid: String
    ): JSONObject {

        val deviceName = Build.DEVICE ?: "Android Device"
        val manufacturer = Build.MANUFACTURER ?: "Unknown"
        val model = Build.MODEL ?: "Unknown"
        val androidVersion = Build.VERSION.RELEASE ?: "Unknown"
        val appVersion = "3.0.0"

        val payload = JSONObject().apply {
            put("email", email)
            put("password", password)
            put("device_uuid", deviceUuid)
            put("device_name", deviceName)
            put("manufacturer", manufacturer)
            put("model", model)
            put("android_version", androidVersion)
            put("app_version", appVersion)
        }

        return request(
            endpoint = LOGIN_ENDPOINT,
            method = "POST",
            body = payload
        )
    }

    fun verifySession(
        token: String
    ): JSONObject {

        return request(
            endpoint = ME_ENDPOINT,
            method = "GET",
            token = token
        )
    }

    private fun request(
        endpoint: String,
        method: String,
        body: JSONObject? = null,
        token: String? = null
    ): JSONObject {

        val connection = (
            URL(BASE_URL + endpoint).openConnection()
            as HttpURLConnection
        )

        try {
            connection.requestMethod = method
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.useCaches = false

            connection.setRequestProperty(
                "Accept",
                "application/json"
            )

            if (body != null) {
                connection.doOutput = true

                connection.setRequestProperty(
                    "Content-Type",
                    "application/json; charset=UTF-8"
                )

                connection.outputStream.use { output ->
                    output.write(body.toString().toByteArray(Charsets.UTF_8))
                }
            }

            if (!token.isNullOrBlank()) {
                connection.setRequestProperty(
                    "Authorization",
                    "Bearer $token"
                )
            }

            val statusCode = connection.responseCode

            val responseStream = if (statusCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val responseText = responseStream
                ?.bufferedReader(Charsets.UTF_8)
                ?.use { it.readText() }
                .orEmpty()

            val json = try {
                JSONObject(responseText)
            } catch (_: Exception) {
                JSONObject().put(
                    "error",
                    "Server returned an invalid response."
                )
            }

            json.put("_http_status", statusCode)

            return json

        } finally {
            connection.disconnect()
        }
    }
}
