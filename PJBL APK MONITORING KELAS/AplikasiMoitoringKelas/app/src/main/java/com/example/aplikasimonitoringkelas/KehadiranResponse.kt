package com.example.aplikasimonitoringkelas

import com.google.gson.annotations.SerializedName

data class GuruMengajarResponse(
    val id: Int = 0,

    @SerializedName("jadwal_id")
    val jadwalId: Int = 0,

    // Field flat dari response API
    @SerializedName("kode_guru")
    val kodeGuru: String = "",

    @SerializedName("nama_guru")
    val namaGuru: String = "",

    val mapel: String = "",

    @SerializedName("jam_ke")
    val jamKe: String = "",

    val hari: String = "",

    @SerializedName("kelas_id")
    val kelasId: Int = 0,

    @SerializedName("guru_id")
    val guruId: Int = 0,

    @SerializedName("mapel_id")
    val mapelId: Int = 0,

    val keterangan: String = "",
    val status: String = "",

    // Untuk backward compatibility - bisa null jika API tidak mengirim
    val jadwal: JadwalDetail? = null,
    
    // Guru pengganti
    @SerializedName("guru_pengganti_id")
    val guruPenggantiId: Int? = null,
    
    @SerializedName("guru_pengganti")
    val guruPengganti: GuruInfo? = null,
    
    // Durasi izin
    @SerializedName("tanggal_mulai_izin")
    val tanggalMulaiIzin: String? = null,
    
    @SerializedName("tanggal_selesai_izin")
    val tanggalSelesaiIzin: String? = null,
    
    @SerializedName("durasi_izin")
    val durasiIzin: Int? = null
)

data class JadwalDetail(
    val id: Int = 0,

    @SerializedName("guru_id")
    val guruId: Int = 0,

    @SerializedName("mapel_id")
    val mapelId: Int = 0,

    @SerializedName("tahun_ajaran_id")
    val tahunAjaranId: Int = 0,

    @SerializedName("kelas_id")
    val kelasId: Int = 0,

    @SerializedName("jam_ke")
    val jamKe: String = "",

    val hari: String = "",

    @SerializedName("created_at")
    val createdAt: String = "",

    @SerializedName("updated_at")
    val updatedAt: String = "",

    val guru: GuruInfo? = null,
    val mapel: MapelInfo? = null,
    val kelas: JadwalKelasInfo? = null
)

data class JadwalKelasInfo(
    val id: Int = 0,
    val kelas: String = ""
)

data class GuruInfo(
    val id: Int = 0,
    @SerializedName("kode_guru")
    val kodeGuru: String = "",
    val guru: String = "",
    val telepon: String = ""
)

data class MapelInfo(
    val id: Int = 0,
    val mapel: String = ""
)

// Response untuk get by jadwal_id
data class GuruMengajarByJadwalResponse(
    val found: Boolean = false,
    val data: GuruMengajarResponse? = null
)