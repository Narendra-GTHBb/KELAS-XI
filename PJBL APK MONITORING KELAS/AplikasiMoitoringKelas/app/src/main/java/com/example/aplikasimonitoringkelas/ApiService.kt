package com.example.aplikasimonitoringkelas

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // Auth endpoints
    @POST("login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    
    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): LogoutResponse
    
    @GET("me")
    suspend fun getMe(@Header("Authorization") token: String): MeResponse
    
    // GET - Ambil semua data (Suspend functions untuk Coroutines)
    @GET("gurus")
    suspend fun getGurus(@Header("Authorization") token: String): List<Guru>
    
    @GET("kelas")
    suspend fun getKelas(@Header("Authorization") token: String): List<KelasResponse>
    
    @GET("mapels")
    suspend fun getMapels(@Header("Authorization") token: String): List<MapelResponse>
    
    @GET("tahun-ajarans")
    suspend fun getTahunAjarans(@Header("Authorization") token: String): List<TahunAjaranResponse>
    
    @GET("jadwals")
    suspend fun getJadwals(@Header("Authorization") token: String): List<JadwalResponse>
    
    @GET("guru-mengajars")
    suspend fun getGuruMengajars(@Header("Authorization") token: String): List<GuruMengajarResponse>

    // GET guru_mengajar dengan filter hari dan kelas
    @GET("guru-mengajar")
    suspend fun getGuruMengajar(
        @Header("Authorization") token: String,
        @Query("hari") hari: String? = null,
        @Query("kelas_id") kelasId: Int? = null
    ): ApiListResponse<GuruMengajarResponse>

    // GET guru_mengajar by jadwal_id - untuk cek data yang sudah ada
    @GET("guru-mengajar/by-jadwal/{jadwal_id}")
    suspend fun getGuruMengajarByJadwalId(
        @Header("Authorization") token: String,
        @Path("jadwal_id") jadwalId: Int
    ): GuruMengajarByJadwalResponse

    // POST endpoint to fetch guru mengajar by hari + kelas
    @POST("guru-mengajar/by-hari-kelas")
    suspend fun getGuruMengajarByHariKelas(
        @Header("Authorization") token: String,
        @Body request: ByHariKelasRequest
    ): okhttp3.ResponseBody
    
    // GET dengan parameter
    @GET("jadwal/kelas/{kelas_id}/{hari}")
    suspend fun getJadwalKelas(
        @Header("Authorization") token: String,
        @Path("kelas_id") kelasId: Int,
        @Path("hari") hari: String
    ): List<JadwalKepalaSekolah>
    
    @GET("jadwal/schedule/{kelas_id}/{hari}")
    suspend fun getJadwalByKelasHari(
        @Header("Authorization") token: String,
        @Path("kelas_id") kelasId: Int,
        @Path("hari") hari: String
    ): List<JadwalResponse>
    
    // POST - Tambah data
    @POST("jadwals")
    suspend fun createJadwal(
        @Header("Authorization") token: String,
        @Body request: JadwalRequest
    ): JadwalResponse
    
    @POST("guru-mengajars")
    suspend fun createGuruMengajar(
        @Header("Authorization") token: String,
        @Body request: GuruMengajarRequest
    ): GuruMengajarResponse
    
    // PUT - Update data
    @PUT("jadwals/{id}")
    suspend fun updateJadwal(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: JadwalRequest
    ): JadwalResponse
    
    @PUT("guru-mengajars/{id}")
    suspend fun updateGuruMengajar(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: GuruMengajarRequest
    ): GuruMengajarResponse

    @PATCH("guru-mengajars/{id}/status")
    suspend fun updateGuruMengajarStatus(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: UpdateStatusRequest
    ): UpdateStatusResponse
    
    // DELETE - Hapus data
    @DELETE("jadwals/{id}")
    suspend fun deleteJadwal(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    )
    
    @DELETE("guru-mengajars/{id}")
    suspend fun deleteGuruMengajar(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    )

    // ====== ENDPOINT BARU: SISWA ======
    @GET("siswa/jadwal")
    suspend fun getSiswaJadwal(
        @Header("Authorization") token: String,
        @Query("hari") hari: String? = null
    ): ApiListResponse<GuruMengajarResponse>

    @GET("siswa/kehadiran-guru")
    suspend fun getSiswaKehadiranGuru(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null
    ): ApiListResponse<GuruMengajarResponse>

    @POST("siswa/kehadiran-guru")
    suspend fun reportKehadiranGuru(
        @Header("Authorization") token: String,
        @Body request: UpdateStatusRequest
    ): ApiSingleResponse<GuruMengajarResponse>

    @GET("siswa/guru-pengganti")
    suspend fun getSiswaGuruPengganti(
        @Header("Authorization") token: String,
        @Query("hari") hari: String? = null
    ): ApiListResponse<GuruPenggantiResponse>

    @GET("siswa/kehadiran-siswa")
    suspend fun getSiswaKehadiranSiswa(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null
    ): ApiListResponse<KehadiranSiswaResponse>

    @POST("siswa/kehadiran-siswa")
    suspend fun reportKehadiranSiswa(
        @Header("Authorization") token: String,
        @Body request: KehadiranSiswaRequest
    ): ApiSingleResponse<KehadiranSiswaResponse>

    // ====== ENDPOINT BARU: KURIKULUM ======
    @GET("kurikulum/guru-izin")
    suspend fun getKurikulumGuruIzin(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null
    ): ApiListResponse<GuruIzinResponse>

    @PATCH("kurikulum/guru-izin/{id}/approve")
    suspend fun approveGuruIzin(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): ApiSingleResponse<GuruIzinResponse>

    @PATCH("kurikulum/guru-izin/{id}/reject")
    suspend fun rejectGuruIzin(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): ApiSingleResponse<GuruIzinResponse>

    @GET("kurikulum/guru-pengganti")
    suspend fun getKurikulumGuruPengganti(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null
    ): ApiListResponse<GuruPenggantiResponse>

    @POST("kurikulum/guru-pengganti")
    suspend fun createGuruPengganti(
        @Header("Authorization") token: String,
        @Body request: GuruPenggantiRequest
    ): ApiSingleResponse<GuruPenggantiResponse>

    @GET("kurikulum/kehadiran-guru")
    suspend fun getKurikulumKehadiranGuru(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null,
        @Query("hari") hari: String? = null
    ): ApiListResponse<GuruMengajarResponse>

    @GET("kurikulum/jadwal")
    suspend fun getKurikulumJadwal(
        @Header("Authorization") token: String,
        @Query("hari") hari: String? = null,
        @Query("kelas_id") kelasId: Int? = null
    ): ApiListResponse<JadwalResponse>

    // ====== ENDPOINT BARU: KEPSEK ======
    @GET("kepsek/dashboard")
    suspend fun getKepsekDashboard(
        @Header("Authorization") token: String
    ): ApiSingleResponse<DashboardKepsekResponse>

    @GET("kepsek/jadwal")
    suspend fun getKepsekJadwal(
        @Header("Authorization") token: String,
        @Query("hari") hari: String? = null,
        @Query("kelas_id") kelasId: Int? = null
    ): ApiListResponse<JadwalResponse>

    @GET("kepsek/kehadiran-guru")
    suspend fun getKepsekKehadiranGuru(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null,
        @Query("hari") hari: String? = null
    ): ApiListResponse<GuruMengajarResponse>

    @GET("kepsek/guru-pengganti")
    suspend fun getKepsekGuruPengganti(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null
    ): ApiListResponse<GuruPenggantiResponse>

    @GET("kepsek/kehadiran-siswa")
    suspend fun getKepsekKehadiranSiswa(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null,
        @Query("kelas_id") kelasId: Int? = null
    ): ApiListResponse<KehadiranSiswaResponse>

    @GET("kepsek/guru-izin")
    suspend fun getKepsekGuruIzin(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null
    ): ApiListResponse<GuruIzinResponse>

    // ====== ENDPOINT BARU: GURU ======
    @GET("guru/profile")
    suspend fun getGuruProfile(
        @Header("Authorization") token: String
    ): ApiSingleResponse<GuruProfileResponse>

    @GET("guru/jadwal")
    suspend fun getGuruJadwal(
        @Header("Authorization") token: String,
        @Query("hari") hari: String? = null,
        @Query("kelas_id") kelasId: Int? = null
    ): ApiListResponse<JadwalGuruResponse>

    @GET("guru/izin")
    suspend fun getGuruIzinList(
        @Header("Authorization") token: String
    ): ApiListResponse<GuruIzinResponse>

    @POST("guru/izin")
    suspend fun createGuruIzin(
        @Header("Authorization") token: String,
        @Body request: CreateGuruIzinRequest
    ): ApiSingleResponse<GuruIzinResponse>

    @GET("guru/pengganti")
    suspend fun getGuruPenggantiForGuru(
        @Header("Authorization") token: String,
        @Query("tanggal") tanggal: String? = null
    ): ApiListResponse<GuruPenggantiResponse>
}

// Request data classes
data class JadwalRequest(
    val guru_id: Int,
    val mapel_id: Int,
    val tahun_ajaran_id: Int,
    val kelas_id: Int,
    val jam_ke: String,
    val hari: String
)

data class GuruMengajarRequest(
    val hari: String,
    val kelas_id: Int,
    val guru_id: Int,
    val mapel_id: Int,
    val jam_ke: String,
    val status: String,
    val keterangan: String? = null
)

data class ByHariKelasRequest(
    val hari: String,
    val kelas_id: Int
)

// Auth data classes
data class LoginRequest(
    val username: String,
    val password: String,
    val role: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: AuthData?
)

data class AuthData(
    val user: UserInfo,
    val token: String
)

data class UserInfo(
    val id: Int,
    val name: String,
    val username: String,
    val role: String,
    @SerializedName("kelas_id")
    val kelasId: Int? = null,
    val kelas: String? = null
)

data class LogoutResponse(
    val success: Boolean,
    val message: String
)

data class MeResponse(
    val success: Boolean,
    val data: UserInfo?
)

data class GuruMengajarByHariKelasResponse(
    val value: List<GuruMengajarResponse> = emptyList(),
    val Count: Int = 0
)

// Update Status Request/Response
data class UpdateStatusRequest(
    val status: String,
    val keterangan: String? = null
)

data class UpdateStatusResponse(
    val success: Boolean,
    val message: String,
    val data: GuruMengajarResponse?
)

// Guru Profile Response
data class GuruProfileResponse(
    val user: UserInfo,
    val guru: Guru?
)

// Jadwal Guru Response
data class JadwalGuruResponse(
    val id: Int = 0,
    val guru_mengajar_id: Int = 0,
    val hari: String = "",
    val jam_mulai: String = "",
    val jam_selesai: String = "",
    val guru_mengajar: GuruMengajarDetail? = null
)

data class GuruMengajarDetail(
    val id: Int = 0,
    val guru_id: Int = 0,
    val mapel_id: Int = 0,
    val kelas_id: Int = 0,
    val guru: Guru? = null,
    val mapel: MapelResponse? = null,
    val kelas: KelasResponse? = null
)

// Create Guru Izin Request
data class CreateGuruIzinRequest(
    val tanggal: String,
    val jenis_izin: String,
    val keterangan: String? = null
)