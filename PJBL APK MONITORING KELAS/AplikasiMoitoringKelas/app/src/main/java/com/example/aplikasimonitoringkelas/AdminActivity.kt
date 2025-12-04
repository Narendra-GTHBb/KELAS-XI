package com.example.aplikasimonitoringkelas

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val sessionManager = SessionManager(this)
        val token = sessionManager.getAuthToken() ?: ""
        setContent {
            AplikasiMonitoringKelasTheme {
                AdminScreen(
                    token = token,
                    onLogout = {
                        sessionManager.clearSession()
                        finish()
                        startActivity(Intent(this@AdminActivity, MainActivity::class.java))
                    }
                )
            }
        }
    }
}

sealed class AdminNavigationItem(val route: String, val title: String, val icon: ImageVector) {
    object EntriUser : AdminNavigationItem("entri_user", "Entri User", Icons.Default.Add)
    object EntriJadwal : AdminNavigationItem("entri_jadwal", "Entri Jadwal", Icons.Default.Edit)
    object List : AdminNavigationItem("list", "List", Icons.Default.List)
}

// Reusable Dropdown Component for AdminActivity
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDropdownSpinner(
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
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
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

data class UserData(val nama: String, val email: String, val role: String)

@Composable
fun AdminScreen(token: String, onLogout: () -> Unit = {}) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf(
        AdminNavigationItem.EntriUser,
        AdminNavigationItem.EntriJadwal,
        AdminNavigationItem.List
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
                0 -> EntriUserPage(token = token, onLogout = onLogout)
                1 -> EntriJadwalPage(token = token)
                2 -> AdminListPage(token = token)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntriUserPage(token: String, onLogout: () -> Unit = {}) {
    var selectedRole by remember { mutableStateOf("Siswa") }
    var nama by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var expandedRole by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var userList by remember { mutableStateOf(listOf<UserData>()) }

    val daftarRole = listOf("Siswa", "Kurikulum", "Kepala Sekolah", "Admin")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with Logout Button
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Login sebagai: Admin",
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
        
        // Spinner Role
        ExposedDropdownMenuBox(
            expanded = expandedRole,
            onExpandedChange = { expandedRole = !expandedRole },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            OutlinedTextField(
                value = selectedRole,
                onValueChange = {},
                readOnly = true,
                label = { Text("Pilih Role") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRole)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedRole,
                onDismissRequest = { expandedRole = false }
            ) {
                daftarRole.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role) },
                        onClick = {
                            selectedRole = role
                            expandedRole = false
                        }
                    )
                }
            }
        }

        // Nama TextField
        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it },
            label = { Text("Nama") },
            placeholder = { Text("Masukkan nama") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        // Email TextField
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text("Masukkan email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        // Password TextField with Show/Hide
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("Masukkan password") },
            visualTransformation = if (showPassword) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(if (showPassword) "Hide" else "Show")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Simpan Button
        Button(
            onClick = {
                if (nama.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    userList = userList + UserData(nama, email, selectedRole)
                    nama = ""
                    email = ""
                    password = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = nama.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
        ) {
            Text("Simpan User")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User List Cards (Scrollable)
        if (userList.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                userList.forEach { user ->
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
                            Text(
                                text = "Nama: ${user.nama}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Email: ${user.email}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Role: ${user.role}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

data class JadwalData(
    val hari: String,
    val kelas: String,
    val mataPelajaran: String,
    val guru: String,
    val tahunAjaran: String,
    val jamKe: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntriJadwalPage(token: String) {
    var selectedHari by remember { mutableStateOf("Senin") }
    var selectedKelas by remember { mutableStateOf("X RPL") }
    var selectedGuru by remember { mutableStateOf("Siti") }
    var selectedMataPelajaran by remember { mutableStateOf("IPA") }
    var selectedTahunAjaran by remember { mutableStateOf("2023/2024") }
    var jamKe by remember { mutableStateOf("") }

    var expandedHari by remember { mutableStateOf(false) }
    var expandedKelas by remember { mutableStateOf(false) }
    var expandedGuru by remember { mutableStateOf(false) }
    var expandedMataPelajaran by remember { mutableStateOf(false) }
    var expandedTahunAjaran by remember { mutableStateOf(false) }

    var jadwalList by remember { mutableStateOf(listOf<JadwalData>()) }

    val daftarHari = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")
    val daftarKelas = listOf("X RPL", "XI RPL", "XII RPL")
    val daftarMataPelajaran = listOf("IPA", "IPS", "Bahasa")
    val daftarGuru = listOf("Siti", "Budi", "Adi", "Agus")
    val daftarTahunAjaran = listOf("2023/2024", "2024/2025", "2025/2026")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Spinner Hari
        ExposedDropdownMenuBox(
            expanded = expandedHari,
            onExpandedChange = { expandedHari = !expandedHari },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            OutlinedTextField(
                value = selectedHari,
                onValueChange = {},
                readOnly = true,
                label = { Text("Pilih Hari") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedHari) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedHari,
                onDismissRequest = { expandedHari = false }
            ) {
                daftarHari.forEach { hari ->
                    DropdownMenuItem(
                        text = { Text(hari) },
                        onClick = { selectedHari = hari; expandedHari = false }
                    )
                }
            }
        }

        // Spinner Kelas
        ExposedDropdownMenuBox(
            expanded = expandedKelas,
            onExpandedChange = { expandedKelas = !expandedKelas },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            OutlinedTextField(
                value = selectedKelas,
                onValueChange = {},
                readOnly = true,
                label = { Text("Pilih Kelas") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedKelas) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedKelas,
                onDismissRequest = { expandedKelas = false }
            ) {
                daftarKelas.forEach { kelas ->
                    DropdownMenuItem(
                        text = { Text(kelas) },
                        onClick = { selectedKelas = kelas; expandedKelas = false }
                    )
                }
            }
        }

        // Spinner Mata Pelajaran
        ExposedDropdownMenuBox(
            expanded = expandedMataPelajaran,
            onExpandedChange = { expandedMataPelajaran = !expandedMataPelajaran },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            OutlinedTextField(
                value = selectedMataPelajaran,
                onValueChange = {},
                readOnly = true,
                label = { Text("Pilih Mata Pelajaran") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMataPelajaran) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedMataPelajaran,
                onDismissRequest = { expandedMataPelajaran = false }
            ) {
                daftarMataPelajaran.forEach { mapel ->
                    DropdownMenuItem(
                        text = { Text(mapel) },
                        onClick = { selectedMataPelajaran = mapel; expandedMataPelajaran = false }
                    )
                }
            }
        }

        // Spinner Guru
        ExposedDropdownMenuBox(
            expanded = expandedGuru,
            onExpandedChange = { expandedGuru = !expandedGuru },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            OutlinedTextField(
                value = selectedGuru,
                onValueChange = {},
                readOnly = true,
                label = { Text("Pilih Guru") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGuru) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedGuru,
                onDismissRequest = { expandedGuru = false }
            ) {
                daftarGuru.forEach { guru ->
                    DropdownMenuItem(
                        text = { Text(guru) },
                        onClick = { selectedGuru = guru; expandedGuru = false }
                    )
                }
            }
        }

        // Spinner Tahun Ajaran
        ExposedDropdownMenuBox(
            expanded = expandedTahunAjaran,
            onExpandedChange = { expandedTahunAjaran = !expandedTahunAjaran },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            OutlinedTextField(
                value = selectedTahunAjaran,
                onValueChange = {},
                readOnly = true,
                label = { Text("Pilih Tahun Ajaran") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTahunAjaran) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedTahunAjaran,
                onDismissRequest = { expandedTahunAjaran = false }
            ) {
                daftarTahunAjaran.forEach { tahun ->
                    DropdownMenuItem(
                        text = { Text(tahun) },
                        onClick = { selectedTahunAjaran = tahun; expandedTahunAjaran = false }
                    )
                }
            }
        }

        // Text Field Jam Ke
        OutlinedTextField(
            value = jamKe,
            onValueChange = { jamKe = it },
            label = { Text("Jam Ke") },
            placeholder = { Text("Contoh: 1-3") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // Simpan Button
        Button(
            onClick = {
                if (jamKe.isNotEmpty()) {
                    jadwalList = jadwalList + JadwalData(
                        selectedHari, selectedKelas, selectedMataPelajaran,
                        selectedGuru, selectedTahunAjaran, jamKe
                    )
                    jamKe = ""
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = jamKe.isNotEmpty()
        ) {
            Text("Simpan Jadwal")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Jadwal List Cards (Scrollable)
        if (jadwalList.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                jadwalList.forEach { jadwal ->
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
                            Text("Hari: ${jadwal.hari}", style = MaterialTheme.typography.titleMedium)
                            Text("Kelas: ${jadwal.kelas}", style = MaterialTheme.typography.bodyMedium)
                            Text("Mata Pelajaran: ${jadwal.mataPelajaran}", style = MaterialTheme.typography.bodyMedium)
                            Text("Guru: ${jadwal.guru}", style = MaterialTheme.typography.bodyMedium)
                            Text("Tahun Ajaran: ${jadwal.tahunAjaran}", style = MaterialTheme.typography.bodySmall)
                            Text("Jam Ke: ${jadwal.jamKe}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

data class AdminListData(
    val guru: String,
    val mataPelajaran: String,
    val tahunAjaran: String,
    val jamKe: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminListPage(token: String) {
    var selectedHari by remember { mutableStateOf("Senin") }
    var selectedKelas by remember { mutableStateOf("X RPL") }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<AdminListData?>(null) }

    val daftarHari = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat")
    val daftarKelas = listOf("X RPL", "XI RPL", "XII RPL")

    // Data dummy untuk list
    var adminListData by remember {
        mutableStateOf(
            listOf(
                AdminListData("Pak Budi", "Matematika", "2023/2024", "1-2"),
                AdminListData("Bu Siti", "Bahasa Indonesia", "2023/2024", "3-4"),
                AdminListData("Pak Andi", "Pemrograman", "2024/2025", "5-6"),
                AdminListData("Bu Dewi", "Basis Data", "2024/2025", "7-8")
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Daftar Jadwal",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Spinner Hari
        AdminDropdownSpinner(
            selectedValue = selectedHari,
            label = "Pilih Hari",
            options = daftarHari,
            onValueChange = { selectedHari = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        // Spinner Kelas
        AdminDropdownSpinner(
            selectedValue = selectedKelas,
            label = "Pilih Kelas",
            options = daftarKelas,
            onValueChange = { selectedKelas = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // List Cards yang bisa di scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            adminListData.forEach { data ->
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Nama Guru: ${data.guru}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Mapel: ${data.mataPelajaran}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Tahun Ajaran: ${data.tahunAjaran}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Jam Ke: ${data.jamKe}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        Row {
                            IconButton(onClick = {
                                selectedItem = data
                                showEditDialog = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = {
                                selectedItem = data
                                showDeleteDialog = true
                            }) {
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

    // Edit Dialog
    if (showEditDialog && selectedItem != null) {
        EditAdminListDialog(
            data = selectedItem!!,
            onDismiss = { showEditDialog = false },
            onSave = { updatedData ->
                adminListData = adminListData.map {
                    if (it == selectedItem) updatedData else it
                }
                showEditDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && selectedItem != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda yakin ingin menghapus jadwal ${selectedItem!!.mataPelajaran}?") },
            confirmButton = {
                Button(
                    onClick = {
                        adminListData = adminListData.filter { it != selectedItem }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAdminListDialog(
    data: AdminListData,
    onDismiss: () -> Unit,
    onSave: (AdminListData) -> Unit
) {
    var guru by remember { mutableStateOf(data.guru) }
    var mataPelajaran by remember { mutableStateOf(data.mataPelajaran) }
    var tahunAjaran by remember { mutableStateOf(data.tahunAjaran) }
    var jamKe by remember { mutableStateOf(data.jamKe) }

    val daftarGuru = listOf("Pak Budi", "Bu Siti", "Pak Andi", "Bu Dewi", "Pak Agus")
    val daftarMapel = listOf("Matematika", "Bahasa Indonesia", "Pemrograman", "Basis Data", "IPA", "IPS")
    val daftarTahunAjaran = listOf("2023/2024", "2024/2025", "2025/2026")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Jadwal") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Spinner Guru
                AdminDropdownSpinner(
                    selectedValue = guru,
                    label = "Pilih Guru",
                    options = daftarGuru,
                    onValueChange = { guru = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )

                // Spinner Mapel
                AdminDropdownSpinner(
                    selectedValue = mataPelajaran,
                    label = "Pilih Mapel",
                    options = daftarMapel,
                    onValueChange = { mataPelajaran = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )

                // Spinner Tahun Ajaran
                AdminDropdownSpinner(
                    selectedValue = tahunAjaran,
                    label = "Pilih Tahun Ajaran",
                    options = daftarTahunAjaran,
                    onValueChange = { tahunAjaran = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                )

                // Text Field Jam Ke
                OutlinedTextField(
                    value = jamKe,
                    onValueChange = { jamKe = it },
                    label = { Text("Jam Ke") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(AdminListData(guru, mataPelajaran, tahunAjaran, jamKe))
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}