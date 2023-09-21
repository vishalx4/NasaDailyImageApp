package com.example.nasapictureoftheday.data.models

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


// https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY&start_date=2017-07-08&end_date=2017-07-8

interface NasaDailyImageAPI {

    @GET("/planetary/apod")
    suspend fun getNasaDailyImageData(
        @Query("api_key") key: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Response<List<NasaModel>>

}