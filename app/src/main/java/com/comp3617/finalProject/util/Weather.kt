package com.comp3617.finalProject.util

import android.app.Activity
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.comp3617.finalProject.models.Block
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * Gets weather info from openWeatherMap. Only the current weather is available, and this weather
 * is dependent on the first Block of that Date. If there is not Block, then there is no Weather
 * info.
 */
class Weather(val activity: Activity, val holder: ImageView, val block: Block) {


    fun getAllWeather() {
        okHTTP(block.lat.toString(), block.lng.toString(), holder)
    }


    private fun okHTTP(lat: String, lng: String, holder: ImageView) {
        val url =
            "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lng&appid=00c38f999aaba79bed1eeb46ecdfbd67"
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: okhttp3.Call, response: Response) {
                    if (response.isSuccessful) {
                        //Handle the response body
                        val answer = JSONObject(response.body?.string()!!)
                        val arr: JSONArray = answer.getJSONArray("weather")
                        try {
                            val jsonObject: JSONObject = arr.getJSONObject(0)
                            // Pulling items from the array
                            val weatherMain = jsonObject.getString("main")
//                            Log.d("test", "Weather name: $weatherMain")
                            val weatherDrawableId = WeatherName.valueOf(weatherMain).drawableId
                            activity.runOnUiThread {
                                holder.setImageDrawable(
                                    ContextCompat.getDrawable(activity, weatherDrawableId)
                                )
                            }
                        } catch (e: JSONException) {
                            // Oops
                            e.printStackTrace()
                        }
                    }
                }
            })
    }
}