package com.example.weatherapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Weather(
    val date: String,
    val description: String
) : Parcelable
