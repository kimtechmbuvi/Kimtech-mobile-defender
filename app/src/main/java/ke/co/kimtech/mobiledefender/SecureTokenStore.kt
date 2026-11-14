
package ke.co.kimtech.mobiledefender

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object SecureTokenStore {

    private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val KEY_ALIAS = "KIMTECH_MOBILE_TOKEN_KEY"
    private const val PREFS_NAME = "kimtech_secure_storage"
    private const val TOKEN_KEY = "encrypted_access_token"
    private const val IV_KEY = "token_iv"

    private const val TRANSFORMATION =
        "AES/GCM/NoPadding"

    private fun getKey(): SecretKey {
        val keyStore = KeyStore.getInstance(
            KEYSTORE_PROVIDER
        )

        keyStore.load(null)

        val existingKey = keyStore.getKey(
            KEY_ALIAS,
            null
        )

        if (existingKey is SecretKey) {
            return existingKey
        }

        val generator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER
        )

        val specification = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or
                KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(
                KeyProperties.BLOCK_MODE_GCM
            )
            .setEncryptionPaddings(
                KeyProperties.ENCRYPTION_PADDING_NONE
            )
            .setRandomizedEncryptionRequired(true)
            .build()

        generator.init(specification)

        return generator.generateKey()
    }

    fun save(
        context: Context,
        token: String
    ) {
        require(token.isNotBlank()) {
            "Authentication token cannot be empty."
        }

        val cipher = Cipher.getInstance(
            TRANSFORMATION
        )

        cipher.init(
            Cipher.ENCRYPT_MODE,
            getKey()
        )

        val encrypted = cipher.doFinal(
            token.toByteArray(Charsets.UTF_8)
        )

        val preferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        preferences.edit()
            .putString(
                TOKEN_KEY,
                Base64.encodeToString(
                    encrypted,
                    Base64.NO_WRAP
                )
            )
            .putString(
                IV_KEY,
                Base64.encodeToString(
                    cipher.iv,
                    Base64.NO_WRAP
                )
            )
            .commit()
    }

    fun read(
        context: Context
    ): String? {

        val preferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val encryptedText = preferences.getString(
            TOKEN_KEY,
            null
        ) ?: return null

        val ivText = preferences.getString(
            IV_KEY,
            null
        ) ?: return null

        return try {
            val encrypted = Base64.decode(
                encryptedText,
                Base64.NO_WRAP
            )

            val iv = Base64.decode(
                ivText,
                Base64.NO_WRAP
            )

            val cipher = Cipher.getInstance(
                TRANSFORMATION
            )

            cipher.init(
                Cipher.DECRYPT_MODE,
                getKey(),
                GCMParameterSpec(128, iv)
            )

            String(
                cipher.doFinal(encrypted),
                Charsets.UTF_8
            )

        } catch (_: Exception) {
            clear(context)
            null
        }
    }

    fun clear(
        context: Context
    ) {
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        ).edit()
            .remove(TOKEN_KEY)
            .remove(IV_KEY)
            .commit()
    }
}
