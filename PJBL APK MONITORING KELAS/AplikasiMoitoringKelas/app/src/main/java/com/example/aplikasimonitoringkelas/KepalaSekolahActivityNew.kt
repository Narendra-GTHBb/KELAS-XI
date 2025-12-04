package com.example.aplikasimonitoringkelas

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadwalPageSimple(token: String = "") {
    var selectedHari by remember { mutableStateOf("Senin") }
    var selectedKelasId by remember { mutableStateOf(1) }
    var selectedKelasIndex by remember { mutableStateOf(0) }
    
    var jadwalList by remember { mutableStateOf<List<GuruMengajarResponse>>(emptyList()) }
    var guruMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var kodeGuruMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var mapelMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val daftarHari = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")
    val daftarKelasNama = listOf("X RPL", "XI RPL", "XII RPL")
    val daftarKelasId = listOf(1, 2, 3)
    
    // Load guru dan mapel lookup saat pertama kali
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val gurus = ApiClient.api.getGurus("Bearer $token")
                guruMap = gurus.associate { (it.id ?: 0) to (it.guru ?: "") }
                kodeGuruMap = gurus.associate { (it.id ?: 0) to (it.kodeGuru ?: "") }
                
                val mapels = ApiClient.api.getMapels("Bearer $token")
                mapelMap = mapels.associate { (it.id ?: 0) to (it.mapel ?: "") }
                
                Log.d("JadwalPage", "Loaded ${guruMap.size} gurus, ${mapelMap.size} mapels")
            } catch (e: Exception) {
                Log.e("JadwalPage", "Error loading lookup data", e)
            }
        }
    }
    
    // Load jadwal saat hari atau kelas berubah
    LaunchedEffect(selectedHari, selectedKelasId) {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val allData = ApiClient.api.getGuruMengajars("Bearer $token")
                jadwalList = allData.filter { item ->
                    val hariMatch = (item.hari ?: item.jadwal?.hari) == selectedHari
                    val kelasMatch = (item.kelasId ?: item.jadwal?.kelasId) == selectedKelasId
                    val statusMatch = item.status?.lowercase() == "masuk"
                    hariMatch && kelasMatch && statusMatch
                }.sortedBy { it.jamKe ?: it.jadwal?.jamKe }
                Log.d("JadwalPage", "Loaded ${jadwalList.size} jadwal items")
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
                Log.e("JadwalPage", "Error loading jadwal", e)
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
            text = "Login sebagai: Kepala Sekolah",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Dropdown Hari
        KepalaSekolahDropdownSpinner(
            selectedValue = selectedHari,
            label = "Pilih Hari",
            options = daftarHari,
            onValueChange = { selectedHari = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
        
        // Dropdown Kelas
        KepalaSekolahDropdownSpinner(
            selectedValue = daftarKelasNama[selectedKelasIndex],
            label = "Pilih Kelas",
            options = daftarKelasNama,
            onValueChange = { 
                selectedKelasIndex = daftarKelasNama.indexOf(it)
                selectedKelasId = daftarKelasId[selectedKelasIndex]
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )
        
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
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            jadwalList.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tidak ada guru yang masuk untuk ${daftarKelasNama[selectedKelasIndex]} - $selectedHari")
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(jadwalList) { data ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                // Jam Ke
                                val jamKe = data.jamKe ?: data.jadwal?.jamKe ?: "-"
                                Text(
                                    text = if (jamKe.startsWith("Jam Ke ") || jamKe.startsWith("Jam ke ")) jamKe else "Jam ke $jamKe",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                // Mapel
                                val mapelName = data.mapel 
                                    ?: data.jadwal?.mapel?.mapel 
                                    ?: mapelMap[data.jadwal?.mapelId ?: 0] 
                                    ?: "-"
                                Text(
                                    text = "Mata Pelajaran: $mapelName",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontSize = 15.sp
                                )
                                
                                // Kode Guru
                                val kodeGuru = data.kodeGuru 
                                    ?: data.jadwal?.guru?.kodeGuru 
                                    ?: kodeGuruMap[data.jadwal?.guruId ?: 0]
                                if (!kodeGuru.isNullOrEmpty()) {
                                    Text(
                                        text = "Kode Guru: $kodeGuru",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                                
                                // Nama Guru
                                val guruName = data.namaGuru 
                                    ?: data.jadwal?.guru?.guru 
                                    ?: guruMap[data.jadwal?.guruId ?: 0] 
                                    ?: "-"
                                Text(
                                    text = "Guru: $guruName",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontSize = 15.sp
                                )
                                
                                Spacer(modifier = Modifier.height(4.dp))
                                
                                // Status
                                Text(
                                    text = "Status: ${data.status ?: "-"}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    color = Color(0xFF2E7D32),
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
}
