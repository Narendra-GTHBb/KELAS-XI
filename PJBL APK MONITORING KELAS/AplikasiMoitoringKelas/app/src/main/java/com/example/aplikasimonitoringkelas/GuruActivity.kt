package com.example.aplikasimonitoringkelas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class GuruActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sessionManager = SessionManager(this)
        val token = sessionManager.getAuthToken() ?: ""
        setContent {
            AplikasiMonitoringKelasTheme {
                GuruScreen(
                    token = token,
                    onLogout = {
                        sessionManager.clearSession()
                        finish()
                        startActivity(Intent(this@GuruActivity, MainActivity::class.java))
                    }
                )
            }
        }
    }
}

sealed class GuruNavigationItem(val route: String, val title: String, val icon: ImageVector) {
    object Jadwal : GuruNavigationItem("jadwal", "Jadwal", Icons.Default.DateRange)
    object Izin : GuruNavigationItem("izin", "Izin", Icons.Default.Create)
    object Pengganti : GuruNavigationItem("pengganti", "Pengganti", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruDropdownSpinner(
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
fun GuruScreen(token: String, onLogout: () -> Unit = {}) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf(
        GuruNavigationItem.Jadwal,
        GuruNavigationItem.Izin,
        GuruNavigationItem.Pengganti
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
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
                0 -> GuruJadwalPage(token = token, onLogout = onLogout)
                1 -> GuruIzinPage(token = token)
                2 -> GuruPenggantiPage(token = token)
            }
        }
    }
}

// ========== GURU JADWAL PAGE ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruJadwalPage(token: String, onLogout: () -> Unit = {}) {
    var selectedHari by remember { mutableStateOf("Senin") }
    var selectedKelasId by remember { mutableStateOf<Int?>(null) }
    var selectedKelasName by remember { mutableStateOf("Semua Kelas") }
    
    var jadwalList by remember { mutableStateOf<List<JadwalGuruResponse>>(emptyList()) }
    var kelasList by remember { mutableStateOf<List<KelasResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val daftarHari = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")

    // Load kelas list
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                kelasList = ApiClient.api.getKelas("Bearer $token")
            } catch (e: Exception) {
                Log.e("GuruJadwal", "Error loading kelas", e)
            }
        }
    }

    // Load jadwal berdasarkan guru_id (dari token)
    LaunchedEffect(selectedHari, selectedKelasId) {
        scope.launch {
            isLoading = true
            errorMessage = ""
            try {
                val response = ApiClient.api.getGuruJadwal(
                    token = "Bearer $token",
                    hari = selectedHari,
                    kelasId = selectedKelasId
                )
                jadwalList = response.data
                Log.d("GuruJadwal", "Loaded ${jadwalList.size} jadwal items")
            } catch (e: Exception) {
                Log.e("GuruJadwal", "Error loading jadwal", e)
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with logout
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Jadwal Mengajar",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Logout")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        // Filter Hari
        GuruDropdownSpinner(
            selectedValue = selectedHari,
            label = "Pilih Hari",
            options = daftarHari,
            onValueChange = { selectedHari = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        // Filter Kelas
        GuruDropdownSpinner(
            selectedValue = selectedKelasName,
            label = "Filter Kelas",
            options = listOf("Semua Kelas") + kelasList.map { it.kelas },
            onValueChange = { name ->
                if (name == "Semua Kelas") {
                    selectedKelasId = null
                    selectedKelasName = "Semua Kelas"
                } else {
                    val kelas = kelasList.find { it.kelas == name }
                    selectedKelasId = kelas?.id
                    selectedKelasName = name
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // Error message
        if (errorMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Content
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (jadwalList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Tidak ada jadwal untuk hari $selectedHari",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(jadwalList) { jadwal ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "${jadwal.jam_mulai} - ${jadwal.jam_selesai}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Mata Pelajaran: ${jadwal.guru_mengajar?.mapel?.mapel ?: "-"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Kelas: ${jadwal.guru_mengajar?.kelas?.kelas ?: "-"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Hari: ${jadwal.hari}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// ========== GURU IZIN PAGE ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruIzinPage(token: String) {
    var izinList by remember { mutableStateOf<List<GuruIzinResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    
    // Form state
    var showForm by remember { mutableStateOf(false) }
    var tanggal by remember { mutableStateOf("") }
    var jenisIzin by remember { mutableStateOf("sakit") }
    var keterangan by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val jenisIzinOptions = listOf("sakit", "izin", "cuti", "dinas_luar", "lainnya")

    // Load izin list
    fun loadIzinList() {
        scope.launch {
            isLoading = true
            errorMessage = ""
            try {
                val response = ApiClient.api.getGuruIzinList("Bearer $token")
                izinList = response.data
            } catch (e: Exception) {
                Log.e("GuruIzin", "Error loading izin", e)
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadIzinList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pengajuan Izin",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Button(onClick = { showForm = !showForm }) {
                Text(if (showForm) "Tutup Form" else "Ajukan Izin")
            }
        }

        // Success/Error messages
        if (successMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
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
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Form Pengajuan Izin
        if (showForm) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Form Pengajuan Izin",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Tanggal
                    OutlinedTextField(
                        value = tanggal,
                        onValueChange = { tanggal = it },
                        label = { Text("Tanggal (YYYY-MM-DD)") },
                        placeholder = { Text("2025-12-03") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        singleLine = true
                    )

                    // Jenis Izin
                    GuruDropdownSpinner(
                        selectedValue = jenisIzin,
                        label = "Jenis Izin",
                        options = jenisIzinOptions,
                        onValueChange = { jenisIzin = it },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )

                    // Keterangan
                    OutlinedTextField(
                        value = keterangan,
                        onValueChange = { keterangan = it },
                        label = { Text("Keterangan") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        minLines = 2,
                        maxLines = 4
                    )

                    // Submit button
                    Button(
                        onClick = {
                            scope.launch {
                                isSubmitting = true
                                errorMessage = ""
                                successMessage = ""
                                try {
                                    val request = CreateGuruIzinRequest(
                                        tanggal = tanggal,
                                        jenis_izin = jenisIzin,
                                        keterangan = keterangan.ifEmpty { null }
                                    )
                                    val response = ApiClient.api.createGuruIzin("Bearer $token", request)
                                    successMessage = "Pengajuan izin berhasil!"
                                    showForm = false
                                    tanggal = ""
                                    keterangan = ""
                                    loadIzinList()
                                } catch (e: Exception) {
                                    Log.e("GuruIzin", "Error creating izin", e)
                                    errorMessage = "Gagal: ${e.message}"
                                } finally {
                                    isSubmitting = false
                                }
                            }
                        },
                        enabled = !isSubmitting && tanggal.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("Kirim Pengajuan")
                        }
                    }
                }
            }
        }

        // List Izin
        Text(
            text = "Riwayat Izin",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (izinList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Belum ada riwayat izin",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(izinList) { izin ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = izin.tanggal,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                val statusColor = when (izin.status.lowercase()) {
                                    "approved" -> Color(0xFF4CAF50)
                                    "rejected" -> Color(0xFFF44336)
                                    else -> Color(0xFFFF9800)
                                }
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.2f))
                                ) {
                                    Text(
                                        text = izin.status.uppercase(),
                                        color = statusColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Jenis: ${izin.jenisIzin.replace("_", " ").replaceFirstChar { it.uppercase() }}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (!izin.keterangan.isNullOrEmpty()) {
                                Text(
                                    text = "Keterangan: ${izin.keterangan}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ========== GURU PENGGANTI PAGE ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruPenggantiPage(token: String) {
    var penggantiList by remember { mutableStateOf<List<GuruPenggantiResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // Filter
    var selectedTanggal by remember { mutableStateOf("") }
    var filterMode by remember { mutableStateOf("sebagai_asli") } // sebagai_asli atau sebagai_pengganti
    
    val scope = rememberCoroutineScope()

    // Load pengganti list
    LaunchedEffect(selectedTanggal) {
        scope.launch {
            isLoading = true
            errorMessage = ""
            try {
                val response = ApiClient.api.getGuruPenggantiForGuru(
                    token = "Bearer $token",
                    tanggal = selectedTanggal.ifEmpty { null }
                )
                penggantiList = response.data
            } catch (e: Exception) {
                Log.e("GuruPengganti", "Error loading pengganti", e)
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Guru Pengganti",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Filter Tanggal
        OutlinedTextField(
            value = selectedTanggal,
            onValueChange = { selectedTanggal = it },
            label = { Text("Filter Tanggal (YYYY-MM-DD)") },
            placeholder = { Text("Kosongkan untuk semua tanggal") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            singleLine = true
        )

        // Filter Mode
        GuruDropdownSpinner(
            selectedValue = if (filterMode == "sebagai_asli") "Saya Digantikan" else "Saya Menggantikan",
            label = "Tampilkan",
            options = listOf("Saya Digantikan", "Saya Menggantikan"),
            onValueChange = { 
                filterMode = if (it == "Saya Digantikan") "sebagai_asli" else "sebagai_pengganti"
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // Error message
        if (errorMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Content
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (penggantiList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Tidak ada data guru pengganti",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(penggantiList) { pengganti ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "${pengganti.kelas ?: "-"} - ${pengganti.mapel ?: "-"}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "Guru Asli: ${pengganti.guruAsli?.nama ?: "-"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Guru Pengganti: ${pengganti.guruPengganti?.nama ?: "-"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = "Jadwal: ${pengganti.hari ?: "-"} - Jam ke ${pengganti.jamKe ?: "-"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            if (!pengganti.keterangan.isNullOrEmpty()) {
                                Text(
                                    text = "Keterangan: ${pengganti.keterangan}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
