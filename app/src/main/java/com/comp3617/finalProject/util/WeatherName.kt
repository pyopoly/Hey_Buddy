package com.comp3617.finalProject.util

import com.comp3617.finalProject.R

/**
 * Different Weathers to be shown in MDate
 */
enum class WeatherName(val drawableId : Int) {
    Clear(R.drawable.clear),
    ClearNight(R.drawable.clear_night),
    Clouds(R.drawable.clouds),
    PartiallyCloudy(R.drawable.partially_cloudy),
    Drizzle(R.drawable.drizzle),
    Rain(R.drawable.rain),
    Snow(R.drawable.snow),
    Thunderstorm(R.drawable.thunderstorm),
    Fog(R.drawable.fog),
    Mist(R.drawable.fog)
}