package com.example.carinspection.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class DriverInfoManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("driver_info", Context.MODE_PRIVATE)

    fun isDriverInfoPresent(): Boolean {
        return prefs.contains(KEY_DRIVER_NAME) && prefs.contains(KEY_DRIVER_SURNAME)
    }

    fun getDriverName(): String = prefs.getString(KEY_DRIVER_NAME, "") ?: ""
    fun getDriverSurname(): String = prefs.getString(KEY_DRIVER_SURNAME, "") ?: ""

    fun saveDriverInfo(name: String, surname: String) {
        prefs.edit {
            putString(KEY_DRIVER_NAME, name)
            putString(KEY_DRIVER_SURNAME, surname)
        }
    }

    companion object {
        private const val KEY_DRIVER_NAME = "driver_name"
        private const val KEY_DRIVER_SURNAME = "driver_surname"
    }
}
