package com.example.aplikasimonitoringkelas

import com.google.gson.annotations.SerializedName

data class Guru(
    val id: Int? = 0,
    @SerializedName("kode_guru")
    val kodeGuru: String? = "",
    val guru: String? = "",
    val telepon: String? = ""
)