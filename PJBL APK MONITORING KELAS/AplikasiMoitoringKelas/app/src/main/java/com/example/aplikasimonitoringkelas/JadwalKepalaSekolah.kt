package com.example.aplikasimonitoringkelas

import com.google.gson.annotations.SerializedName

data class JadwalKepalaSekolah(
    @SerializedName("jadwal_id")
    val jadwalId: Int = 0,
    
    @SerializedName("jam_ke")
    val jamKe: String = "",
    
    @SerializedName("mata_pelajaran")
    val mataPelajaran: String = "",
    
    @SerializedName("kode_guru")
    val kodeGuru: String = "",
    
    @SerializedName("nama_guru")
    val namaGuru: String = "",
    
    @SerializedName("guru_id")
    val guruId: Int = 0,
    
    @SerializedName("mapel_id")
    val mapelId: Int = 0
)
