package com.example.nasapictureoftheday

import com.example.nasapictureoftheday.data.models.NasaDailyImageAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val api: NasaDailyImageAPI by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.nasa.gov")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NasaDailyImageAPI::class.java)
    }

}