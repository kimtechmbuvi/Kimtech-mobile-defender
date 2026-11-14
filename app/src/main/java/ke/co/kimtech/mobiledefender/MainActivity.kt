
package ke.co.kimtech.mobiledefender

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import java.util.UUID
import java.util.concurrent.Executors

class MainActivity : Activity() {

    private val executor = Executors.newSingleThreadExecutor()

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var statusText: TextView
    private lateinit var loginButton: Button

    private val backgroundColor = Color.rgb(13, 22, 40)
    private val accentColor = Color.rgb(25, 197, 232)
    private val textColor = Color.WHITE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showLoginScreen()
    }

    private fun showLoginScreen() {
        val root = createContainer()

        val title = TextView(this).apply {
            text = "KIMTECH"
            textSize = 30f
            setTextColor(accentColor)
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "MOBILE DEFENDER V3.0"
            textSize = 16f
            setTextColor(textColor)
            gravity = Gravity.CENTER
        }

        emailInput = EditText(this).apply {
            hint = "Email address"
            setSingleLine(true)
            setTextColor(textColor)
            setHintTextColor(Color.LTGRAY)
            inputType = 33
        }

        passwordInput = EditText(this).apply {
            hint = "Password"
            setSingleLine(true)
            setTextColor(textColor)
            setHintTextColor(Color.LTGRAY)
            inputType = 129
        }

        loginButton = Button(this).apply {
            text = "SIGN IN AND ENROLL DEVICE"
            setTextColor(Color.BLACK)
            setBackgroundColor(accentColor)
            setOnClickListener {
                performLogin()
            }
        }

        statusText = TextView(this).apply {
            text = "Sign in using your existing KIMTECH Cyber Defense account."
            textSize = 14f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        root.addView(title)
        root.addView(subtitle)
        root.addView(spacer(35))
        root.addView(emailInput)
        root.addView(passwordInput)
        root.addView(spacer(15))
        root.addView(loginButton)
        root.addView(spacer(20))
        root.addView(statusText)

        setContentView(root)
    }

    private fun performLogin() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            statusText.text = "Enter your email address and password."
            return
        }

        loginButton.isEnabled = false
        statusText.text = "Connecting securely to KIMTECH..."

        executor.execute {
            try {
                val response = ApiClient.login(
                    this,
                    email,
                    password,
                    getDeviceUuid()
                )

                if (!response.optBoolean("success", false)) {
                    val error = response.optString(
                        "error",
                        "Login failed."
                    )

                    runOnUiThread {
                        statusText.text = error
                        loginButton.isEnabled = true
                    }
                    return@execute
                }

                val data = response.optJSONObject("data")

                val token = response.optString("token")
                    .ifBlank {
                        response.optString("access_token")
                    }
                    .ifBlank {
                        data?.optString("token").orEmpty()
                    }
                    .ifBlank {
                        data?.optString("access_token").orEmpty()
                    }

                if (token.isBlank()) {
                    runOnUiThread {
                        statusText.text =
                            "Server response did not contain an access token."
                        loginButton.isEnabled = true
                    }
                    return@execute
                }

                SecureTokenStore.save(this, token)

                runOnUiThread {
                    HealthWorker.schedule(this)
                    showDashboard(email)
                }

            } catch (e: Exception) {
                runOnUiThread {
                    statusText.text =
                        "Connection error: ${e.message ?: "Please try again."}"
                    loginButton.isEnabled = true
                }
            }
        }
    }

    private fun showDashboard(email: String) {
        val root = createContainer()

        val title = TextView(this).apply {
            text = "KIMTECH MOBILE DEFENDER"
            textSize = 23f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(accentColor)
            gravity = Gravity.CENTER
        }

        val success = TextView(this).apply {
            text = "DEVICE LOGIN SUCCESSFUL"
            textSize = 17f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.rgb(80, 220, 140))
            gravity = Gravity.CENTER
        }

        val account = TextView(this).apply {
            text = "Account: $email"
            textSize = 14f
            setTextColor(textColor)
            gravity = Gravity.CENTER
        }

        val device = TextView(this).apply {
            text = "Device ID: ${getDeviceUuid()}"
            textSize = 12f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        val info = TextView(this).apply {
            text = """
                Device enrollment submitted.

                Your authenticated session is stored securely.

                Background security checks are scheduled
                through Android WorkManager.

                Monitoring runs when Android permits it.
            """.trimIndent()
            textSize = 15f
            setTextColor(textColor)
            gravity = Gravity.CENTER
        }

        root.addView(title)
        root.addView(spacer(25))
        root.addView(success)
        root.addView(spacer(15))
        root.addView(account)
        root.addView(spacer(10))
        root.addView(device)
        root.addView(spacer(25))
        root.addView(info)

        setContentView(root)
    }

    private fun getDeviceUuid(): String {
        val preferences = getSharedPreferences(
            "kimtech_mobile",
            Context.MODE_PRIVATE
        )

        var uuid = preferences.getString("device_uuid", null)

        if (uuid.isNullOrBlank()) {
            uuid = UUID.randomUUID().toString()

            preferences.edit()
                .putString("device_uuid", uuid)
                .apply()
        }

        return uuid
    }

    private fun createContainer(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 35, 28, 35)
            setBackgroundColor(backgroundColor)
        }
    }

    private fun spacer(height: Int): View {
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(1, height)
        }
    }

    override fun onDestroy() {
        executor.shutdown()
        super.onDestroy()
    }
}
