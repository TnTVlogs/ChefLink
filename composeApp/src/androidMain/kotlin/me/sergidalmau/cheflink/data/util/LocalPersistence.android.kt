package me.sergidalmau.cheflink.data.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

var appContext: Context? = null

fun initLocalPersistence(context: Context) {
    appContext = context.applicationContext
}

actual fun saveLocalData(key: String, value: String) {
    val prefs: SharedPreferences = appContext?.getSharedPreferences("cheflink_prefs", Context.MODE_PRIVATE) ?: return
    prefs.edit { putString(key, value) }
}

actual fun getLocalData(key: String): String? {
    val prefs: SharedPreferences =
        appContext?.getSharedPreferences("cheflink_prefs", Context.MODE_PRIVATE) ?: return null
    return prefs.getString(key, null)
}
