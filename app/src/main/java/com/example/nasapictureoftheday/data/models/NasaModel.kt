package com.example.nasapictureoftheday.data.models

data class NasaModel(
    val url: String,
    val title: String,
    val date: String,
    val explanation: String,
    val copyright: String,
    val hdurl: String,
    val media_type: String,
    val service_version: String,
)
