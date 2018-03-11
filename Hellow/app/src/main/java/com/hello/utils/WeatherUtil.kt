package com.hello.utils

import android.content.Context
import android.graphics.drawable.Drawable
import com.hello.R
import java.util.*

object WeatherUtil {
    private val imgs = listOf(
            R.drawable.img_weather_cloudy, R.drawable.img_weather_cloudy_night,
            R.drawable.img_weather_rain, R.drawable.img_weather_rain_night,
            R.drawable.img_weather_snow, R.drawable.img_weather_snow_night,
            R.drawable.img_weather_sunny, R.drawable.img_weather_sunny_night,
            R.drawable.img_weather_thunderstorms, R.drawable.img_weather_thunderstorms_night)

    @Suppress("DEPRECATION")
    @JvmStatic
    fun getWeatherImg(context: Context, s: String): Drawable? {
        val res = context.resources
        return when {
            s.contains("多云") -> if (isNight()) res.getDrawable(imgs[1])
            else res.getDrawable(imgs[0])
            s.contains("晴") -> if (isNight()) res.getDrawable(imgs[7])
            else res.getDrawable(imgs[6])
            s.contains("雷") -> if (isNight()) res.getDrawable(imgs[9])
            else res.getDrawable(imgs[8])
            s.contains("雪") -> if (isNight()) res.getDrawable(imgs[5])
            else res.getDrawable(imgs[4])
            else -> if (isNight()) res.getDrawable(imgs[3])
            else res.getDrawable(imgs[2])
        }
    }

    private fun isNight(): Boolean {
        val time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        Log.i(time)
        return time < 6 || time > 20
    }
}