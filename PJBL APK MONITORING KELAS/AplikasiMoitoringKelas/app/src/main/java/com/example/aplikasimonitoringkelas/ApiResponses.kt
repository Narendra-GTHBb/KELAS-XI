package com.example.aplikasimonitoringkelas

import com.google.gson.annotations.SerializedName

// Response untuk Kelas
data class KelasResponse(
    val id: Int,
    val kelas: String,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

// Response untuk Mapel
data class MapelResponse(
    val id: Int,
    val mapel: String,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

// Response untuk Tahun Ajaran
data class TahunAjaranResponse(
    val id: Int,
    @SerializedName("tahun_ajaran")
    val tahunAjaran: String,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null
)

// Response untuk Jadwal
data class JadwalResponse(
    val id: Int,
    @SerializedName("guru_id")
    val guruId: Int,
    @SerializedName("mapel_id")
    val mapelId: Int,
    @SerializedName("tahun_ajaran_id")
    val tahunAjaranId: Int,
    @SerializedName("kelas_id")
    val kelasId: Int,
    @SerializedName("jam_ke")
    val jamKe: String,
    val hari: String,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    val guru: GuruInfo? = null,
    val mapel: MapelInfo? = null,
    val kelas: KelasInfo? = null,
    @SerializedName("tahun_ajaran")
    val tahunAjaran: TahunAjaranInfo? = null
)

data class KelasInfo(
    val id: Int,
    val kelas: String
)

data class TahunAjaranInfo(
    val id: Int,
    @SerializedName("tahun_ajaran")
    val tahunAjaran: String
)

// === FITUR BARU: Guru Izin ===
data class GuruIzinResponse(
    val id: Int,
    @SerializedName("guru_id")
    val guruId: Int,
    val tanggal: String,
    @SerializedName("jenis_izin")
    val jenisIzin: String,
    val keterangan: String? = null,
    val status: String,
    @SerializedName("approved_by")
    val approvedBy: Int? = null,
    @SerializedName("approved_at")
    val approvedAt: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    val guru: GuruInfo? = null,
    @SerializedName("approved_user")
    val approvedUser: UserInfoSimple? = null
)

data class UserInfoSimple(
    val id: Int,
    val nama: String?
)

data class GuruIzinRequest(
    @SerializedName("guru_id")
    val guruId: Int,
    val tanggal: String,
    @SerializedName("jenis_izin")
    val jenisIzin: String,
    val keterangan: String? = null
)

// === FITUR BARU: Guru Pengganti (dari tabel guru_mengajars) ===
data class GuruPenggantiResponse(
    val id: Int,
    @SerializedName("jadwal_id")
    val jadwalId: Int,
    val hari: String? = null,
    @SerializedName("jam_ke")
    val jamKe: String? = null,
    val kelas: String? = null,
    val mapel: String? = null,
    @SerializedName("guru_asli")
    val guruAsli: GuruInfoSimple? = null,
    @SerializedName("guru_pengganti")
    val guruPengganti: GuruInfoSimple? = null,
    val status: String? = null,
    val keterangan: String? = null
)

data class GuruInfoSimple(
    val id: Int? = null,
    val nama: String? = null,
    val kode: String? = null
)

data class GuruPenggantiRequest(
    @SerializedName("guru_mengajar_id")
    val guruMengajarId: Int,
    @SerializedName("guru_pengganti_id")
    val guruPenggantiId: Int
)

// JadwalInfo untuk KehadiranSiswaResponse
data class JadwalInfo(
    val id: Int,
    val hari: String? = null,
    @SerializedName("jam_ke")
    val jamKe: String? = null
)

// === FITUR BARU: Kehadiran Siswa ===
data class KehadiranSiswaResponse(
    val id: Int,
    @SerializedName("jadwal_id")
    val jadwalId: Int,
    @SerializedName("kelas_id")
    val kelasId: Int,
    val tanggal: String,
    @SerializedName("jumlah_hadir")
    val jumlahHadir: Int,
    @SerializedName("jumlah_sakit")
    val jumlahSakit: Int,
    @SerializedName("jumlah_izin")
    val jumlahIzin: Int,
    @SerializedName("jumlah_alpha")
    val jumlahAlpha: Int,
    val keterangan: String? = null,
    @SerializedName("dilaporkan_oleh")
    val dilaporkanOleh: Int? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    val kelas: KelasInfo? = null,
    val jadwal: JadwalInfo? = null,
    @SerializedName("reported_by")
    val reportedBy: UserInfoSimple? = null
)

data class KehadiranSiswaRequest(
    @SerializedName("jadwal_id")
    val jadwalId: Int,
    @SerializedName("kelas_id")
    val kelasId: Int,
    val tanggal: String,
    @SerializedName("jumlah_hadir")
    val jumlahHadir: Int,
    @SerializedName("jumlah_sakit")
    val jumlahSakit: Int,
    @SerializedName("jumlah_izin")
    val jumlahIzin: Int,
    @SerializedName("jumlah_alpha")
    val jumlahAlpha: Int,
    val keterangan: String? = null
)

// === Wrapper Response ===
data class ApiListResponse<T>(
    val success: Boolean,
    val data: List<T>
)

data class ApiSingleResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

// === Dashboard Kepsek ===
data class DashboardKepsekResponse(
    @SerializedName("total_guru")
    val totalGuru: Int,
    @SerializedName("total_kelas")
    val totalKelas: Int,
    @SerializedName("total_mapel")
    val totalMapel: Int,
    @SerializedName("guru_hadir_hari_ini")
    val guruHadirHariIni: Int,
    @SerializedName("guru_izin_hari_ini")
    val guruIzinHariIni: Int,
    @SerializedName("guru_pengganti_hari_ini")
    val guruPenggantiHariIni: Int,
    @SerializedName("izin_pending")
    val izinPending: Int
)
