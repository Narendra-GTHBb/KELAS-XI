package com.example.realtimeweather.api

import com.google.gson.annotations.SerializedName

data class WeatherModel(
    @SerializedName("location")
    val location: Location,
    @SerializedName("current")
    val current: Current
)

data class Location(
    @SerializedName("name")
    val name: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("localtime")
    val localtime: String
)

data class Current(
    @SerializedName("temp_c")
    val tempC: Double,
    @SerializedName("condition")
    val condition: Condition,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("wind_kph")
    val windKph: Double,
    @SerializedName("precip_mm")
    val precipMm: Double,
    @SerializedName("uv")
    val uv: Double
)

data class Condition(
    @SerializedName("text")
    val text: String,
    @SerializedName("icon")
    val icon: String
)
