package com.example.weatherapp

import android.content.Context
import android.content.SharedPreferences

object PreferencesHelper {
    private const val PREFS_NAME = "weather_prefs"
    private const val KEY_WEATHER_DATA = "weather_data"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveWeatherData(context: Context, data: String) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putString(KEY_WEATHER_DATA, data)
        editor.apply()
    }

    fun getWeatherData(context: Context): String? {
        return getPreferences(context).getString(KEY_WEATHER_DATA, null)
    }
}
