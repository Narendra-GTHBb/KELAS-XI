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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

class KurikulumActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sessionManager = SessionManager(this)
        val token = sessionManager.getAuthToken() ?: ""
        setContent {
            AplikasiMonitoringKelasTheme {
                KurikulumScreen(
                    token = token,
                    onLogout = {
                        sessionManager.clearSession()
                        finish()
                        startActivity(Intent(this@KurikulumActivity, MainActivity::class.java))
                    }
                )
            }
        }
    }
}

sealed class KurikulumNavigationItem(val route: String, val title: String, val icon: ImageVector) {
    object JadwalPelajaran : KurikulumNavigationItem("jadwal", "Jadwal", Icons.Default.DateRange)
    object KehadiranGuru : KurikulumNavigationItem("kehadiran", "Kehadiran", Icons.Default.Check)
    object GuruPengganti : KurikulumNavigationItem("pengganti", "Pengganti", Icons.Default.Edit)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KurikulumDropdownSpinner(
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
fun KurikulumScreen(token: String, onLogout: () -> Unit = {}) {
    var selectedItem by remember { mutableStateOf(0) }
    
    // Shared state untuk auto-fill dari Jadwal ke Ganti Guru
    var autoFillData by remember { mutableStateOf<GuruMengajarResponse?>(null) }
    var autoFillHari by remember { mutableStateOf("Senin") }
    var autoFillKelasId by remember { mutableStateOf<Int?>(null) }
    
    val items = listOf(
        KurikulumNavigationItem.JadwalPelajaran,
        KurikulumNavigationItem.KehadiranGuru,
        KurikulumNavigationItem.GuruPengganti
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
                0 -> KurikulumJadwalPelajaranPage(
                    token = token,
                    onJadwalClick = { jadwal, hari, kelasId ->
                        autoFillData = jadwal
                        autoFillHari = hari
                        autoFillKelasId = kelasId
                        selectedItem = 2 // Pindah ke tab Guru Pengganti
                    },
                    onLogout = onLogout
                )
                1 -> KurikulumKehadiranGuruPage(token = token)
                2 -> KurikulumGuruPenggantiPage(
                    token = token,
                    initialData = autoFillData,
                    initialHari = autoFillHari,
                    initialKelasId = autoFillKelasId,
                    onDataUsed = { autoFillData = null }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KurikulumJadwalPelajaranPage(
    token: String,
    onJadwalClick: (GuruMengajarResponse, String, Int) -> Unit = { _, _, _ -> },
    onLogout: () -> Unit = {}
) {
    var selectedHari by remember { mutableStateOf("Senin") }
    var selectedKelasId by remember { mutableStateOf<Int?>(null) }
    var expandedHari by remember { mutableStateOf(false) }
    var expandedKelas by remember { mutableStateOf(false) }
    
    var jadwalList by remember { mutableStateOf<List<GuruMengajarResponse>>(emptyList()) }
    var kelasList by remember { mutableStateOf<List<KelasResponse>>(emptyList()) }
    var guruMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var kodeGuruMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var mapelMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var kelasMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val daftarHari = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")

    // Load kelas list
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                kelasList = ApiClient.api.getKelas("Bearer $token")
                android.util.Log.d("KurikulumActivity", "Kelas loaded: ${kelasList.size} items")
                kelasList.forEach { kelas ->
                    android.util.Log.d("KurikulumActivity", "Kelas: id=${kelas.id}, nama='${kelas.kelas}'")
                }
                kelasMap = kelasList.associate { it.id to it.kelas }
                if (kelasList.isNotEmpty()) {
                    selectedKelasId = kelasList[0].id
                    android.util.Log.d("KurikulumActivity", "Default selectedKelasId: $selectedKelasId (${kelasMap[selectedKelasId]})")
                }
            } catch (e: Exception) {
                errorMessage = "Error loading kelas: ${e.message}"
                android.util.Log.e("KurikulumActivity", "Error loading kelas", e)
            }
        }
    }

    // Load jadwal based on selected hari and kelas
    LaunchedEffect(selectedHari, selectedKelasId) {
        if (selectedKelasId == null) return@LaunchedEffect
        
        scope.launch {
            isLoading = true
            try {
                val allData = ApiClient.api.getGuruMengajars("Bearer $token")
                android.util.Log.d("KurikulumActivity", "=== DATA RECEIVED ===")
                android.util.Log.d("KurikulumActivity", "Total data received: ${allData.size}")
                android.util.Log.d("KurikulumActivity", "Filter criteria - selectedHari: '$selectedHari', selectedKelasId: $selectedKelasId")
                
                // Log first 5 items to see structure
                allData.take(5).forEachIndexed { index, item ->
                    android.util.Log.d("KurikulumActivity", "Item[$index]: hari='${item.hari}', kelasId=${item.kelasId}")
                }
                
                // Log all unique hari values
                val allHari = allData.map { it.hari }.filter { it.isNotEmpty() }.distinct()
                android.util.Log.d("KurikulumActivity", "Available hari values: $allHari")
                
                // Log all unique kelasId values
                val allKelasId = allData.map { it.kelasId }.filter { it > 0 }.distinct()
                android.util.Log.d("KurikulumActivity", "Available kelasId values: $allKelasId")
                
                // Auto-adjust hari if selected hari has no data
                val normalizedSelectedHari = selectedHari.trim().lowercase()
                val availableHariNormalized = allHari.map { it.trim().lowercase() }
                val selectedHariExists = availableHariNormalized.contains(normalizedSelectedHari)
                
                var hariToUse = selectedHari
                if (!selectedHariExists && allHari.isNotEmpty()) {
                    // Find first hari from daftarHari that exists in data
                    val firstAvailableHari = daftarHari.firstOrNull { hari ->
                        availableHariNormalized.contains(hari.trim().lowercase())
                    }
                    if (firstAvailableHari != null && firstAvailableHari != selectedHari) {
                        android.util.Log.d("KurikulumActivity", "Auto-adjusting hari from '$selectedHari' to '$firstAvailableHari' (no data for selected hari)")
                        hariToUse = firstAvailableHari
                        // Update state - this will trigger LaunchedEffect again, but with correct hari
                        selectedHari = firstAvailableHari
                    }
                }
                
                // Build lookup maps
                val gurusData = try { ApiClient.api.getGurus("Bearer $token") } catch (e: Exception) { emptyList() }
                val mapelsData = try { ApiClient.api.getMapels("Bearer $token") } catch (e: Exception) { emptyList() }
                
                guruMap = gurusData.associate { g -> (g.id ?: 0) to (g.guru ?: "") }
                kodeGuruMap = gurusData.associate { g -> (g.id ?: 0) to (g.kodeGuru ?: "") }
                mapelMap = mapelsData.associate { m -> m.id to m.mapel }
                
                // Filter by hari and kelas - menggunakan flat field langsung
                val normalizedSelectedHariFinal = hariToUse.trim().lowercase()
                jadwalList = allData.filter { 
                    val itemHari = it.hari.trim().lowercase()
                    val itemKelasId = it.kelasId
                    val hariMatch = itemHari == normalizedSelectedHariFinal
                    val kelasMatch = itemKelasId == selectedKelasId
                    
                    if (!hariMatch || !kelasMatch) {
                        android.util.Log.v("KurikulumActivity", "Filtered out: hari='$itemHari' (expected: '$normalizedSelectedHariFinal', match: $hariMatch), kelasId=$itemKelasId (expected: $selectedKelasId, match: $kelasMatch)")
                    }
                    
                    hariMatch && kelasMatch
                }.sortedBy { it.jamKe }
                
                android.util.Log.d("KurikulumActivity", "=== FILTER RESULT ===")
                android.util.Log.d("KurikulumActivity", "Filtered data count: ${jadwalList.size}")
                if (jadwalList.isEmpty() && allData.isNotEmpty()) {
                    android.util.Log.w("KurikulumActivity", "WARNING: No data matches filter! Check if hari/kelasId values match exactly.")
                }
                
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
        // Informasi User Login with Logout Button
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Login sebagai: Kurikulum",
                style = MaterialTheme.typography.titleMedium
            )
            
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Logout")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        // Spinner Hari
        ExposedDropdownMenuBox(
            expanded = expandedHari,
            onExpandedChange = { expandedHari = !expandedHari },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            OutlinedTextField(
                value = selectedHari,
                onValueChange = {},
                readOnly = true,
                label = { Text("Pilih Hari") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHari)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors()
            )
            ExposedDropdownMenu(
                expanded = expandedHari,
                onDismissRequest = { expandedHari = false }
            ) {
                daftarHari.forEach { hari ->
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

        // Spinner Kelas
        ExposedDropdownMenuBox(
            expanded = expandedKelas,
            onExpandedChange = { expandedKelas = !expandedKelas },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = selectedKelasId?.let { kelasMap[it] } ?: "Pilih Kelas",
                onValueChange = {},
                readOnly = true,
                label = { Text("Pilih Kelas") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedKelas)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors()
            )
            ExposedDropdownMenu(
                expanded = expandedKelas,
                onDismissRequest = { expandedKelas = false }
            ) {
                kelasList.forEach { kelas ->
                    DropdownMenuItem(
                        text = { Text(kelas.kelas) },
                        onClick = {
                            selectedKelasId = kelas.id
                            expandedKelas = false
                        }
                    )
                }
            }
        }

        // Debug info (only show if no data)
        if (jadwalList.isEmpty() && !isLoading && errorMessage.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Debug Info",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Text(
                        text = "Filter: Hari='$selectedHari', Kelas ID=$selectedKelasId",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Cek Logcat dengan filter 'KurikulumActivity' untuk detail",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage.isNotEmpty() -> {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                jadwalList.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tidak ada jadwal",
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Hari: $selectedHari, Kelas ID: $selectedKelasId",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        android.util.Log.d("KurikulumActivity", "Displaying ${jadwalList.size} items in UI")
                        jadwalList.forEach { jadwal ->
                            // Support both flat and nested structure
                            val guruName = jadwal.namaGuru 
                                ?: jadwal.jadwal?.guru?.guru 
                                ?: guruMap[jadwal.jadwal?.guruId] 
                                ?: "Unknown"
                            val kodeGuru = jadwal.kodeGuru 
                                ?: jadwal.jadwal?.guru?.kodeGuru 
                                ?: kodeGuruMap[jadwal.jadwal?.guruId] 
                                ?: "-"
                            val mapelName = jadwal.mapel 
                                ?: jadwal.jadwal?.mapel?.mapel 
                                ?: mapelMap[jadwal.jadwal?.mapelId] 
                                ?: "Unknown"
                            val jamKe = jadwal.jamKe ?: jadwal.jadwal?.jamKe ?: "-"
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable { 
                                        selectedKelasId?.let { kelasId ->
                                            onJadwalClick(jadwal, selectedHari, kelasId)
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
                                        text = "Jam Ke $jamKe",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = "Mata Pelajaran: $mapelName",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontSize = 15.sp,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = "Kode Guru: $kodeGuru",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                    Text(
                                        text = "Nama Guru: $guruName",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontSize = 14.sp,
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GantiGuruPage(
    token: String,
    initialData: GuruMengajarResponse? = null,
    initialHari: String = "Senin",
    initialKelasId: Int? = null,
    onDataUsed: () -> Unit = {}
) {
    var selectedHari by remember { mutableStateOf(initialHari) }
    var selectedKelasId by remember { mutableStateOf(initialKelasId) }
    var selectedJamKe by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("Masuk") }
    var keterangan by remember { mutableStateOf("") }
    
    // Data yang auto-fill dari jadwal
    var selectedGuruId by remember { mutableStateOf<Int?>(null) }
    var selectedMapelId by remember { mutableStateOf<Int?>(null) }
    var guruName by remember { mutableStateOf("") }
    var mapelName by remember { mutableStateOf("") }
    var kodeGuru by remember { mutableStateOf("") }
    var guruMengajarId by remember { mutableStateOf<Int?>(null) } // ID untuk UPDATE
    
    var kelasList by remember { mutableStateOf<List<KelasResponse>>(emptyList()) }
    var jadwalList by remember { mutableStateOf<List<GuruMengajarResponse>>(emptyList()) }
    var availableJamList by remember { mutableStateOf<List<String>>(emptyList()) }
    
    var guruMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var kodeGuruMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var mapelMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    
    var guruList by remember { mutableStateOf<List<Guru>>(emptyList()) }
    var mapelList by remember { mutableStateOf<List<MapelResponse>>(emptyList()) }
    
    var isSubmitting by remember { mutableStateOf(false) }
    var isLoadingJadwal by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val daftarHari = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")
    val daftarStatus = listOf("Masuk", "Tidak Masuk")
    
    // Auto-fill dari initial data (ketika click card di tab Jadwal)
    LaunchedEffect(initialData) {
        if (initialData != null) {
            // Simpan ID untuk UPDATE
            guruMengajarId = initialData.id
            
            selectedJamKe = initialData.jadwal?.jamKe ?: ""
            selectedGuruId = initialData.jadwal?.guruId
            selectedMapelId = initialData.jadwal?.mapelId
            
            guruName = initialData.jadwal?.guru?.guru ?: ""
            mapelName = initialData.jadwal?.mapel?.mapel ?: ""
            kodeGuru = initialData.jadwal?.guru?.kodeGuru ?: ""
            
            // Auto-fill status dan keterangan jika ada
            selectedStatus = initialData.status ?: "Masuk"
            keterangan = initialData.keterangan ?: ""
            
            onDataUsed() // Mark data as used
        }
    }
    
    // Load kelas, guru, dan mapel list
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                kelasList = ApiClient.api.getKelas("Bearer $token")
                if (selectedKelasId == null && kelasList.isNotEmpty()) {
                    selectedKelasId = kelasList[0].id
                }
                
                // Load guru dan mapel untuk dropdown
                guruList = ApiClient.api.getGurus("Bearer $token")
                mapelList = ApiClient.api.getMapels("Bearer $token")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    // Load jadwal when hari or kelas changes
    LaunchedEffect(selectedHari, selectedKelasId) {
        if (selectedKelasId == null) return@LaunchedEffect
        
        scope.launch {
            isLoadingJadwal = true
            try {
                val allData = ApiClient.api.getGuruMengajars("Bearer $token")
                
                // Build lookup maps
                val gurusData = try { ApiClient.api.getGurus("Bearer $token") } catch (e: Exception) { emptyList() }
                val mapelsData = try { ApiClient.api.getMapels("Bearer $token") } catch (e: Exception) { emptyList() }
                
                guruMap = gurusData.associate { g -> (g.id ?: 0) to (g.guru ?: "") }
                kodeGuruMap = gurusData.associate { g -> (g.id ?: 0) to (g.kodeGuru ?: "") }
                mapelMap = mapelsData.associate { m -> m.id to m.mapel }
                
                // Filter jadwal by hari and kelas
                jadwalList = allData.filter { 
                    it.jadwal?.hari == selectedHari && it.jadwal?.kelasId == selectedKelasId
                }.sortedBy { it.jadwal?.jamKe }
                
                // Get available jam_ke list
                availableJamList = jadwalList.mapNotNull { it.jadwal?.jamKe }.distinct()
                
                // Reset selections when filter changes (only if not from initial data)
                if (initialData == null) {
                    selectedJamKe = ""
                    selectedGuruId = null
                    selectedMapelId = null
                    guruName = ""
                    mapelName = ""
                    kodeGuru = ""
                }
                
            } catch (e: Exception) {
                errorMessage = "Error loading jadwal: ${e.message}"
            } finally {
                isLoadingJadwal = false
            }
        }
    }
    
    // Auto-fill guru and mapel when jam_ke selected
    LaunchedEffect(selectedJamKe, jadwalList) {
        if (selectedJamKe.isEmpty()) {
            if (initialData == null) {
                selectedGuruId = null
                selectedMapelId = null
                guruName = ""
                mapelName = ""
                kodeGuru = ""
            }
            return@LaunchedEffect
        }
        
        val matchedJadwal = jadwalList.find { it.jadwal?.jamKe == selectedJamKe }
        if (matchedJadwal != null) {
            selectedGuruId = matchedJadwal.jadwal?.guruId
            selectedMapelId = matchedJadwal.jadwal?.mapelId
            
            guruName = matchedJadwal.jadwal?.guru?.guru 
                ?: guruMap[matchedJadwal.jadwal?.guruId] 
                ?: "Unknown"
            
            mapelName = matchedJadwal.jadwal?.mapel?.mapel 
                ?: mapelMap[matchedJadwal.jadwal?.mapelId] 
                ?: "Unknown"
            
            kodeGuru = matchedJadwal.jadwal?.guru?.kodeGuru 
                ?: kodeGuruMap[matchedJadwal.jadwal?.guruId] 
                ?: "-"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Input Kehadiran Guru",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Success/Error messages
        if (successMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text(
                    text = successMessage,
                    color = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        
        if (errorMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF44336))
            ) {
                Text(
                    text = errorMessage,
                    color = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Spinner Hari
        KurikulumDropdownSpinner(
            selectedValue = selectedHari,
            label = "Pilih Hari",
            options = daftarHari,
            onValueChange = { selectedHari = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        // Spinner Kelas
        KurikulumDropdownSpinner(
            selectedValue = selectedKelasId?.let { id -> kelasList.find { k -> k.id == id }?.kelas } ?: "Pilih Kelas",
            label = "Pilih Kelas",
            options = kelasList.map { k -> k.kelas },
            onValueChange = { selectedKelas ->
                selectedKelasId = kelasList.find { k -> k.kelas == selectedKelas }?.id
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        // Spinner Jam Ke (from available jadwal)
        if (isLoadingJadwal) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        } else {
            KurikulumDropdownSpinner(
                selectedValue = selectedJamKe.ifEmpty { "Pilih Jam Ke" },
                label = "Jam Ke",
                options = availableJamList,
                onValueChange = { selectedJamKe = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            )
        }

        // Dropdown Guru
        KurikulumDropdownSpinner(
            selectedValue = guruName.ifEmpty { "Pilih Guru" },
            label = "Guru",
            options = guruList.mapNotNull { it.guru },
            onValueChange = { selectedName ->
                val selectedGuru = guruList.find { it.guru == selectedName }
                if (selectedGuru != null) {
                    selectedGuruId = selectedGuru.id
                    guruName = selectedGuru.guru ?: ""
                    kodeGuru = selectedGuru.kodeGuru ?: ""
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        // Dropdown Mata Pelajaran
        KurikulumDropdownSpinner(
            selectedValue = mapelName.ifEmpty { "Pilih Mata Pelajaran" },
            label = "Mata Pelajaran",
            options = mapelList.map { it.mapel },
            onValueChange = { selectedName ->
                val selectedMapel = mapelList.find { it.mapel == selectedName }
                if (selectedMapel != null) {
                    selectedMapelId = selectedMapel.id
                    mapelName = selectedMapel.mapel
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        // Display Kode Guru (read-only)
        if (kodeGuru.isNotEmpty()) {
            OutlinedTextField(
                value = kodeGuru,
                onValueChange = {},
                label = { Text("Kode Guru") },
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        // Spinner Status
        KurikulumDropdownSpinner(
            selectedValue = selectedStatus,
            label = "Status",
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

        // Tombol Simpan
        Button(
            onClick = {
                scope.launch {
                    isSubmitting = true
                    successMessage = ""
                    errorMessage = ""
                    
                    try {
                        // Validasi input terlebih dahulu
                        if (selectedJamKe.isEmpty()) {
                            errorMessage = "Pilih Jam Ke terlebih dahulu"
                            isSubmitting = false
                            return@launch
                        }
                        
                        if (selectedKelasId == null) {
                            errorMessage = "Kelas belum dipilih"
                            isSubmitting = false
                            return@launch
                        }
                        
                        // Cari jadwal_id dari jadwal yang dipilih
                        val matchedJadwal = jadwalList.find { 
                            it.jadwal?.jamKe == selectedJamKe && 
                            it.jadwal?.hari == selectedHari && 
                            it.jadwal?.kelasId == selectedKelasId
                        }
                        
                        println("DEBUG: Selected JamKe=$selectedJamKe, Hari=$selectedHari, KelasId=$selectedKelasId")
                        println("DEBUG: Matched Jadwal ID=${matchedJadwal?.jadwal?.id}")
                        println("DEBUG: GuruMengajar ID=$guruMengajarId")
                        
                        if (matchedJadwal?.jadwal?.id == null) {
                            errorMessage = "Jadwal tidak ditemukan. Pastikan jadwal untuk Jam $selectedJamKe sudah ada."
                            isSubmitting = false
                            return@launch
                        }
                        
                        // Validasi data yang diperlukan
                        if (selectedGuruId == null || selectedGuruId!! <= 0) {
                            errorMessage = "Guru belum dipilih"
                            isSubmitting = false
                            return@launch
                        }
                        
                        if (selectedMapelId == null || selectedMapelId!! <= 0) {
                            errorMessage = "Mapel belum dipilih"
                            isSubmitting = false
                            return@launch
                        }
                        
                        val request = GuruMengajarRequest(
                            hari = selectedHari,
                            kelas_id = selectedKelasId!!,
                            guru_id = selectedGuruId!!,
                            mapel_id = selectedMapelId!!,
                            jam_ke = selectedJamKe,
                            status = selectedStatus,
                            keterangan = if (keterangan.isBlank()) null else keterangan.trim()
                        )
                        
                        println("DEBUG: Request = hari=$selectedHari, kelas_id=$selectedKelasId, guru_id=$selectedGuruId, mapel_id=$selectedMapelId, jam_ke=$selectedJamKe, status=$selectedStatus")
                        
                        // Jika ada ID guru-mengajar, gunakan UPDATE. Jika tidak, gunakan CREATE
                        if (guruMengajarId != null && guruMengajarId!! > 0) {
                            println("DEBUG: Updating GuruMengajar ID=$guruMengajarId")
                            ApiClient.api.updateGuruMengajar("Bearer $token", guruMengajarId!!, request)
                            successMessage = "Data kehadiran berhasil diupdate!"
                        } else {
                            println("DEBUG: Creating new GuruMengajar")
                            val response = ApiClient.api.createGuruMengajar("Bearer $token", request)
                            println("DEBUG: Created with ID=${response.id}")
                            successMessage = "Data kehadiran berhasil disimpan!"
                        }
                        
                        // Reset form
                        guruMengajarId = null
                        selectedJamKe = ""
                        keterangan = ""
                        selectedStatus = "Masuk"
                        guruName = ""
                        mapelName = ""
                        kodeGuru = ""
                        
                    } catch (e: Exception) {
                        val errorDetail = when {
                            e.message?.contains("500") == true -> "Server Error - Periksa data yang dikirim"
                            e.message?.contains("404") == true -> "Endpoint tidak ditemukan"
                            e.message?.contains("422") == true -> "Validasi gagal - Data tidak sesuai"
                            else -> e.message ?: "Error tidak diketahui"
                        }
                        errorMessage = "Gagal menyimpan: $errorDetail"
                        println("ERROR: ${e.javaClass.simpleName} - ${e.message}")
                        e.printStackTrace()
                    } finally {
                        isSubmitting = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isSubmitting && selectedJamKe.isNotEmpty() && selectedKelasId != null
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text("Simpan Status & Keterangan")
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Untuk mengganti Guru atau Mata Pelajaran:",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF5722),
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "1. Pilih guru dan mapel baru dari dropdown di atas\n2. Klik tombol MERAH di bawah ini",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        val isGantiEnabled = !isSubmitting && selectedJamKe.isNotEmpty() && selectedGuruId != null && selectedMapelId != null && selectedKelasId != null
        
        if (!isGantiEnabled) {
            Text(
                text = if (selectedJamKe.isEmpty()) "⚠️ Pilih Jam Ke dulu" 
                       else if (selectedGuruId == null) "⚠️ Pilih Guru dulu"
                       else if (selectedMapelId == null) "⚠️ Pilih Mapel dulu"
                       else "⚠️ Pilih Kelas dulu",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFFF5722),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // Tombol Ganti Guru/Mapel di Jadwal
        Button(
            onClick = {
                scope.launch {
                    println("DEBUG GANTI: TOMBOL DIKLIK!")
                    isSubmitting = true
                    successMessage = ""
                    errorMessage = ""
                    
                    try {
                        // Validasi input
                        if (selectedJamKe.isEmpty()) {
                            errorMessage = "Pilih Jam Ke terlebih dahulu"
                            isSubmitting = false
                            return@launch
                        }
                        
                        if (selectedGuruId == null || selectedGuruId!! <= 0) {
                            errorMessage = "Pilih guru yang baru terlebih dahulu"
                            isSubmitting = false
                            return@launch
                        }
                        
                        if (selectedMapelId == null || selectedMapelId!! <= 0) {
                            errorMessage = "Pilih mapel yang baru terlebih dahulu"
                            isSubmitting = false
                            return@launch
                        }
                        
                        // Cari jadwal yang dipilih
                        val matchedJadwal = jadwalList.find { 
                            it.jadwal?.jamKe == selectedJamKe && 
                            it.jadwal?.hari == selectedHari && 
                            it.jadwal?.kelasId == selectedKelasId
                        }
                        
                        println("DEBUG GANTI: JamKe=$selectedJamKe, Hari=$selectedHari, KelasId=$selectedKelasId")
                        println("DEBUG GANTI: Matched Jadwal ID=${matchedJadwal?.jadwal?.id}")
                        
                        if (matchedJadwal?.jadwal?.id == null) {
                            errorMessage = "Jadwal tidak ditemukan. Pastikan jadwal untuk Jam $selectedJamKe sudah ada."
                            isSubmitting = false
                            return@launch
                        }
                        
                        val tahunAjaranId = matchedJadwal.jadwal!!.tahunAjaranId ?: 1
                        if (tahunAjaranId <= 0) {
                            errorMessage = "Tahun ajaran tidak valid"
                            isSubmitting = false
                            return@launch
                        }
                        
                        // Update jadwal dengan guru dan mapel baru
                        // PENTING: Gunakan data dari matchedJadwal, jangan dari variable pilihan user!
                        val jadwalRequest = JadwalRequest(
                            guru_id = selectedGuruId!!,
                            mapel_id = selectedMapelId!!,
                            tahun_ajaran_id = tahunAjaranId,
                            kelas_id = matchedJadwal.jadwal!!.kelasId!!,
                            jam_ke = matchedJadwal.jadwal!!.jamKe!!,
                            hari = matchedJadwal.jadwal!!.hari!!
                        )
                        
                        println("DEBUG GANTI: Request = guru_id=$selectedGuruId, mapel_id=$selectedMapelId, tahun_ajaran_id=$tahunAjaranId")
                        
                        ApiClient.api.updateJadwal("Bearer $token", matchedJadwal.jadwal!!.id!!, jadwalRequest)
                        successMessage = "Guru dan Mapel berhasil diganti di jadwal!"
                        
                        // Reload jadwal list
                        val allData = ApiClient.api.getGuruMengajars("Bearer $token")
                        jadwalList = allData.filter { 
                            it.jadwal?.hari == selectedHari && it.jadwal?.kelasId == selectedKelasId
                        }.sortedBy { it.jadwal?.jamKe }
                        
                        // Update nama guru dan mapel di UI
                        guruName = guruMap[selectedGuruId] ?: ""
                        mapelName = mapelMap[selectedMapelId] ?: ""
                        
                    } catch (e: Exception) {
                        val errorDetail = when {
                            e.message?.contains("500") == true -> "Server Error - Periksa data yang dikirim"
                            e.message?.contains("404") == true -> "Endpoint tidak ditemukan"
                            e.message?.contains("422") == true -> "Validasi gagal - Data tidak sesuai"
                            else -> e.message ?: "Error tidak diketahui"
                        }
                        errorMessage = "Gagal mengganti guru/mapel: $errorDetail"
                        println("ERROR GANTI: ${e.javaClass.simpleName} - ${e.message}")
                        e.printStackTrace()
                    } finally {
                        isSubmitting = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722)),
            enabled = isGantiEnabled
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text("🔄 GANTI GURU/MAPEL", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KurikulumListPage(token: String) {
    var selectedHari by remember { mutableStateOf("Senin") }
    var selectedKelasId by remember { mutableStateOf<Int?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<GuruMengajarResponse?>(null) }
    
    var kelasList by remember { mutableStateOf<List<KelasResponse>>(emptyList()) }
    var guruMengajarList by remember { mutableStateOf<List<GuruMengajarResponse>>(emptyList()) }
    var guruMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var mapelMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val daftarHari = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")
    
    // Load kelas list
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                kelasList = ApiClient.api.getKelas("Bearer $token")
                if (kelasList.isNotEmpty()) {
                    selectedKelasId = kelasList[0].id
                }
            } catch (e: Exception) {
                errorMessage = "Error loading kelas: ${e.message}"
            }
        }
    }
    
    // Load guru mengajar list when filter changes
    LaunchedEffect(selectedHari, selectedKelasId) {
        if (selectedKelasId == null) return@LaunchedEffect
        
        scope.launch {
            isLoading = true
            errorMessage = ""
            successMessage = ""
            try {
                val allData = ApiClient.api.getGuruMengajars("Bearer $token")
                
                // Load lookup maps for guru and mapel
                val gurusData = try { ApiClient.api.getGurus("Bearer $token") } catch (e: Exception) { emptyList() }
                val mapelsData = try { ApiClient.api.getMapels("Bearer $token") } catch (e: Exception) { emptyList() }
                
                guruMap = gurusData.associate { g -> (g.id ?: 0) to (g.guru ?: "") }
                mapelMap = mapelsData.associate { m -> m.id to m.mapel }
                
                guruMengajarList = allData.filter { 
                    val hariMatch = it.hari == selectedHari
                    val kelasMatch = it.kelasId == selectedKelasId
                    hariMatch && kelasMatch
                }.sortedBy { it.jamKe }
            } catch (e: Exception) {
                errorMessage = "Error loading data: ${e.message}"
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
            text = "Daftar Ganti Guru",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Spinner Hari
        KurikulumDropdownSpinner(
            selectedValue = selectedHari,
            label = "Filter Hari",
            options = daftarHari,
            onValueChange = { selectedHari = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        // Spinner Kelas
        KurikulumDropdownSpinner(
            selectedValue = kelasList.find { it.id == selectedKelasId }?.kelas ?: "Pilih Kelas",
            label = "Filter Kelas",
            options = kelasList.map { it.kelas },
            onValueChange = { selectedKelas ->
                selectedKelasId = kelasList.find { it.kelas == selectedKelas }?.id
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        
        // Success/Error messages
        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Loading or List Cards
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (guruMengajarList.isEmpty()) {
                    Text(
                        text = "Tidak ada data",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    guruMengajarList.forEach { data ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = "Guru: ${data.namaGuru ?: data.jadwal?.guru?.guru ?: guruMap[data.jadwal?.guruId] ?: "-"}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Mapel: ${data.mapel ?: data.jadwal?.mapel?.mapel ?: mapelMap[data.jadwal?.mapelId] ?: "-"}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Status: ${data.status ?: "-"}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (data.status == "Masuk") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = "Keterangan: ${data.keterangan ?: "-"}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                
                                // Icon Edit dan Delete
                                Row {
                                    IconButton(
                                        onClick = {
                                            selectedItem = data
                                            showEditDialog = true
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(
                                        onClick = {
                                            selectedItem = data
                                            showDeleteDialog = true
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog Edit
    if (showEditDialog && selectedItem != null) {
        var editedStatus by remember { mutableStateOf(selectedItem?.status ?: "Masuk") }
        var editedKeterangan by remember { mutableStateOf(selectedItem?.keterangan ?: "") }
        var isSubmitting by remember { mutableStateOf(false) }
        
        val daftarStatus = listOf("Masuk", "Tidak Masuk")
        
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Data Kehadiran") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Guru: ${selectedItem?.namaGuru ?: "-"}\nMapel: ${selectedItem?.mapel ?: "-"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Spinner Status
                    KurikulumDropdownSpinner(
                        selectedValue = editedStatus,
                        label = "Status",
                        options = daftarStatus,
                        onValueChange = { editedStatus = it },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    
                    // Text Field Keterangan
                    OutlinedTextField(
                        value = editedKeterangan,
                        onValueChange = { editedKeterangan = it },
                        label = { Text("Keterangan") },
                        placeholder = { Text("Masukkan keterangan") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            isSubmitting = true
                            try {
                                selectedItem?.id?.let { id ->
                                    // Gunakan endpoint updateStatus yang baru
                                    val request = UpdateStatusRequest(
                                        status = editedStatus,
                                        keterangan = editedKeterangan.ifEmpty { null }
                                    )
                                    ApiClient.api.updateGuruMengajarStatus("Bearer $token", id, request)
                                    successMessage = "Data berhasil diupdate!"
                                    
                                    // Reload data
                                    val allData = ApiClient.api.getGuruMengajars("Bearer $token")
                                    guruMengajarList = allData.filter { 
                                        it.hari == selectedHari && it.kelasId == selectedKelasId
                                    }.sortedBy { it.jamKe }
                                }
                                showEditDialog = false
                            } catch (e: Exception) {
                                errorMessage = "Gagal mengupdate: ${e.message}"
                                showEditDialog = false
                            } finally {
                                isSubmitting = false
                            }
                        }
                    },
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Simpan")
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showEditDialog = false },
                    enabled = !isSubmitting
                ) {
                    Text("Batal")
                }
            }
        )
    }

    // Dialog Delete
    if (showDeleteDialog && selectedItem != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Data") },
            text = { 
                Column {
                    Text("Apakah Anda yakin ingin menghapus data ini?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Guru: ${selectedItem?.namaGuru ?: "-"}\nMapel: ${selectedItem?.mapel ?: "-"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                selectedItem?.id?.let { id ->
                                    ApiClient.api.deleteGuruMengajar("Bearer $token", id)
                                    successMessage = "Data berhasil dihapus!"
                                    // Reload data
                                    val allData = ApiClient.api.getGuruMengajars("Bearer $token")
                                    guruMengajarList = allData.filter { 
                                        it.hari == selectedHari && it.kelasId == selectedKelasId
                                    }.sortedBy { it.jamKe }
                                }
                                showDeleteDialog = false
                            } catch (e: Exception) {
                                errorMessage = "Gagal menghapus: ${e.message}"
                                showDeleteDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuruScreen(token: String) {
    var guruList by remember { mutableStateOf<List<Guru>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(Unit) {
        scope.launch {
            isLoading = true
            try {
                guruList = ApiClient.api.getGurus("Bearer $token")
                errorMessage = ""
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Daftar Guru") })
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage.isNotEmpty() -> {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(all = 16.dp)
                    ) {
                        items(guruList) { guru ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(all = 16.dp)) {
                                    Text(text = "Kode: ${guru.kodeGuru ?: "-"}", style = MaterialTheme.typography.bodyLarge)
                                    Text(text = "Nama: ${guru.guru ?: "-"}")
                                    Text(text = "Telepon: ${guru.telepon ?: "-"}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// === TAB KEHADIRAN GURU ===
@Composable
fun KurikulumKehadiranGuruPage(token: String) {
    var guruMengajarList by remember { mutableStateOf<List<GuruMengajarResponse>>(emptyList()) }
    var filteredList by remember { mutableStateOf<List<GuruMengajarResponse>>(emptyList()) }
    var kelasList by remember { mutableStateOf<List<KelasResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Filter states
    var selectedHari by remember { mutableStateOf("Senin") }
    var selectedKelasId by remember { mutableStateOf<Int?>(null) }
    var selectedFilter by remember { mutableStateOf("semua") } // semua, kelas_kosong, guru_masuk
    
    val scope = rememberCoroutineScope()
    val hariList = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
    val filterOptions = listOf(
        "semua" to "Semua Data",
        "kelas_kosong" to "Kelas Kosong",
        "guru_masuk" to "Guru Masuk"
    )
    
    // Load kelas
    LaunchedEffect(Unit) {
        try {
            kelasList = ApiClient.api.getKelas("Bearer $token")
        } catch (e: Exception) {
            Log.e("KurikulumKehadiran", "Error loading kelas", e)
        }
    }
    
    // Load guru mengajar data
    fun loadData() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = ApiClient.api.getGuruMengajar(
                    token = "Bearer $token",
                    hari = selectedHari,
                    kelasId = selectedKelasId
                )
                guruMengajarList = response.data
            } catch (e: Exception) {
                errorMessage = "Gagal memuat data: ${e.message}"
                Log.e("KurikulumKehadiran", "Error", e)
            } finally {
                isLoading = false
            }
        }
    }
    
    // Apply filter
    LaunchedEffect(guruMengajarList, selectedFilter) {
        filteredList = when (selectedFilter) {
            "kelas_kosong" -> {
                // Kelas kosong: status Tidak Masuk/Izin DAN belum ada guru pengganti
                guruMengajarList.filter { gm ->
                    (gm.status == "Tidak Masuk" || gm.status == "Izin") && gm.guruPengganti == null
                }
            }
            "guru_masuk" -> {
                // Guru masuk: status Masuk ATAU (status Tidak Masuk/Izin tapi sudah ada guru pengganti)
                guruMengajarList.filter { gm ->
                    gm.status == "Masuk" || 
                    ((gm.status == "Tidak Masuk" || gm.status == "Izin") && gm.guruPengganti != null)
                }
            }
            else -> guruMengajarList // semua
        }
    }
    
    LaunchedEffect(selectedHari, selectedKelasId) {
        loadData()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Kehadiran Guru",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Filter Hari
        KurikulumDropdownSpinner(
            selectedValue = selectedHari,
            label = "Hari",
            options = hariList,
            onValueChange = { selectedHari = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        
        // Filter Kelas
        KurikulumDropdownSpinner(
            selectedValue = kelasList.find { it.id == selectedKelasId }?.kelas ?: "Semua Kelas",
            label = "Kelas",
            options = listOf("Semua Kelas") + kelasList.map { it.kelas },
            onValueChange = { selected ->
                selectedKelasId = if (selected == "Semua Kelas") null else kelasList.find { it.kelas == selected }?.id
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        
        // Filter Status (Semua/Kelas Kosong/Guru Masuk)
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterOptions.forEach { (value, label) ->
                FilterChip(
                    selected = selectedFilter == value,
                    onClick = { selectedFilter = value },
                    label = { Text(label, fontSize = 12.sp) }
                )
            }
        }
        
        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val totalMasuk = guruMengajarList.count { it.status == "Masuk" || ((it.status == "Tidak Masuk" || it.status == "Izin") && it.guruPengganti != null) }
            val totalKosong = guruMengajarList.count { (it.status == "Tidak Masuk" || it.status == "Izin") && it.guruPengganti == null }
            
            StatCard(
                label = "Guru Masuk",
                value = totalMasuk.toString(),
                color = Color(0xFF4CAF50)
            )
            StatCard(
                label = "Kelas Kosong",
                value = totalKosong.toString(),
                color = Color(0xFFF44336)
            )
            StatCard(
                label = "Total",
                value = guruMengajarList.size.toString(),
                color = Color(0xFF2196F3)
            )
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
            filteredList.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tidak ada data kehadiran")
                    }
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredList) { gm ->
                        KehadiranGuruCard(gm)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color) {
    Card(
        modifier = Modifier.padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = color
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = color
            )
        }
    }
}

@Composable
fun KehadiranGuruCard(gm: GuruMengajarResponse) {
    val statusColor = when {
        gm.status == "Masuk" -> Color(0xFF4CAF50)
        (gm.status == "Tidak Masuk" || gm.status == "Izin") && gm.guruPengganti != null -> Color(0xFF2196F3)
        gm.status == "Izin" -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }
    
    val statusLabel = when {
        gm.status == "Masuk" -> "Masuk"
        (gm.status == "Tidak Masuk" || gm.status == "Izin") && gm.guruPengganti != null -> "Diganti"
        gm.status == "Izin" -> "Izin"
        else -> "Tidak Masuk"
    }
    
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = gm.jadwal?.guru?.guru ?: "-",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${gm.jadwal?.mapel?.mapel ?: "-"} • Jam ${gm.jadwal?.jamKe ?: "-"}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Kelas: ${gm.jadwal?.kelas?.kelas ?: "-"}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }
            
            // Tampilkan guru pengganti jika ada
            if (gm.guruPengganti != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF2196F3)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Diganti oleh: ${gm.guruPengganti?.guru ?: "-"}",
                        fontSize = 13.sp,
                        color = Color(0xFF2196F3),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Tampilkan durasi izin jika ada
            if (gm.status == "Izin" && gm.tanggalMulaiIzin != null && gm.tanggalSelesaiIzin != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFFF9800).copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFFFF9800)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Izin: ${gm.tanggalMulaiIzin} s/d ${gm.tanggalSelesaiIzin}",
                                fontSize = 12.sp,
                                color = Color(0xFFFF9800),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Durasi: ${gm.durasiIzin ?: 0} hari",
                                fontSize = 11.sp,
                                color = Color(0xFFFF9800)
                            )
                        }
                    }
                }
            }
            
            if (!gm.keterangan.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Keterangan: ${gm.keterangan}",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun KurikulumIzinCard(
    izin: GuruIzinResponse,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val statusColor = when (izin.status.lowercase()) {
        "pending" -> Color(0xFFFF9800)
        "disetujui" -> Color(0xFF4CAF50)
        "ditolak" -> Color(0xFFF44336)
        else -> Color.Gray
    }
    
    val jenisIzinLabel = when (izin.jenisIzin.lowercase()) {
        "sakit" -> "Sakit"
        "izin" -> "Izin"
        "cuti" -> "Cuti"
        "dinas_luar" -> "Dinas Luar"
        else -> izin.jenisIzin
    }
    
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = izin.guru?.guru ?: "Guru",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "$jenisIzinLabel - ${izin.tanggal}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = izin.status.replaceFirstChar { it.uppercase() },
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }
            
            if (!izin.keterangan.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = izin.keterangan,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
            
            if (izin.status.lowercase() == "pending") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF44336))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tolak")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Setujui")
                    }
                }
            }
        }
    }
}

// === TAB GURU PENGGANTI ===
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KurikulumGuruPenggantiPage(
    token: String,
    initialData: GuruMengajarResponse? = null,
    initialHari: String = "Senin",
    initialKelasId: Int? = null,
    onDataUsed: () -> Unit = {}
) {
    var penggantiList by remember { mutableStateOf<List<GuruPenggantiResponse>>(emptyList()) }
    var guruList by remember { mutableStateOf<List<Guru>>(emptyList()) }
    var jadwalList by remember { mutableStateOf<List<GuruMengajarResponse>>(emptyList()) }
    var kelasList by remember { mutableStateOf<List<KelasResponse>>(emptyList()) }
    
    var selectedHari by remember { mutableStateOf(initialHari) }
    var selectedKelasId by remember { mutableStateOf(initialKelasId) }
    var selectedKelasName by remember { mutableStateOf("X RPL") }
    var selectedGuruMengajarId by remember { mutableStateOf<Int?>(null) }  // ID dari guru_mengajars
    var selectedJadwalId by remember { mutableStateOf<Int?>(null) }
    var selectedJadwalLabel by remember { mutableStateOf("Pilih Jadwal") }
    var selectedGuruAsliId by remember { mutableStateOf<Int?>(null) }
    var selectedGuruPenggantiId by remember { mutableStateOf<Int?>(null) }
    var selectedGuruPenggantiName by remember { mutableStateOf("Pilih Guru Pengganti") }
    var keterangan by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var showForm by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val daftarHari = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")
    
    // Load data awal
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                guruList = ApiClient.api.getGurus("Bearer $token")
                kelasList = ApiClient.api.getKelas("Bearer $token")
                if (kelasList.isNotEmpty() && selectedKelasId == null) {
                    selectedKelasId = kelasList[0].id
                    selectedKelasName = kelasList[0].kelas
                }
                
                // Load daftar pengganti (yang sudah ada guru pengganti)
                val response = ApiClient.api.getKurikulumGuruPengganti("Bearer $token", null)
                penggantiList = response.data
            } catch (e: Exception) {
                Log.e("KurikulumGuruPengganti", "Error loading", e)
            }
        }
    }
    
    // Load jadwal berdasarkan hari dan kelas - HANYA yang status "Tidak Masuk" atau "Izin"
    LaunchedEffect(selectedHari, selectedKelasId) {
        if (selectedKelasId != null) {
            scope.launch {
                try {
                    val allData = ApiClient.api.getGuruMengajars("Bearer $token")
                    jadwalList = allData.filter { item ->
                        val hariMatch = (item.hari ?: item.jadwal?.hari) == selectedHari
                        val kelasMatch = (item.kelasId ?: item.jadwal?.kelasId) == selectedKelasId
                        // Hanya tampilkan guru yang TIDAK MASUK atau IZIN
                        val statusMatch = item.status?.lowercase() in listOf("tidak masuk", "izin")
                        hariMatch && kelasMatch && statusMatch
                    }.sortedBy { it.jamKe ?: it.jadwal?.jamKe }
                    
                    Log.d("KurikulumGuruPengganti", "Filtered jadwal (Tidak Masuk/Izin): ${jadwalList.size} items")
                } catch (e: Exception) {
                    Log.e("KurikulumGuruPengganti", "Error loading jadwal", e)
                }
            }
        }
    }
    
    // Apply initial data
    LaunchedEffect(initialData) {
        if (initialData != null) {
            selectedJadwalId = initialData.jadwalId ?: initialData.jadwal?.id
            selectedGuruAsliId = initialData.guruId ?: initialData.jadwal?.guruId
            selectedJadwalLabel = "Jam ${initialData.jamKe.ifEmpty { initialData.jadwal?.jamKe ?: "" }} - ${initialData.mapel.ifEmpty { initialData.jadwal?.mapel?.mapel ?: "Mapel" }}"
            showForm = true
            onDataUsed()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Guru Pengganti",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = { showForm = !showForm }) {
                Text(if (showForm) "Tutup Form" else "Tambah")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        successMessage?.let {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f))
            ) {
                Text(
                    text = it,
                    color = Color(0xFF4CAF50),
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
        
        if (showForm) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Atur Guru Pengganti",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Text(
                        text = "Pilih guru yang tidak masuk/izin lalu tentukan penggantinya",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Pilih Hari
                    KurikulumDropdownSpinner(
                        selectedValue = selectedHari,
                        label = "Hari",
                        options = daftarHari,
                        onValueChange = { selectedHari = it },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    
                    // Pilih Kelas
                    KurikulumDropdownSpinner(
                        selectedValue = selectedKelasName,
                        label = "Kelas",
                        options = kelasList.map { it.kelas },
                        onValueChange = { name ->
                            val kelas = kelasList.find { it.kelas == name }
                            if (kelas != null) {
                                selectedKelasId = kelas.id
                                selectedKelasName = kelas.kelas
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    
                    // Info jika tidak ada guru yang tidak masuk/izin
                    if (jadwalList.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                        ) {
                            Text(
                                text = "⚠️ Tidak ada guru yang tidak masuk/izin pada hari $selectedHari untuk kelas $selectedKelasName",
                                modifier = Modifier.padding(12.dp),
                                color = Color(0xFFE65100),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    
                    // Pilih Jadwal - Guru yang Tidak Masuk/Izin
                    KurikulumDropdownSpinner(
                        selectedValue = selectedJadwalLabel,
                        label = "Guru Tidak Masuk/Izin",
                        options = jadwalList.map { 
                            val jamKe = it.jamKe.ifEmpty { it.jadwal?.jamKe ?: "" }
                            val mapel = it.mapel.ifEmpty { it.jadwal?.mapel?.mapel ?: "Mapel" }
                            val guru = it.namaGuru.ifEmpty { it.jadwal?.guru?.guru ?: "Guru" }
                            val status = it.status ?: "Tidak Masuk"
                            "Jam $jamKe - $mapel ($guru) [$status]"
                        },
                        onValueChange = { label ->
                            selectedJadwalLabel = label
                            val index = jadwalList.indexOfFirst { 
                                val jamKe = it.jamKe.ifEmpty { it.jadwal?.jamKe ?: "" }
                                val mapel = it.mapel.ifEmpty { it.jadwal?.mapel?.mapel ?: "Mapel" }
                                val guru = it.namaGuru.ifEmpty { it.jadwal?.guru?.guru ?: "Guru" }
                                val status = it.status ?: "Tidak Masuk"
                                "Jam $jamKe - $mapel ($guru) [$status]" == label 
                            }
                            if (index >= 0) {
                                val jadwal = jadwalList[index]
                                selectedGuruMengajarId = jadwal.id  // ID guru_mengajar
                                selectedJadwalId = jadwal.jadwalId ?: jadwal.jadwal?.id
                                selectedGuruAsliId = jadwal.guruId ?: jadwal.jadwal?.guruId
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    
                    // Pilih Guru Pengganti - Exclude guru asli yang tidak masuk
                    val filteredGuruList = guruList.filter { it.id != selectedGuruAsliId }
                    
                    KurikulumDropdownSpinner(
                        selectedValue = selectedGuruPenggantiName,
                        label = "Guru Pengganti",
                        options = filteredGuruList.map { it.guru ?: "-" },
                        onValueChange = { name ->
                            selectedGuruPenggantiName = name
                            val guru = filteredGuruList.find { it.guru == name }
                            selectedGuruPenggantiId = guru?.id
                        },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )
                    
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
                                    val request = GuruPenggantiRequest(
                                        guruMengajarId = selectedGuruMengajarId ?: 0,
                                        guruPenggantiId = selectedGuruPenggantiId ?: 0
                                    )
                                    ApiClient.api.createGuruPengganti("Bearer $token", request)
                                    successMessage = "Guru pengganti berhasil ditambahkan!"
                                    showForm = false
                                    // Reset form
                                    selectedJadwalLabel = "Pilih Jadwal"
                                    selectedGuruPenggantiName = "Pilih Guru Pengganti"
                                    selectedGuruMengajarId = null
                                    keterangan = ""
                                    // Reload list
                                    val response = ApiClient.api.getKurikulumGuruPengganti("Bearer $token", null)
                                    penggantiList = response.data
                                } catch (e: Exception) {
                                    errorMessage = "Gagal menambahkan: ${e.message}"
                                    Log.e("KurikulumGuruPengganti", "Error", e)
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        enabled = !isLoading && selectedGuruMengajarId != null && selectedGuruPenggantiId != null,
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Simpan")
                        }
                    }
                }
            }
        }
        
        Text(
            text = "Daftar Guru Pengganti Hari Ini",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        when {
            penggantiList.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Belum ada guru pengganti hari ini")
                    }
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(penggantiList) { data ->
                        KurikulumPenggantiCard(data)
                    }
                }
            }
        }
    }
}

@Composable
fun KurikulumPenggantiCard(data: GuruPenggantiResponse) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = data.kelas ?: "-",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "${data.hari ?: "-"}, Jam ke-${data.jamKe ?: "-"}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            Text(
                text = data.mapel ?: "-",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Guru Asli",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = data.guruAsli?.nama ?: "-",
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "(${data.guruAsli?.kode ?: "-"})",
                        fontSize = 11.sp,
                        color = Color.Gray
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
                        text = data.guruPengganti?.nama ?: "-",
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4CAF50)
                    )
                    Text(
                        text = "(${data.guruPengganti?.kode ?: "-"})",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
            
            if (!data.keterangan.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Keterangan: ${data.keterangan}",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KurikulumScreenPreview() {
    AplikasiMonitoringKelasTheme {
        KurikulumScreen(token = "")
    }
}
