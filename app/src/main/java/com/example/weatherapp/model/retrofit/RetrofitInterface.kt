package com.example.weatherapp.model.retrofit

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.model.weather.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface RetrofitInterface {
    @GET("${BuildConfig.BASE_URL}?hourly=temperature_2m,weathercode,windspeed_10m&daily=weathercode,temperature_2m_max,temperature_2m_min&current_weather=true&timezone=auto")
    fun getWeather(@QueryMap fields: Map<String, String>): Call<Weather>
}