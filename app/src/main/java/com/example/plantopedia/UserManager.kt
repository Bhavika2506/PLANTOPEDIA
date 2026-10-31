package com.example.plantopedia

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest

object UserManager {

    private const val PREFS_NAME = "agromedic_user_prefs"

    private const val KEY_REGISTERED = "is_registered"
    private const val KEY_USERNAME = "username"
    private const val KEY_MOBILE = "mobile_number"
    private const val KEY_PASSWORD_HASH = "password_hash"
    private const val KEY_PREFERRED_LANG = "preferred_language"

    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_IS_GUEST = "is_guest"
    private const val KEY_CURRENT_LANG = "current_language"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun isValidMobile(mobile: String): Boolean {
        // Validates standard 10-digit Indian mobile number
        return mobile.trim().matches(Regex("^[6-9]\\d{9}$"))
    }

    fun register(
        context: Context,
        username: String,
        mobile: String,
        password: String,
        languageCode: String
    ): Boolean {
        if (username.isBlank() || !isValidMobile(mobile) || password.isBlank()) {
            return false
        }

        val prefs = getPrefs(context)
        val cleanMobile = mobile.trim()
        val pwdHash = hashPassword(password)

        prefs.edit()
            .putBoolean(KEY_REGISTERED, true)
            .putString(KEY_USERNAME, username.trim())
            .putString(KEY_MOBILE, cleanMobile)
            .putString(KEY_PASSWORD_HASH, pwdHash)
            .putString(KEY_PREFERRED_LANG, languageCode)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putBoolean(KEY_IS_GUEST, false)
            .putString(KEY_CURRENT_LANG, languageCode)
            .apply()

        return true
    }

    fun login(
        context: Context,
        mobileOrUsername: String,
        password: String
    ): Boolean {
        val prefs = getPrefs(context)
        val isRegistered = prefs.getBoolean(KEY_REGISTERED, false)
        if (!isRegistered) return false

        val savedMobile = prefs.getString(KEY_MOBILE, "") ?: ""
        val savedUsername = prefs.getString(KEY_USERNAME, "") ?: ""
        val savedHash = prefs.getString(KEY_PASSWORD_HASH, "") ?: ""
        val savedLang = prefs.getString(KEY_PREFERRED_LANG, "en") ?: "en"

        val cleanInput = mobileOrUsername.trim()
        val inputHash = hashPassword(password)

        if ((cleanInput == savedMobile || cleanInput.equals(savedUsername, ignoreCase = true)) &&
            inputHash == savedHash
        ) {
            prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putBoolean(KEY_IS_GUEST, false)
                .putString(KEY_CURRENT_LANG, savedLang)
                .apply()
            return true
        }

        return false
    }

    fun setGuestMode(context: Context, active: Boolean = true) {
        val prefs = getPrefs(context)
        // If guest mode is turned on, set is_guest=true and is_logged_in=false.
        // DO NOT clear or overwrite registered user data or registered language preference.
        if (active) {
            val currentLang = prefs.getString(KEY_CURRENT_LANG, "en") ?: "en"
            prefs.edit()
                .putBoolean(KEY_IS_GUEST, true)
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .putString(KEY_CURRENT_LANG, currentLang)
                .apply()
        } else {
            prefs.edit().putBoolean(KEY_IS_GUEST, false).apply()
        }
    }

    fun logout(context: Context) {
        val prefs = getPrefs(context)
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .putBoolean(KEY_IS_GUEST, false)
            .apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun isGuest(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_GUEST, false)
    }

    fun getUsername(context: Context): String {
        val prefs = getPrefs(context)
        return if (prefs.getBoolean(KEY_IS_GUEST, false)) {
            "Guest"
        } else {
            prefs.getString(KEY_USERNAME, "Farmer") ?: "Farmer"
        }
    }

    fun getLanguage(context: Context): String {
        val prefs = getPrefs(context)
        return prefs.getString(KEY_CURRENT_LANG, null)
            ?: prefs.getString(KEY_PREFERRED_LANG, "en")
            ?: "en"
    }

    fun setLanguage(context: Context, langCode: String) {
        val prefs = getPrefs(context)
        val editor = prefs.edit().putString(KEY_CURRENT_LANG, langCode)
        if (prefs.getBoolean(KEY_REGISTERED, false) && !prefs.getBoolean(KEY_IS_GUEST, false)) {
            editor.putString(KEY_PREFERRED_LANG, langCode)
        }
        editor.apply()
    }

    fun isRegistered(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_REGISTERED, false)
    }

    private const val KEY_SELECTED_CROPS = "selected_crops"

    fun getSelectedCrops(context: Context): Set<String> {
        val prefs = getPrefs(context)
        val key = if (isGuest(context)) "guest_$KEY_SELECTED_CROPS" else "user_${prefs.getString(KEY_MOBILE, "default")}_$KEY_SELECTED_CROPS"
        return prefs.getStringSet(key, emptySet()) ?: emptySet()
    }

    fun saveSelectedCrops(context: Context, crops: Set<String>) {
        val prefs = getPrefs(context)
        val key = if (isGuest(context)) "guest_$KEY_SELECTED_CROPS" else "user_${prefs.getString(KEY_MOBILE, "default")}_$KEY_SELECTED_CROPS"
        prefs.edit().putStringSet(key, crops).apply()
    }
}