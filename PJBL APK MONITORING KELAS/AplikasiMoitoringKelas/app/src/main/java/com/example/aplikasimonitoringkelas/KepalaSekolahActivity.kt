package com.example.aplikasimonitoringkelas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.rememberCoroutineScope

class KepalaSekolahActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sessionManager = SessionManager(this)
        val token = sessionManager.getAuthToken() ?: ""
        setContent {
            AplikasiMonitoringKelasTheme {
                KepalaSekolahScreen(
                    token = token,
                    onLogout = {
                        sessionManager.clearSession()
                        finish()
                        startActivity(Intent(this@KepalaSekolahActivity, MainActivity::class.java))
                    }
                )
            }
        }
    }
}

sealed class KepalaSekolahNavigationItem(val route: String, val title: String, val icon: ImageVector) {
    object Jadwal : KepalaSekolahNavigationItem("jadwal", "Jadwal", Icons.Default.DateRange)
    object KehadiranGuru : KepalaSekolahNavigationItem("kehadiran_guru", "Kehadiran", Icons.Default.Person)
    object Pengganti : KepalaSekolahNavigationItem("pengganti", "Pengganti", Icons.Default.List)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepalaSekolahDropdownSpinner(
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
fun KepalaSekolahScreen(token: String, onLogout: () -> Unit = {}) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf(
        KepalaSekolahNavigationItem.Jadwal,
        KepalaSekolahNavigationItem.KehadiranGuru,
        KepalaSekolahNavigationItem.Pengganti
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title, fontSize = 10.sp) },
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
                0 -> KepalaSekolahJadwalPage(token = token, onLogout = onLogout)
                1 -> KepsekKehadiranGuruPage(token = token)
                2 -> KepsekGuruPenggantiPage(token = token)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepalaSekolahJadwalPage(token: String, onLogout: () -> Unit = {}) {
    var selectedHari by remember { mutableStateOf("Senin") }
    var selectedKelasId by remember { mutableStateOf<Int?>(null) }
    var selectedKelasName by remember { mutableStateOf("Pilih Kelas") }
    
    var jadwalMasukList by remember { mutableStateOf<List<GuruMengajarResponse>>(emptyList()) }
    var kelasList by remember { mutableStateOf<List<KelasResponse>>(emptyList()) }
    var guruMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var kodeGuruMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var mapelMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var isLoadingJadwal by remember { mutableStateOf(false) }
    var errorJadwal by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val daftarHari = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
    
    // Load kelas dari API
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                kelasList = ApiClient.api.getKelas("Bearer $token")
                if (kelasList.isNotEmpty()) {
                    selectedKelasId = kelasList[0].id
                    selectedKelasName = kelasList[0].kelas
                }
            } catch (e: Exception) {
                Log.e("KepsekJadwal", "Error loading kelas", e)
            }
        }
    }

    LaunchedEffect(selectedHari, selectedKelasId) {
        if (selectedKelasId == null) return@LaunchedEffect
        scope.launch {
            isLoadingJadwal = true
            try {
                val allData = ApiClient.api.getGuruMengajars("Bearer $token")
                
                val gurusData = try { ApiClient.api.getGurus("Bearer $token") } catch (e: Exception) { emptyList() }
                val mapelsData = try { ApiClient.api.getMapels("Bearer $token") } catch (e: Exception) { emptyList() }
                
                guruMap = gurusData.associate { g -> (g.id ?: 0) to (g.guru ?: "") }
                kodeGuruMap = gurusData.associate { g -> (g.id ?: 0) to (g.kodeGuru ?: "") }
                mapelMap = mapelsData.associate { m -> m.id to m.mapel }
                
                jadwalMasukList = allData.filter { data ->
                    val hariMatch = data.hari == selectedHari
                    val kelasMatch = data.kelasId == selectedKelasId
                    val statusMatch = data.status.lowercase() == "masuk"
                    hariMatch && kelasMatch && statusMatch
                }.sortedBy { it.jamKe }
                
                errorJadwal = ""
            } catch (e: Exception) {
                errorJadwal = "Error: ${e.message}"
            } finally {
                isLoadingJadwal = false
            }
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
            Text(
                text = "Login sebagai: Kepala Sekolah",
                style = MaterialTheme.typography.titleLarge
            )
            
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Logout")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        KepalaSekolahDropdownSpinner(
            selectedValue = selectedHari,
            label = "Pilih Hari",
            options = daftarHari,
            onValueChange = { selectedHari = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        KepalaSekolahDropdownSpinner(
            selectedValue = selectedKelasName,
            label = "Pilih Kelas",
            options = kelasList.map { it.kelas },
            onValueChange = { name ->
                val kelas = kelasList.find { it.kelas == name }
                selectedKelasId = kelas?.id
                selectedKelasName = name
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        if (isLoadingJadwal) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        else if (errorJadwal.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorJadwal,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        else if (jadwalMasukList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tidak ada guru yang masuk untuk $selectedKelasName - $selectedHari",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(jadwalMasukList) { data ->
                    val guruName = data.namaGuru ?: data.jadwal?.guru?.guru ?: guruMap[data.jadwal?.guruId] ?: "Unknown"
                    val kodeGuru = data.kodeGuru ?: data.jadwal?.guru?.kodeGuru ?: kodeGuruMap[data.jadwal?.guruId] ?: "-"
                    val mapelName = data.mapel ?: data.jadwal?.mapel?.mapel ?: mapelMap[data.jadwal?.mapelId] ?: "Unknown"
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Jam Ke
                            Text(
                                text = "Jam ke ${data.jamKe ?: data.jadwal?.jamKe ?: "-"}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // Mata Pelajaran
                            Text(
                                text = "Mata Pelajaran: $mapelName",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black,
                                fontSize = 15.sp
                            )
                            
                            // Kode Guru & Nama Guru
                            if (kodeGuru != "-") {
                                Text(
                                    text = "Kode Guru: $kodeGuru",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                            
                            Text(
                                text = "Guru: $guruName",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black,
                                fontSize = 15.sp
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // Status
                            Text(
                                text = "Status: ${data.status ?: "-"}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = if (data.status == "Masuk") Color(0xFF2E7D32) else Color(0xFFC62828),
                                fontSize = 15.sp
                            )
                            
                            // Keterangan
                            if (!data.keterangan.isNullOrEmpty()) {
                                Text(
                                    text = "Keterangan: ${data.keterangan}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    fontSize = 14.sp
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
fun KelasKosongPage(token: String) {
    var selectedHari by remember { mutableStateOf("Senin") }
    var selectedKelas by remember { mutableStateOf(1) }
    
    var kelasKosongList by remember { mutableStateOf<List<GuruMengajarResponse>>(emptyList()) }
    var guruMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var kodeGuruMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var mapelMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val daftarHari = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")
    val daftarKelasNama = listOf("X RPL", "XI RPL", "XII RPL")
    val daftarKelasId = listOf(1, 2, 3)

    LaunchedEffect(selectedHari, selectedKelas) {
        scope.launch {
            isLoading = true
            try {
                val allData = ApiClient.api.getGuruMengajars("Bearer $token")
                
                val gurusData = try { ApiClient.api.getGurus("Bearer $token") } catch (e: Exception) { emptyList() }
                val mapelsData = try { ApiClient.api.getMapels("Bearer $token") } catch (e: Exception) { emptyList() }
                
                guruMap = gurusData.associate { g -> (g.id ?: 0) to (g.guru ?: "") }
                kodeGuruMap = gurusData.associate { g -> (g.id ?: 0) to (g.kodeGuru ?: "") }
                mapelMap = mapelsData.associate { m -> m.id to m.mapel }
                
                kelasKosongList = allData.filter { data ->
                    val hariMatch = data.hari == selectedHari
                    val kelasMatch = data.kelasId == selectedKelas
                    val statusMatch = data.status.lowercase() == "tidak masuk"
                    hariMatch && kelasMatch && statusMatch
                }.sortedBy { it.jamKe }
                
                errorMessage = ""
            } catch (e: Exception) {
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
        Text(
            text = "Daftar Kelas Kosong",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        KepalaSekolahDropdownSpinner(
            selectedValue = selectedHari,
            label = "Pilih Hari",
            options = daftarHari,
            onValueChange = { selectedHari = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        var selectedKelasIndex by remember { mutableStateOf(0) }
        KepalaSekolahDropdownSpinner(
            selectedValue = daftarKelasNama[selectedKelasIndex],
            label = "Pilih Kelas",
            options = daftarKelasNama,
            onValueChange = {
                selectedKelasIndex = daftarKelasNama.indexOf(it)
                selectedKelas = daftarKelasId[selectedKelasIndex]
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        else if (errorMessage.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        else if (kelasKosongList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tidak ada kelas kosong untuk ${daftarKelasNama[selectedKelasIndex]} - $selectedHari",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(kelasKosongList) { data ->
                    val guruName = data.namaGuru ?: data.jadwal?.guru?.guru ?: guruMap[data.jadwal?.guruId] ?: "Unknown"
                    val kodeGuru = data.kodeGuru ?: data.jadwal?.guru?.kodeGuru ?: kodeGuruMap[data.jadwal?.guruId] ?: "-"
                    val mapelName = data.mapel ?: data.jadwal?.mapel?.mapel ?: mapelMap[data.jadwal?.mapelId] ?: "Unknown"
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Jam Ke
                            Text(
                                text = "Jam ke ${data.jamKe ?: data.jadwal?.jamKe ?: "-"}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // Mata Pelajaran
                            Text(
                                text = "Mata Pelajaran: $mapelName",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black,
                                fontSize = 15.sp
                            )
                            
                            // Kode Guru & Nama Guru
                            if (kodeGuru != "-") {
                                Text(
                                    text = "Kode Guru: $kodeGuru",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                            
                            Text(
                                text = "Guru: $guruName",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black,
                                fontSize = 15.sp
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // Status
                            Text(
                                text = "Status: ${data.status ?: "-"}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = Color(0xFFC62828),
                                fontSize = 15.sp
                            )
                            
                            // Keterangan
                            if (!data.keterangan.isNullOrEmpty()) {
                                Text(
                                    text = "Keterangan: ${data.keterangan}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    fontSize = 14.sp
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
fun KepalaSekolahListPage(token: String) {
    var selectedHari by remember { mutableStateOf("Senin") }
    var selectedKelas by remember { mutableStateOf(1) }
    
    var guruMengajarList by remember { mutableStateOf<List<GuruMengajarResponse>>(emptyList()) }
    var guruMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var kodeGuruMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var mapelMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val daftarHari = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")
    val daftarKelasNama = listOf("X RPL", "XI RPL", "XII RPL")
    val daftarKelasId = listOf(1, 2, 3)

    LaunchedEffect(selectedHari, selectedKelas) {
        scope.launch {
            isLoading = true
            try {
                val allData = ApiClient.api.getGuruMengajars("Bearer $token")
                
                val gurusData = try { ApiClient.api.getGurus("Bearer $token") } catch (e: Exception) { emptyList() }
                val mapelsData = try { ApiClient.api.getMapels("Bearer $token") } catch (e: Exception) { emptyList() }
                
                guruMap = gurusData.associate { g -> (g.id ?: 0) to (g.guru ?: "") }
                kodeGuruMap = gurusData.associate { g -> (g.id ?: 0) to (g.kodeGuru ?: "") }
                mapelMap = mapelsData.associate { m -> m.id to m.mapel }
                
                guruMengajarList = allData.filter { data ->
                    val hariMatch = data.hari == selectedHari
                    val kelasMatch = data.kelasId == selectedKelas
                    hariMatch && kelasMatch
                }.sortedBy { it.jamKe }
                
                errorMessage = ""
            } catch (e: Exception) {
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
        Text(
            text = "Daftar Kehadiran Guru",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        KepalaSekolahDropdownSpinner(
            selectedValue = selectedHari,
            label = "Pilih Hari",
            options = daftarHari,
            onValueChange = { selectedHari = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        var selectedKelasIndex by remember { mutableStateOf(0) }
        KepalaSekolahDropdownSpinner(
            selectedValue = daftarKelasNama[selectedKelasIndex],
            label = "Pilih Kelas",
            options = daftarKelasNama,
            onValueChange = {
                selectedKelasIndex = daftarKelasNama.indexOf(it)
                selectedKelas = daftarKelasId[selectedKelasIndex]
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        else if (errorMessage.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        else if (guruMengajarList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tidak ada data untuk ${daftarKelasNama[selectedKelasIndex]} - $selectedHari",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(guruMengajarList) { data ->
                    val guruName = data.namaGuru ?: data.jadwal?.guru?.guru ?: guruMap[data.jadwal?.guruId] ?: "Unknown"
                    val kodeGuru = data.kodeGuru ?: data.jadwal?.guru?.kodeGuru ?: kodeGuruMap[data.jadwal?.guruId] ?: "-"
                    val mapelName = data.mapel ?: data.jadwal?.mapel?.mapel ?: mapelMap[data.jadwal?.mapelId] ?: "Unknown"
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Jam Ke
                            Text(
                                text = "Jam ke ${data.jamKe ?: data.jadwal?.jamKe ?: "-"}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // Mata Pelajaran
                            Text(
                                text = "Mata Pelajaran: $mapelName",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black,
                                fontSize = 15.sp
                            )
                            
                            // Kode Guru & Nama Guru
                            if (kodeGuru != "-") {
                                Text(
                                    text = "Kode Guru: $kodeGuru",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                            
                            Text(
                                text = "Guru: $guruName",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black,
                                fontSize = 15.sp
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // Status
                            val statusColor = if (data.status == "Masuk") Color(0xFF2E7D32) else Color(0xFFC62828)
                            Text(
                                text = "Status: ${data.status ?: "-"}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = statusColor,
                                fontSize = 15.sp
                            )
                            
                            // Keterangan
                            if (!data.keterangan.isNullOrEmpty()) {
                                Text(
                                    text = "Keterangan: ${data.keterangan}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ===========================================
// KEPSEK DASHBOARD PAGE
// ===========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepsekDashboardPage(token: String, onLogout: () -> Unit = {}) {
    var totalGuru by remember { mutableStateOf(0) }
    var totalMapel by remember { mutableStateOf(0) }
    var totalKelas by remember { mutableStateOf(0) }
    var guruMasukHariIni by remember { mutableStateOf(0) }
    var guruTidakMasukHariIni by remember { mutableStateOf(0) }
    var guruIzinPending by remember { mutableStateOf(0) }
    var guruPenggantiAktif by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Get current day
    val currentDay = remember {
        val dayOfWeek = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK)
        when (dayOfWeek) {
            java.util.Calendar.MONDAY -> "Senin"
            java.util.Calendar.TUESDAY -> "Selasa"
            java.util.Calendar.WEDNESDAY -> "Rabu"
            java.util.Calendar.THURSDAY -> "Kamis"
            java.util.Calendar.FRIDAY -> "Jumat"
            else -> "Senin"
        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            try {
                // Fetch all data
                val gurus = try { ApiClient.api.getGurus("Bearer $token") } catch (e: Exception) { emptyList() }
                val mapels = try { ApiClient.api.getMapels("Bearer $token") } catch (e: Exception) { emptyList() }
                val kelass = try { ApiClient.api.getKelas("Bearer $token") } catch (e: Exception) { emptyList() }
                val guruMengajars = try { ApiClient.api.getGuruMengajars("Bearer $token") } catch (e: Exception) { emptyList() }
                val guruIzins = try { ApiClient.api.getKepsekGuruIzin("Bearer $token").data } catch (e: Exception) { emptyList() }
                val guruPenggantis = try { ApiClient.api.getKepsekGuruPengganti("Bearer $token").data } catch (e: Exception) { emptyList() }

                totalGuru = gurus.size
                totalMapel = mapels.size
                totalKelas = kelass.size

                // Filter guru mengajar for today
                val todayData = guruMengajars.filter { it.hari == currentDay }
                guruMasukHariIni = todayData.count { it.status.lowercase() == "masuk" }
                guruTidakMasukHariIni = todayData.count { it.status.lowercase() == "tidak masuk" }

                // Count pending izin
                guruIzinPending = guruIzins.count { it.status?.lowercase() == "pending" }

                // Count active pengganti (today)
                guruPenggantiAktif = guruPenggantis.size


                errorMessage = ""
            } catch (e: Exception) {
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
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dashboard Kepala Sekolah",
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

        Text(
            text = "Hari ini: $currentDay",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Statistics Cards
                item {
                    Text(
                        text = "Statistik Umum",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardStatCard(
                            title = "Total Guru",
                            value = totalGuru.toString(),
                            color = Color(0xFF1976D2),
                            modifier = Modifier.weight(1f)
                        )
                        DashboardStatCard(
                            title = "Total Mapel",
                            value = totalMapel.toString(),
                            color = Color(0xFF388E3C),
                            modifier = Modifier.weight(1f)
                        )
                        DashboardStatCard(
                            title = "Total Kelas",
                            value = totalKelas.toString(),
                            color = Color(0xFFF57C00),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text(
                        text = "Kehadiran Hari Ini",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardStatCard(
                            title = "Guru Masuk",
                            value = guruMasukHariIni.toString(),
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.weight(1f)
                        )
                        DashboardStatCard(
                            title = "Tidak Masuk",
                            value = guruTidakMasukHariIni.toString(),
                            color = Color(0xFFC62828),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text(
                        text = "Status Izin & Pengganti",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DashboardStatCard(
                            title = "Izin Pending",
                            value = guruIzinPending.toString(),
                            color = Color(0xFFFF9800),
                            modifier = Modifier.weight(1f)
                        )
                        DashboardStatCard(
                            title = "Guru Pengganti",
                            value = guruPenggantiAktif.toString(),
                            color = Color(0xFF7B1FA2),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

// ===========================================
// KEPSEK GURU PENGGANTI PAGE
// ===========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepsekGuruPenggantiPage(token: String) {
    var allDataList by remember { mutableStateOf<List<GuruMengajarResponse>>(emptyList()) }
    var filteredList by remember { mutableStateOf<List<GuruMengajarResponse>>(emptyList()) }
    var kelasList by remember { mutableStateOf<List<KelasResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    
    // Filter states
    var selectedHari by remember { mutableStateOf("Senin") }
    var selectedKelasId by remember { mutableStateOf<Int?>(null) }
    var selectedKelasName by remember { mutableStateOf("Semua Kelas") }
    
    val scope = rememberCoroutineScope()
    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")

    // Load kelas
    LaunchedEffect(Unit) {
        try {
            kelasList = ApiClient.api.getKelas("Bearer $token")
        } catch (e: Exception) {
            Log.e("KepsekGuruPengganti", "Error loading kelas", e)
        }
    }

    // Load data guru mengajar yang sudah ada penggantinya
    LaunchedEffect(selectedHari, selectedKelasId) {
        scope.launch {
            isLoading = true
            try {
                val response = ApiClient.api.getGuruMengajar(
                    token = "Bearer $token",
                    hari = selectedHari,
                    kelasId = selectedKelasId
                )
                allDataList = response.data
                errorMessage = ""
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
                Log.e("KepsekGuruPengganti", "Error loading data", e)
            } finally {
                isLoading = false
            }
        }
    }
    
    // Filter hanya yang sudah ada guru pengganti
    LaunchedEffect(allDataList) {
        filteredList = allDataList.filter { it.guruPenggantiId != null }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Daftar Guru Pengganti",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Filter Hari
        KepalaSekolahDropdownSpinner(
            selectedValue = selectedHari,
            label = "Hari",
            options = hariList,
            onValueChange = { selectedHari = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        
        // Filter Kelas
        KepalaSekolahDropdownSpinner(
            selectedValue = selectedKelasName,
            label = "Kelas",
            options = listOf("Semua Kelas") + kelasList.map { it.kelas },
            onValueChange = { selected ->
                if (selected == "Semua Kelas") {
                    selectedKelasId = null
                    selectedKelasName = "Semua Kelas"
                } else {
                    selectedKelasId = kelasList.find { it.kelas == selected }?.id
                    selectedKelasName = selected
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tidak ada data guru pengganti",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredList) { data ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Kelas & Mapel
                            Text(
                                text = "${data.jadwal?.kelas?.kelas ?: "-"} - ${data.mapel}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Guru Asli -> Guru Pengganti
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Guru Asli",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = data.namaGuru,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Icon(
                                    Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    tint = Color.Gray
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Guru Pengganti",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = data.guruPengganti?.guru ?: "-",
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))

                            // Jadwal
                            Text(
                                text = "Jam ke ${data.jamKe}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            
                            // Tampilkan durasi izin jika ada
                            if (data.status.lowercase() == "izin" && data.tanggalMulaiIzin != null && data.tanggalSelesaiIzin != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Color(0xFFFF9800).copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = Color(0xFFFF9800),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Column {
                                        Text(
                                            text = "Izin: ${data.tanggalMulaiIzin} s/d ${data.tanggalSelesaiIzin}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFFF9800),
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Durasi: ${data.durasiIzin ?: 0} hari",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFFF9800)
                                        )
                                    }
                                }
                            }

                            // Keterangan
                            if (data.keterangan.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Keterangan: ${data.keterangan}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ========== KEPSEK KEHADIRAN GURU PAGE ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepsekKehadiranGuruPage(token: String) {
    var selectedHari by remember { mutableStateOf("Senin") }
    var selectedKelasId by remember { mutableStateOf<Int?>(null) }
    var selectedKelasName by remember { mutableStateOf("Semua Kelas") }
    var selectedFilter by remember { mutableStateOf("Semua Data") }
    
    var allKehadiranList by remember { mutableStateOf<List<GuruMengajarResponse>>(emptyList()) }
    var filteredKehadiranList by remember { mutableStateOf<List<GuruMengajarResponse>>(emptyList()) }
    var kelasList by remember { mutableStateOf<List<KelasResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val daftarHari = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
    val filterOptions = listOf("Semua Data", "Kelas Kosong", "Guru Masuk")

    // Load kelas
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                kelasList = ApiClient.api.getKelas("Bearer $token")
            } catch (e: Exception) {
                Log.e("KepsekKehadiranGuru", "Error loading kelas", e)
            }
        }
    }

    // Load kehadiran guru with API filter
    LaunchedEffect(selectedHari, selectedKelasId) {
        scope.launch {
            isLoading = true
            errorMessage = ""
            try {
                val response = ApiClient.api.getGuruMengajar(
                    token = "Bearer $token",
                    hari = selectedHari,
                    kelasId = selectedKelasId
                )
                allKehadiranList = response.data
            } catch (e: Exception) {
                Log.e("KepsekKehadiranGuru", "Error", e)
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Apply client-side filter based on selectedFilter
    LaunchedEffect(allKehadiranList, selectedFilter) {
        filteredKehadiranList = when (selectedFilter) {
            "Kelas Kosong" -> {
                // Kelas kosong: status tidak masuk/izin DAN belum ada guru pengganti
                allKehadiranList.filter { data ->
                    (data.status.lowercase() == "tidak masuk" || data.status.lowercase() == "izin") &&
                    data.guruPenggantiId == null
                }
            }
            "Guru Masuk" -> {
                // Guru masuk: status masuk ATAU sudah ada guru pengganti
                allKehadiranList.filter { data ->
                    data.status.lowercase() == "masuk" || data.guruPenggantiId != null
                }
            }
            else -> allKehadiranList
        }
    }

    // Calculate statistics
    val totalData = allKehadiranList.size
    val kelasKosongCount = allKehadiranList.count { data ->
        (data.status.lowercase() == "tidak masuk" || data.status.lowercase() == "izin") &&
        data.guruPenggantiId == null
    }
    val guruMasukCount = allKehadiranList.count { data ->
        data.status.lowercase() == "masuk" || data.guruPenggantiId != null
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Kehadiran Guru",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Filter Hari
        KepalaSekolahDropdownSpinner(
            selectedValue = selectedHari,
            label = "Pilih Hari",
            options = daftarHari,
            onValueChange = { selectedHari = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        // Filter Kelas
        KepalaSekolahDropdownSpinner(
            selectedValue = selectedKelasName,
            label = "Pilih Kelas",
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
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        // Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterOptions.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Statistics Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$totalData", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(text = "Total", style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$kelasKosongCount", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFFF44336))
                Text(text = "Kelas Kosong", style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$guruMasukCount", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF4CAF50))
                Text(text = "Guru Masuk", style = MaterialTheme.typography.bodySmall)
            }
        }

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (filteredKehadiranList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tidak ada data kehadiran guru")
            }
        } else {
            LazyColumn {
                items(filteredKehadiranList) { data ->
                    // Tentukan apakah sudah diganti
                    val isDiganti = (data.status.lowercase() == "tidak masuk" || data.status.lowercase() == "izin") &&
                                    data.guruPenggantiId != null
                    
                    val displayStatus = if (isDiganti) "DIGANTI" else data.status.uppercase()
                    
                    val statusColor = when {
                        isDiganti -> Color(0xFF2196F3) // Blue for DIGANTI
                        data.status.lowercase() == "masuk" || data.status.lowercase() == "hadir" -> Color(0xFF4CAF50)
                        data.status.lowercase() == "tidak masuk" || data.status.lowercase() == "tidak hadir" -> Color(0xFFF44336)
                        data.status.lowercase() == "izin" -> Color(0xFFFF9800)
                        data.status.lowercase() == "telat" -> Color(0xFFFF9800)
                        else -> Color.Gray
                    }
                    
                    val isKelasKosong = (data.status.lowercase() == "tidak masuk" || data.status.lowercase() == "izin") &&
                                        data.guruPenggantiId == null
                    
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = if (isKelasKosong) {
                            CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                        } else {
                            CardDefaults.cardColors()
                        }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = data.jadwal?.kelas?.kelas ?: "Kelas -",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Jam ke ${data.jamKe}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                                Card(colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.2f))) {
                                    Text(
                                        text = displayStatus,
                                        color = statusColor,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(text = "Guru: ${data.namaGuru}")
                            Text(text = "Mapel: ${data.mapel}")
                            
                            // Show guru pengganti if exists
                            if (data.guruPengganti != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Color(0xFF4CAF50).copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Digantikan oleh: ${data.guruPengganti?.guru ?: "-"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF4CAF50),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            } else if (isKelasKosong) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Color(0xFFF44336).copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFF44336),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Belum ada guru pengganti",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFF44336),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            
                            // Tampilkan durasi izin jika ada
                            if (data.status.lowercase() == "izin" && data.tanggalMulaiIzin != null && data.tanggalSelesaiIzin != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Color(0xFFFF9800).copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = Color(0xFFFF9800),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Column {
                                        Text(
                                            text = "Izin: ${data.tanggalMulaiIzin} s/d ${data.tanggalSelesaiIzin}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFFFF9800),
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Durasi: ${data.durasiIzin ?: 0} hari",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFFF9800)
                                        )
                                    }
                                }
                            }
                            
                            if (data.keterangan.isNotEmpty()) {
                                Text(
                                    text = "Ket: ${data.keterangan}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ========== KEPSEK KEHADIRAN SISWA PAGE ==========
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepsekKehadiranSiswaPage(token: String) {
    var selectedKelasId by remember { mutableStateOf<Int?>(null) }
    var selectedKelasName by remember { mutableStateOf("X RPL") }
    
    var kehadiranList by remember { mutableStateOf<List<KehadiranSiswaResponse>>(emptyList()) }
    var kelasList by remember { mutableStateOf<List<KelasResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

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
                Log.e("KepsekKehadiranSiswa", "Error loading kelas", e)
            }
        }
    }

    // Load kehadiran siswa
    LaunchedEffect(selectedKelasId) {
        if (selectedKelasId == null) return@LaunchedEffect
        scope.launch {
            isLoading = true
            errorMessage = ""
            try {
                val response = ApiClient.api.getKepsekKehadiranSiswa(
                    token = "Bearer $token",
                    kelasId = selectedKelasId
                )
                kehadiranList = response.data
            } catch (e: Exception) {
                Log.e("KepsekKehadiranSiswa", "Error", e)
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Kehadiran Siswa",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Filter Kelas
        KepalaSekolahDropdownSpinner(
            selectedValue = selectedKelasName,
            label = "Pilih Kelas",
            options = kelasList.map { it.kelas },
            onValueChange = { name ->
                val kelas = kelasList.find { it.kelas == name }
                selectedKelasId = kelas?.id
                selectedKelasName = name
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (kehadiranList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tidak ada data kehadiran siswa untuk kelas ini")
            }
        } else {
            LazyColumn {
                items(kehadiranList) { data ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Tanggal: ${data.tanggal}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Grid statistik
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${data.jumlahHadir}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4CAF50)
                                    )
                                    Text(text = "Hadir", style = MaterialTheme.typography.bodySmall)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${data.jumlahSakit}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2196F3)
                                    )
                                    Text(text = "Sakit", style = MaterialTheme.typography.bodySmall)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${data.jumlahIzin}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFF9800)
                                    )
                                    Text(text = "Izin", style = MaterialTheme.typography.bodySmall)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${data.jumlahAlpha}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF44336)
                                    )
                                    Text(text = "Alpha", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            
                            if (!data.keterangan.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Ket: ${data.keterangan}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
// ===========================================
// KEPSEK GURU IZIN PAGE
// ===========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KepsekGuruIzinPage(token: String) {
    var guruIzinList by remember { mutableStateOf<List<GuruIzinResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            try {
                guruIzinList = ApiClient.api.getKepsekGuruIzin("Bearer $token").data
                errorMessage = ""
            } catch (e: Exception) {
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
        Text(
            text = "Daftar Guru Izin",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else if (guruIzinList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tidak ada data guru izin",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(guruIzinList) { data ->
                    val statusColor = when (data.status?.lowercase()) {
                        "approved" -> Color(0xFF2E7D32)
                        "rejected" -> Color(0xFFC62828)
                        else -> Color(0xFFFF9800)
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Nama Guru
                            Text(
                                text = data.guru?.guru ?: "Guru ID: ${data.guruId}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Tanggal
                            Row {
                                Text(
                                    text = "Tanggal: ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = data.tanggal,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            // Jenis Izin
                            Row {
                                Text(
                                    text = "Jenis Izin: ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = data.jenisIzin.replace("_", " ").replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Status
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Status: ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = data.status.uppercase(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor
                                )
                            }

                            // Keterangan
                            if (!data.keterangan.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Keterangan: ${data.keterangan}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
