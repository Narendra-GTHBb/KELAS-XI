package com.example.aplikasimonitoringkelas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SiswaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val sessionManager = SessionManager(this)
        val token = sessionManager.getAuthToken() ?: ""
        val kelasId = sessionManager.getKelasId()
        val kelasName = sessionManager.getKelasName() ?: ""
        val userName = sessionManager.getUserName() ?: "Siswa"
        
        setContent {
            AplikasiMonitoringKelasTheme {
                var selectedItem by remember { mutableStateOf(0) }
                var editData by remember { mutableStateOf<GuruMengajarResponse?>(null) }
                SiswaScreen(
                    selectedItem = selectedItem,
                    onTabChange = { selectedItem = it },
                    editData = editData,
                    onEditDataChange = { editData = it },
                    token = token,
                    kelasId = kelasId,
                    kelasName = kelasName,
                    userName = userName,
                    onLogout = {
                        sessionManager.clearSession()
                        finish()
                        startActivity(Intent(this@SiswaActivity, MainActivity::class.java))
                    }
                )
            }
        }
    }
}

sealed class SiswaNavigationItem(val route: String, val title: String, val icon: ImageVector) {
    object Jadwal : SiswaNavigationItem("jadwal", "Jadwal", Icons.Default.Home)
    object KehadiranGuru : SiswaNavigationItem("kehadiran", "Entri", Icons.Default.Edit)
    object GuruPengganti : SiswaNavigationItem("pengganti", "Status", Icons.Default.List)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSpinner(
    selectedValue: String,
    label: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SiswaScreen(
    selectedItem: Int,
    onTabChange: (Int) -> Unit,
    editData: GuruMengajarResponse?,
    onEditDataChange: (GuruMengajarResponse?) -> Unit,
    token: String,
    kelasId: Int?,
    kelasName: String,
    userName: String,
    onLogout: () -> Unit = {}
) {
    val items = listOf(
        SiswaNavigationItem.Jadwal,
        SiswaNavigationItem.KehadiranGuru,
        SiswaNavigationItem.GuruPengganti
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedItem == index,
                        onClick = { onTabChange(index) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedItem) {
                0 -> SiswaJadwalPage(
                    token = token,
                    kelasId = kelasId,
                    kelasName = kelasName,
                    userName = userName,
                    onEditClick = { data ->
                        onEditDataChange(data)
                        onTabChange(1) // Pindah ke tab Kehadiran Guru
                    },
                    onLogout = onLogout
                )
                1 -> SiswaEntriPage(
                    token = token,
                    kelasId = kelasId,
                    kelasName = kelasName,
                    initialData = editData,
                    onDataSaved = { onEditDataChange(null) }
                )
                2 -> SiswaGuruPenggantiPage(token = token)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiswaJadwalPage(
    token: String,
    kelasId: Int?,
    kelasName: String,
    userName: String,
    onEditClick: (GuruMengajarResponse) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var selectedHari by remember { mutableStateOf("Senin") }
    
    var jadwalList by remember { mutableStateOf<List<JadwalKepalaSekolah>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val scope = rememberCoroutineScope()

    val daftarHari = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")
    
    // Load jadwal ketika hari berubah (kelas sudah fix dari session)
    LaunchedEffect(selectedHari, kelasId) {
        if (kelasId != null) {
            scope.launch {
                isLoading = true
                errorMessage = ""
                try {
                    Log.d("SiswaJadwal", "Loading jadwal for kelas=$kelasId, hari=$selectedHari")
                    jadwalList = ApiClient.api.getJadwalKelas("Bearer $token", kelasId, selectedHari)
                    Log.d("SiswaJadwal", "Jadwal loaded: ${jadwalList.size} items")
                } catch (e: Exception) {
                    Log.e("SiswaJadwal", "Error loading jadwal", e)
                    errorMessage = "Error jadwal: ${e.javaClass.simpleName} - ${e.message}"
                    jadwalList = emptyList()
                } finally {
                    isLoading = false
                }
            }
        } else {
            errorMessage = "Kelas belum diatur. Hubungi admin untuk mengatur kelas Anda."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Halo, $userName",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Kelas: $kelasName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Logout")
            }
        }

        // Spinner Hari
        DropdownSpinner(
            selectedValue = selectedHari,
            label = "Pilih Hari",
            options = daftarHari,
            onValueChange = { selectedHari = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        
        // Error message
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Scrollable Cards with real data
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (jadwalList.isEmpty() && errorMessage.isEmpty()) {
                    Text(
                        text = "Tidak ada jadwal untuk hari ini",
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    jadwalList.forEach { jadwal ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    // Cek apakah sudah ada data guru_mengajar untuk jadwal ini
                                    scope.launch {
                                        try {
                                            if (jadwal.jadwalId > 0) {
                                                val response = ApiClient.api.getGuruMengajarByJadwalId(
                                                    "Bearer $token",
                                                    jadwal.jadwalId
                                                )
                                                if (response.found && response.data != null) {
                                                    // Data sudah ada, gunakan data yang sudah tersimpan
                                                    onEditClick(response.data)
                                                } else {
                                                    // Data belum ada, buat payload baru
                                                    val payload = GuruMengajarResponse(
                                                        id = 0,
                                                        jadwalId = jadwal.jadwalId,
                                                        kodeGuru = jadwal.kodeGuru,
                                                        namaGuru = jadwal.namaGuru,
                                                        mapel = jadwal.mataPelajaran,
                                                        jamKe = jadwal.jamKe,
                                                        hari = selectedHari,
                                                        kelasId = kelasId ?: 0,
                                                        guruId = jadwal.guruId,
                                                        mapelId = jadwal.mapelId,
                                                        keterangan = "",
                                                        status = "Masuk",
                                                        jadwal = null
                                                    )
                                                    onEditClick(payload)
                                                }
                                            } else {
                                                // Tidak ada jadwal_id, buat payload baru
                                                val payload = GuruMengajarResponse(
                                                    id = 0,
                                                    jadwalId = 0,
                                                    kodeGuru = jadwal.kodeGuru,
                                                    namaGuru = jadwal.namaGuru,
                                                    mapel = jadwal.mataPelajaran,
                                                    jamKe = jadwal.jamKe,
                                                    hari = selectedHari,
                                                    kelasId = kelasId ?: 0,
                                                    guruId = jadwal.guruId,
                                                    mapelId = jadwal.mapelId,
                                                    keterangan = "",
                                                    status = "Masuk",
                                                    jadwal = null
                                                )
                                                onEditClick(payload)
                                            }
                                        } catch (e: Exception) {
                                            // Jika gagal, tetap buat payload baru
                                            val payload = GuruMengajarResponse(
                                                id = 0,
                                                jadwalId = jadwal.jadwalId,
                                                kodeGuru = jadwal.kodeGuru,
                                                namaGuru = jadwal.namaGuru,
                                                mapel = jadwal.mataPelajaran,
                                                jamKe = jadwal.jamKe,
                                                hari = selectedHari,
                                                kelasId = kelasId ?: 0,
                                                guruId = jadwal.guruId,
                                                mapelId = jadwal.mapelId,
                                                keterangan = "",
                                                status = "Masuk",
                                                jadwal = null
                                            )
                                            onEditClick(payload)
                                        }
                                    }
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Jam Ke ${jadwal.jamKe}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Mata Pelajaran: ${jadwal.mataPelajaran}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Kode Guru: ${jadwal.kodeGuru} | Nama Guru: ${jadwal.namaGuru}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiswaEntriPage(
    token: String,
    kelasId: Int?,
    kelasName: String,
    initialData: GuruMengajarResponse? = null,
    onDataSaved: () -> Unit = {}
) {
    var editingId by remember { mutableStateOf<Int?>(null) }
    var selectedHari by remember { mutableStateOf("Senin") }
    
    // Jadwal-based selection
    var jadwalList by remember { mutableStateOf<List<JadwalKepalaSekolah>>(emptyList()) }
    var selectedJadwal by remember { mutableStateOf<JadwalKepalaSekolah?>(null) }
    var selectedJadwalLabel by remember { mutableStateOf("Pilih Jadwal") }
    
    var selectedStatus by remember { mutableStateOf("Masuk") }
    var keterangan by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    
    val scope = rememberCoroutineScope()

    val daftarHari = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")
    val daftarStatus = listOf("Masuk", "Tidak Masuk", "Izin")
    
    // Auto-fill form ketika initialData berubah
    LaunchedEffect(initialData?.id, initialData?.hari, initialData?.jamKe, initialData?.namaGuru) {
        initialData?.let { data ->
            editingId = if (data.id > 0) data.id else null
            selectedHari = data.hari.ifEmpty { "Senin" }
            selectedStatus = data.status.ifEmpty { "Masuk" }
            keterangan = data.keterangan
            
            // Set jadwal label from initialData
            if (data.namaGuru.isNotEmpty() && data.mapel.isNotEmpty()) {
                selectedJadwalLabel = "Jam ${data.jamKe} - ${data.mapel} (${data.namaGuru})"
            }
        }
    }
    
    // Load jadwal ketika hari berubah (kelas sudah fix dari session)
    LaunchedEffect(selectedHari, kelasId) {
        if (kelasId != null) {
            scope.launch {
                isLoading = true
                try {
                    jadwalList = ApiClient.api.getJadwalKelas("Bearer $token", kelasId, selectedHari)
                    
                    // Auto-select jadwal if initialData matches
                    initialData?.let { data ->
                        if (data.guruId > 0 && data.mapelId > 0) {
                            val matchingJadwal = jadwalList.find { 
                                it.guruId == data.guruId && it.mapelId == data.mapelId 
                            }
                            if (matchingJadwal != null) {
                                selectedJadwal = matchingJadwal
                                selectedJadwalLabel = "Jam ${matchingJadwal.jamKe} - ${matchingJadwal.mataPelajaran} (${matchingJadwal.namaGuru})"
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SiswaEntri", "Error loading jadwal", e)
                    jadwalList = emptyList()
                } finally {
                    isLoading = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Entri Kehadiran Guru",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Show current class
        Text(
            text = "Kelas: $kelasName",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Success/Error Messages
        if (successMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    text = successMessage,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        
        if (errorMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Spinner Hari
        DropdownSpinner(
            selectedValue = selectedHari,
            label = "Pilih Hari",
            options = daftarHari,
            onValueChange = { 
                selectedHari = it
                // Reset jadwal selection when hari changes
                selectedJadwal = null
                selectedJadwalLabel = "Pilih Jadwal"
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        // Loading indicator for jadwal
        if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))
        }

        // Spinner Jadwal (Guru + Mapel dari jadwal)
        DropdownSpinner(
            selectedValue = selectedJadwalLabel,
            label = "Pilih Jadwal (Guru & Mapel)",
            options = if (jadwalList.isEmpty()) {
                listOf("Tidak ada jadwal")
            } else {
                jadwalList.map { "Jam ${it.jamKe} - ${it.mataPelajaran} (${it.namaGuru})" }
            },
            onValueChange = { label ->
                if (label != "Tidak ada jadwal") {
                    selectedJadwalLabel = label
                    val index = jadwalList.indexOfFirst { 
                        "Jam ${it.jamKe} - ${it.mataPelajaran} (${it.namaGuru})" == label 
                    }
                    if (index >= 0) {
                        selectedJadwal = jadwalList[index]
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        
        // Show selected jadwal info
        selectedJadwal?.let { jadwal ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Jadwal Terpilih:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Jam: ${jadwal.jamKe}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Guru: ${jadwal.namaGuru} (${jadwal.kodeGuru})",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Mapel: ${jadwal.mataPelajaran}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Spinner Status
        DropdownSpinner(
            selectedValue = selectedStatus,
            label = "Status Kehadiran",
            options = daftarStatus,
            onValueChange = { selectedStatus = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        // Text Field Keterangan
        OutlinedTextField(
            value = keterangan,
            onValueChange = { keterangan = it },
            label = { Text("Keterangan") },
            placeholder = { Text("Masukkan keterangan (opsional)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            minLines = 3,
            maxLines = 5
        )

        // Tombol Tambah/Update
        Button(
            onClick = {
                scope.launch {
                    isSubmitting = true
                    successMessage = ""
                    errorMessage = ""
                    
                    try {
                        val jadwal = selectedJadwal
                        if (jadwal == null) {
                            errorMessage = "Pilih jadwal terlebih dahulu"
                            isSubmitting = false
                            return@launch
                        }
                        
                        if (kelasId == null) {
                            errorMessage = "Kelas belum diatur. Hubungi admin."
                            isSubmitting = false
                            return@launch
                        }
                        
                        // Buat request dengan data dari jadwal
                        val request = GuruMengajarRequest(
                            hari = selectedHari,
                            kelas_id = kelasId,
                            guru_id = jadwal.guruId,
                            mapel_id = jadwal.mapelId,
                            jam_ke = jadwal.jamKe,
                            status = selectedStatus,
                            keterangan = keterangan.ifEmpty { null }
                        )
                        
                        // Kirim ke API (CREATE atau UPDATE)
                        if (editingId != null) {
                            ApiClient.api.updateGuruMengajar("Bearer $token", editingId!!, request)
                            successMessage = "Data berhasil diupdate!"
                        } else {
                            ApiClient.api.createGuruMengajar("Bearer $token", request)
                            successMessage = "Data berhasil disimpan!"
                        }
                        
                        // Reset form
                        editingId = null
                        selectedJadwal = null
                        selectedJadwalLabel = "Pilih Jadwal"
                        keterangan = ""
                        selectedStatus = "Masuk"
                        
                        onDataSaved()
                        
                    } catch (e: retrofit2.HttpException) {
                        errorMessage = "Gagal menyimpan: HTTP ${e.code()} - ${e.message()}"
                    } catch (e: Exception) {
                        errorMessage = "Gagal menyimpan: ${e.message}"
                    } finally {
                        isSubmitting = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isSubmitting && selectedJadwal != null && kelasId != null
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(if (editingId != null) "Update" else "Simpan Kehadiran")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiswaListPage(
    token: String,
    onEditClick: (GuruMengajarResponse) -> Unit = {}
) {
    var selectedHari by remember { mutableStateOf("Senin") }
    var selectedKelasId by remember { mutableStateOf<Int?>(null) }
    var selectedKelasName by remember { mutableStateOf("") }

    var kelasList by remember { mutableStateOf<List<KelasResponse>>(emptyList()) }
    var guruList by remember { mutableStateOf<List<Guru>>(emptyList()) }
    var mapelList by remember { mutableStateOf<List<MapelResponse>>(emptyList()) }
    var guruMengajarList by remember { mutableStateOf<List<GuruMengajarResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    var showEditDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<GuruMengajarResponse?>(null) }
    var editStatus by remember { mutableStateOf("Masuk") }
    var editKeterangan by remember { mutableStateOf("") }
        var showDeleteDialog by remember { mutableStateOf(false) }
        var deletingItem by remember { mutableStateOf<GuruMengajarResponse?>(null) }

    val scope = rememberCoroutineScope()

    val daftarHari = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")

    // Load kelas, guru, mapel lists
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                kelasList = ApiClient.api.getKelas("Bearer $token")
                guruList = ApiClient.api.getGurus("Bearer $token")
                mapelList = ApiClient.api.getMapels("Bearer $token")
                if (kelasList.isNotEmpty()) {
                    selectedKelasId = kelasList[0].id
                    selectedKelasName = kelasList[0].kelas
                }
            } catch (e: Exception) {
                errorMessage = "Error loading lists: ${e.message}"
            }
        }
    }

    // Fetch guru mengajar via POST endpoint when hari or kelas change
    LaunchedEffect(selectedHari, selectedKelasId) {
        if (selectedKelasId != null) {
            scope.launch {
                isLoading = true
                errorMessage = ""
                try {
                    val req = ByHariKelasRequest(hari = selectedHari, kelas_id = selectedKelasId!!)
                    val body = ApiClient.api.getGuruMengajarByHariKelas("Bearer $token", req)
                    val bodyStr = body.string()
                    val gson = com.google.gson.Gson()
                    try {
                        // Try wrapper { value: [...] }
                        val wrapper = gson.fromJson(bodyStr, GuruMengajarByHariKelasResponse::class.java)
                        guruMengajarList = wrapper?.value ?: emptyList()
                    } catch (e: com.google.gson.JsonSyntaxException) {
                        // Fallback: plain array
                        val listType = object : com.google.gson.reflect.TypeToken<List<GuruMengajarResponse>>() {}.type
                        guruMengajarList = gson.fromJson(bodyStr, listType) as List<GuruMengajarResponse>
                    }
                    body.close()
                } catch (e: Exception) {
                    errorMessage = "Error loading data: ${e.message}"
                    guruMengajarList = emptyList()
                } finally {
                    isLoading = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Daftar Kehadiran Guru",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Spinner Hari
        DropdownSpinner(
            selectedValue = selectedHari,
            label = "Filter Hari",
            options = daftarHari,
            onValueChange = { selectedHari = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        // Spinner Kelas
        DropdownSpinner(
            selectedValue = selectedKelasName,
            label = "Filter Kelas",
            options = kelasList.map { it.kelas },
            onValueChange = { selectedName ->
                val kelas = kelasList.find { it.kelas == selectedName }
                if (kelas != null) {
                    selectedKelasId = kelas.id
                    selectedKelasName = kelas.kelas
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        
        // Error message
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // List Cards - Tap untuk edit
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (guruMengajarList.isEmpty()) {
                    Text(
                        text = "Tidak ada data kehadiran",
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    guruMengajarList.forEach { guruMengajar ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable { onEditClick(guruMengajar) },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Jam Ke ${guruMengajar.jamKe ?: guruMengajar.jadwal?.jamKe ?: "-"}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Guru: ${guruMengajar.namaGuru ?: guruMengajar.jadwal?.guru?.guru ?: "-"}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Mapel: ${guruMengajar.mapel ?: guruMengajar.jadwal?.mapel?.mapel ?: "-"}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Status: ${guruMengajar.status}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (guruMengajar.status == "Masuk") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                    )
                                    if (!guruMengajar.keterangan.isNullOrEmpty()) {
                                        Text(
                                            text = "Ket: ${guruMengajar.keterangan}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }

                                // Actions on the right
                                Column(horizontalAlignment = Alignment.End) {
                                    Row {
                                        IconButton(onClick = {
                                            editingItem = guruMengajar
                                            editStatus = guruMengajar.status ?: "Masuk"
                                            editKeterangan = guruMengajar.keterangan ?: ""
                                            showEditDialog = true
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                                        }

                                        IconButton(onClick = {
                                            deletingItem = guruMengajar
                                            showDeleteDialog = true
                                        }) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit dialog for updating status/keterangan
    if (showEditDialog && editingItem != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Kehadiran") },
            text = {
                Column {
                    DropdownSpinner(
                        selectedValue = editStatus,
                        label = "Status",
                        options = listOf("Masuk", "Tidak Masuk", "Izin"),
                        onValueChange = { editStatus = it }
                    )
                    OutlinedTextField(
                        value = editKeterangan,
                        onValueChange = { editKeterangan = it },
                        label = { Text("Keterangan") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // Perform update
                    val item = editingItem!!
                    scope.launch {
                        try {
                            // resolve guru_id and mapel_id
                            val guruId = item.jadwal?.guruId ?: guruList.find { it.guru == item.namaGuru }?.id
                            val mapelId = item.jadwal?.mapelId ?: mapelList.find { it.mapel == item.mapel }?.id
                            val kelasId = item.jadwal?.kelasId ?: selectedKelasId
                            if (item.id == null || guruId == null || mapelId == null || kelasId == null) {
                                errorMessage = "Data tidak lengkap untuk update"
                                return@launch
                            }

                            val request = GuruMengajarRequest(
                                hari = item.jadwal?.hari ?: selectedHari,
                                kelas_id = kelasId,
                                guru_id = guruId,
                                mapel_id = mapelId,
                                jam_ke = item.jamKe ?: item.jadwal?.jamKe ?: "",
                                status = editStatus,
                                keterangan = editKeterangan.ifEmpty { null }
                            )

                            ApiClient.api.updateGuruMengajar("Bearer $token", item.id!!, request)
                            // refresh list
                            val req = ByHariKelasRequest(hari = selectedHari, kelas_id = selectedKelasId!!)
                            val body = ApiClient.api.getGuruMengajarByHariKelas("Bearer $token", req)
                            val bodyStr = body.string()
                            val gson = com.google.gson.Gson()
                            try {
                                val wrapper = gson.fromJson(bodyStr, GuruMengajarByHariKelasResponse::class.java)
                                guruMengajarList = wrapper?.value ?: emptyList()
                            } catch (e: com.google.gson.JsonSyntaxException) {
                                val listType = object : com.google.gson.reflect.TypeToken<List<GuruMengajarResponse>>() {}.type
                                guruMengajarList = gson.fromJson(bodyStr, listType) as List<GuruMengajarResponse>
                            }
                            body.close()
                            showEditDialog = false
                        } catch (e: Exception) {
                            errorMessage = "Gagal update: ${e.message}"
                        }
                    }
                }) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Batal") }
            }
        )
    }

    // Delete confirmation dialog
    if (showDeleteDialog && deletingItem != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Kehadiran") },
            text = { Text("Anda yakin ingin menghapus data kehadiran ini?") },
            confirmButton = {
                TextButton(onClick = {
                    val item = deletingItem!!
                    scope.launch {
                        try {
                            if (item.id != null) {
                                ApiClient.api.deleteGuruMengajar("Bearer $token", item.id)
                            }

                            // refresh list
                            if (selectedKelasId != null) {
                                val req = ByHariKelasRequest(hari = selectedHari, kelas_id = selectedKelasId!!)
                                val body = ApiClient.api.getGuruMengajarByHariKelas("Bearer $token", req)
                                val bodyStr = body.string()
                                val gson = com.google.gson.Gson()
                                try {
                                    val wrapper = gson.fromJson(bodyStr, GuruMengajarByHariKelasResponse::class.java)
                                    guruMengajarList = wrapper?.value ?: emptyList()
                                } catch (e: com.google.gson.JsonSyntaxException) {
                                    val listType = object : com.google.gson.reflect.TypeToken<List<GuruMengajarResponse>>() {}.type
                                    guruMengajarList = gson.fromJson(bodyStr, listType) as List<GuruMengajarResponse>
                                }
                                body.close()
                            }
                        } catch (e: Exception) {
                            errorMessage = "Gagal menghapus: ${e.message}"
                        } finally {
                            showDeleteDialog = false
                            deletingItem = null
                        }
                    }
                }) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }
}

// === TAB 3: KEHADIRAN SISWA ===
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiswaKehadiranSiswaPage(token: String) {
    var selectedKelasId by remember { mutableStateOf<Int?>(null) }
    var selectedKelasName by remember { mutableStateOf("X RPL") }
    var kelasList by remember { mutableStateOf<List<KelasResponse>>(emptyList()) }
    var jadwalList by remember { mutableStateOf<List<GuruMengajarResponse>>(emptyList()) }
    
    var selectedJadwalId by remember { mutableStateOf<Int?>(null) }
    var selectedJadwalLabel by remember { mutableStateOf("Pilih Jadwal") }
    var jumlahHadir by remember { mutableStateOf("") }
    var jumlahSakit by remember { mutableStateOf("") }
    var jumlahIzin by remember { mutableStateOf("") }
    var jumlahAlpha by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val currentHari = remember {
        val calendar = Calendar.getInstance()
        when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Senin"
            Calendar.TUESDAY -> "Selasa"
            Calendar.WEDNESDAY -> "Rabu"
            Calendar.THURSDAY -> "Kamis"
            Calendar.FRIDAY -> "Jumat"
            Calendar.SATURDAY -> "Sabtu"
            Calendar.SUNDAY -> "Minggu"
            else -> "Senin"
        }
    }
    
    // Load kelas
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                kelasList = ApiClient.api.getKelas("Bearer $token")
                if (kelasList.isNotEmpty()) {
                    selectedKelasId = kelasList[0].id
                    selectedKelasName = kelasList[0].kelas
                }
            } catch (e: Exception) {
                Log.e("KehadiranSiswa", "Error loading kelas", e)
            }
        }
    }
    
    // Load jadwal berdasarkan kelas
    LaunchedEffect(selectedKelasId) {
        if (selectedKelasId != null) {
            scope.launch {
                try {
                    val allData = ApiClient.api.getGuruMengajars("Bearer $token")
                    jadwalList = allData.filter { item ->
                        val hariMatch = (item.hari ?: item.jadwal?.hari) == currentHari
                        val kelasMatch = (item.kelasId ?: item.jadwal?.kelasId) == selectedKelasId
                        hariMatch && kelasMatch
                    }.sortedBy { it.jamKe ?: it.jadwal?.jamKe }
                } catch (e: Exception) {
                    Log.e("KehadiranSiswa", "Error loading jadwal", e)
                }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Lapor Kehadiran Siswa",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Tanggal: $today",
            style = MaterialTheme.typography.bodyMedium,
            color = androidx.compose.ui.graphics.Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        successMessage?.let {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFF4CAF50).copy(alpha = 0.1f))
            ) {
                Text(
                    text = it,
                    color = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        
        errorMessage?.let {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f))
            ) {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        
        // Pilih Kelas
        DropdownSpinner(
            selectedValue = selectedKelasName,
            label = "Pilih Kelas",
            options = kelasList.map { it.kelas },
            onValueChange = { selectedName ->
                val kelas = kelasList.find { it.kelas == selectedName }
                if (kelas != null) {
                    selectedKelasId = kelas.id
                    selectedKelasName = kelas.kelas
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        
        // Pilih Jadwal
        DropdownSpinner(
            selectedValue = selectedJadwalLabel,
            label = "Pilih Jadwal (Jam Ke)",
            options = jadwalList.map { "Jam ${it.jamKe.ifEmpty { it.jadwal?.jamKe ?: "" }} - ${it.mapel.ifEmpty { it.jadwal?.mapel?.mapel ?: "Mapel" }}" },
            onValueChange = { label ->
                selectedJadwalLabel = label
                val index = jadwalList.indexOfFirst { 
                    "Jam ${it.jamKe.ifEmpty { it.jadwal?.jamKe ?: "" }} - ${it.mapel.ifEmpty { it.jadwal?.mapel?.mapel ?: "Mapel" }}" == label 
                }
                if (index >= 0) {
                    selectedJadwalId = jadwalList[index].jadwalId ?: jadwalList[index].jadwal?.id
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        
        // Input Jumlah
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = jumlahHadir,
                onValueChange = { jumlahHadir = it },
                label = { Text("Hadir") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = jumlahSakit,
                onValueChange = { jumlahSakit = it },
                label = { Text("Sakit") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = jumlahIzin,
                onValueChange = { jumlahIzin = it },
                label = { Text("Izin") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = jumlahAlpha,
                onValueChange = { jumlahAlpha = it },
                label = { Text("Alpha") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        
        OutlinedTextField(
            value = keterangan,
            onValueChange = { keterangan = it },
            label = { Text("Keterangan (Opsional)") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            minLines = 2
        )
        
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    errorMessage = null
                    successMessage = null
                    try {
                        val request = KehadiranSiswaRequest(
                            jadwalId = selectedJadwalId ?: 0,
                            kelasId = selectedKelasId ?: 0,
                            tanggal = today,
                            jumlahHadir = jumlahHadir.toIntOrNull() ?: 0,
                            jumlahSakit = jumlahSakit.toIntOrNull() ?: 0,
                            jumlahIzin = jumlahIzin.toIntOrNull() ?: 0,
                            jumlahAlpha = jumlahAlpha.toIntOrNull() ?: 0,
                            keterangan = keterangan.ifEmpty { null }
                        )
                        ApiClient.api.reportKehadiranSiswa("Bearer $token", request)
                        successMessage = "Kehadiran siswa berhasil dilaporkan!"
                        // Reset form
                        jumlahHadir = ""
                        jumlahSakit = ""
                        jumlahIzin = ""
                        jumlahAlpha = ""
                        keterangan = ""
                    } catch (e: Exception) {
                        errorMessage = "Gagal melaporkan kehadiran: ${e.message}"
                        Log.e("KehadiranSiswa", "Error", e)
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading && selectedJadwalId != null && jumlahHadir.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = androidx.compose.ui.graphics.Color.White
                )
            } else {
                Text("Simpan Kehadiran")
            }
        }
    }
}

// === TAB 3: STATUS KEHADIRAN GURU ===
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiswaGuruPenggantiPage(token: String) {
    var guruMengajarList by remember { mutableStateOf<List<GuruMengajarResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
    var selectedHari by remember { mutableStateOf(getCurrentDayName()) }
    var expandedHari by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    
    // Function to load data - menggunakan endpoint siswa/jadwal
    fun loadData() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = ApiClient.api.getSiswaJadwal("Bearer $token", selectedHari)
                guruMengajarList = response.data.sortedBy { it.jamKe.ifEmpty { it.jadwal?.jamKe ?: "0" } }
            } catch (e: Exception) {
                errorMessage = "Gagal memuat data: ${e.message}"
                Log.e("SiswaGuruPengganti", "Error loading", e)
            } finally {
                isLoading = false
            }
        }
    }
    
    // Load data when selectedHari changes
    LaunchedEffect(selectedHari) {
        loadData()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Status Kehadiran Guru",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Menampilkan jadwal guru dan status kehadiran",
            style = MaterialTheme.typography.bodySmall,
            color = androidx.compose.ui.graphics.Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Dropdown filter hari
        ExposedDropdownMenuBox(
            expanded = expandedHari,
            onExpandedChange = { expandedHari = !expandedHari }
        ) {
            OutlinedTextField(
                value = selectedHari,
                onValueChange = {},
                readOnly = true,
                label = { Text("Pilih Hari") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHari) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                    .padding(bottom = 16.dp)
            )
            ExposedDropdownMenu(
                expanded = expandedHari,
                onDismissRequest = { expandedHari = false }
            ) {
                hariList.forEach { hari ->
                    DropdownMenuItem(
                        text = { Text(hari) },
                        onClick = {
                            selectedHari = hari
                            expandedHari = false
                        }
                    )
                }
            }
        }
        
        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { loadData() }) {
                            Text("Coba Lagi")
                        }
                    }
                }
            }
            guruMengajarList.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.List,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = androidx.compose.ui.graphics.Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tidak ada jadwal untuk hari $selectedHari")
                    }
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(guruMengajarList) { data ->
                        SiswaGuruMengajarCard(data)
                    }
                }
            }
        }
    }
}

// Helper function to get current day name
private fun getCurrentDayName(): String {
    val calendar = Calendar.getInstance()
    return when (calendar.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> "Senin"
        Calendar.TUESDAY -> "Selasa"
        Calendar.WEDNESDAY -> "Rabu"
        Calendar.THURSDAY -> "Kamis"
        Calendar.FRIDAY -> "Jumat"
        Calendar.SATURDAY -> "Sabtu"
        Calendar.SUNDAY -> "Minggu"
        else -> "Senin"
    }
}

@Composable
fun SiswaGuruMengajarCard(data: GuruMengajarResponse) {
    val statusColor = when (data.status.lowercase()) {
        "masuk" -> androidx.compose.ui.graphics.Color(0xFF4CAF50) // Green
        "tidak masuk", "tidak_masuk" -> androidx.compose.ui.graphics.Color(0xFFF44336) // Red
        "izin" -> androidx.compose.ui.graphics.Color(0xFFFF9800) // Orange
        else -> androidx.compose.ui.graphics.Color.Gray
    }
    
    val hasGuruPengganti = data.guruPenggantiId != null && data.guruPengganti != null
    val hasDurasiIzin = data.durasiIzin != null && data.durasiIzin > 0
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Jam ke, Mapel, Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Jam ke-${data.jamKe.ifEmpty { data.jadwal?.jamKe ?: "-" }}",
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = data.mapel.ifEmpty { data.jadwal?.mapel?.mapel ?: "-" },
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Badges Column
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Status Badge
                    if (data.status.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = statusColor.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = data.status.replaceFirstChar { it.uppercase() },
                                fontSize = 11.sp,
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    // Guru Pengganti Badge
                    if (hasGuruPengganti) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = androidx.compose.ui.graphics.Color(0xFF2196F3).copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Ada Pengganti",
                                fontSize = 10.sp,
                                color = androidx.compose.ui.graphics.Color(0xFF2196F3),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    
                    // Durasi Izin Badge
                    if (hasDurasiIzin) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = androidx.compose.ui.graphics.Color(0xFF9C27B0).copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Izin ${data.durasiIzin} hari",
                                fontSize = 10.sp,
                                color = androidx.compose.ui.graphics.Color(0xFF9C27B0),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Guru Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Guru",
                        fontSize = 11.sp,
                        color = androidx.compose.ui.graphics.Color.Gray
                    )
                    Text(
                        text = data.namaGuru.ifEmpty { data.jadwal?.guru?.guru ?: "-" },
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                        fontSize = 13.sp
                    )
                    val kodeGuru = data.kodeGuru.ifEmpty { data.jadwal?.guru?.kodeGuru ?: "" }
                    if (kodeGuru.isNotEmpty()) {
                        Text(
                            text = "($kodeGuru)",
                            fontSize = 11.sp,
                            color = androidx.compose.ui.graphics.Color.Gray
                        )
                    }
                }
                
                // Show guru pengganti if exists
                if (hasGuruPengganti) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        tint = androidx.compose.ui.graphics.Color(0xFF2196F3)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Digantikan oleh",
                            fontSize = 11.sp,
                            color = androidx.compose.ui.graphics.Color.Gray
                        )
                        Text(
                            text = data.guruPengganti?.guru ?: "-",
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            fontSize = 13.sp,
                            color = androidx.compose.ui.graphics.Color(0xFF2196F3)
                        )
                        val kodePengganti = data.guruPengganti?.kodeGuru ?: ""
                        if (kodePengganti.isNotEmpty()) {
                            Text(
                                text = "($kodePengganti)",
                                fontSize = 11.sp,
                                color = androidx.compose.ui.graphics.Color.Gray
                            )
                        }
                    }
                }
            }
            
            // Keterangan & Durasi Izin Info
            if (data.keterangan.isNotEmpty() || hasDurasiIzin) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = androidx.compose.ui.graphics.Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(8.dp))
                
                if (hasDurasiIzin && !data.tanggalMulaiIzin.isNullOrEmpty()) {
                    Text(
                        text = "Periode izin: ${data.tanggalMulaiIzin} s/d ${data.tanggalSelesaiIzin ?: "-"}",
                        fontSize = 12.sp,
                        color = androidx.compose.ui.graphics.Color(0xFF9C27B0)
                    )
                }
                
                if (data.keterangan.isNotEmpty()) {
                    Text(
                        text = "Keterangan: ${data.keterangan}",
                        fontSize = 12.sp,
                        color = androidx.compose.ui.graphics.Color.DarkGray
                    )
                }
            }
        }
    }
}

