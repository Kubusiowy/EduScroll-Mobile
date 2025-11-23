package com.example.eduscroll.StorageOperations

import android.content.Context

object UserPrefs {
    private const val PREF_NAME = "user_prefs"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_PREFERRED_CATEGORY_ID = "preferred_category_id"
    private const val KEY_IS_GUEST_LOGGED = "is_guest_logged"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun setGuestLogged(context: Context, userId: Int) {
        prefs(context).edit()
            .putBoolean(KEY_IS_GUEST_LOGGED, true)
            .putInt(KEY_USER_ID, userId)
            .apply()
    }

    fun isGuestLogged(context: Context): Boolean =
        prefs(context).getBoolean(KEY_IS_GUEST_LOGGED, false)

    fun getUserId(context: Context): Int? {
        val stored = prefs(context).getInt(KEY_USER_ID, -1)
        return if (stored == -1) null else stored
    }

    fun setPreferredCategory(context: Context, categoryId: Int) {
        prefs(context).edit()
            .putInt(KEY_PREFERRED_CATEGORY_ID, categoryId)
            .apply()
    }

    fun getPreferredCategory(context: Context): Int? {
        val stored = prefs(context).getInt(KEY_PREFERRED_CATEGORY_ID, -1)
        return if (stored == -1) null else stored
    }

    fun clearAll(context: Context) {
        prefs(context).edit().clear().apply()
    }
}