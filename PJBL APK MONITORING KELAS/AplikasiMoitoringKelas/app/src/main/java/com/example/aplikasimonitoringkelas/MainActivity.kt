    package com.example.aplikasimonitoringkelas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.aplikasimonitoringkelas.ui.theme.AplikasiMonitoringKelasTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Check if already logged in
        val sessionManager = SessionManager(this)
        if (sessionManager.isLoggedIn()) {
            val token = sessionManager.getAuthToken() ?: ""
            val role = sessionManager.getUserRole()
            Log.d("MainActivity", "Auto-login: role=$role, token=${token.take(20)}...")
            val intent = when (role) {
                "Guru" -> Intent(this, GuruActivity::class.java)
                "Siswa" -> Intent(this, SiswaActivity::class.java)
                "Kurikulum" -> Intent(this, KurikulumActivity::class.java)
                "Kepala Sekolah" -> Intent(this, KepalaSekolahActivity::class.java)
                "Admin" -> Intent(this, AdminActivity::class.java)
                else -> null
            }
            intent?.let {
                startActivity(it)
                finish()
                return
            }
        } else {
            Log.d("MainActivity", "No active session, showing login")
        }
        
        setContent {
            AplikasiMonitoringKelasTheme {
                LoginScreen(activity = this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(activity: ComponentActivity? = null) {
    var selectedRole by remember { mutableStateOf("Siswa") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val context = activity ?: LocalContext.current
    val scope = rememberCoroutineScope()
    val roles = listOf("Guru", "Siswa", "Kurikulum", "Kepala Sekolah", "Admin")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo Sekolah
        Image(
            painter = painterResource(id = R.drawable.logo_smkn2buduran),
            contentDescription = "Logo SMKN 2 Buduran",
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 32.dp)
        )

        // Role Spinner
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = selectedRole,
                onValueChange = {},
                readOnly = true,
                label = { Text("Pilih Role") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                roles.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role) },
                        onClick = {
                            selectedRole = role
                            expanded = false
                        }
                    )
                }
            }
        }

        // Email TextField -> Username TextField
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            placeholder = { Text("Contoh: siswa") },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Password TextField
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            placeholder = { Text("Masukkan password Anda") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        // Error message
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Login Button
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    errorMessage = ""
                    
                    try {
                        val loginRequest = LoginRequest(
                            username = username,
                            password = password,
                            role = selectedRole
                        )
                        
                        val response = ApiClient.api.login(loginRequest)
                        
                        if (response.success && response.data != null) {
                            // Save session
                            val sessionManager = SessionManager(context)
                            sessionManager.saveAuthToken(response.data.token)
                            sessionManager.saveUserData(
                                id = response.data.user.id,
                                name = response.data.user.name,
                                username = response.data.user.username,
                                role = response.data.user.role,
                                kelasId = response.data.user.kelasId,
                                kelasName = response.data.user.kelas
                            )
                            
                            // Navigate to appropriate activity
                            val intent = when (selectedRole) {
                                "Guru" -> Intent(context, GuruActivity::class.java)
                                "Siswa" -> Intent(context, SiswaActivity::class.java)
                                "Kurikulum" -> Intent(context, KurikulumActivity::class.java)
                                "Kepala Sekolah" -> Intent(context, KepalaSekolahActivity::class.java)
                                "Admin" -> Intent(context, AdminActivity::class.java)
                                else -> Intent(context, SiswaActivity::class.java)
                            }
                            context.startActivity(intent)
                            activity?.finish()
                        } else {
                            errorMessage = response.message
                        }
                    } catch (e: retrofit2.HttpException) {
                        errorMessage = when (e.code()) {
                            401 -> "Username, password, atau role tidak sesuai"
                            404 -> "Server tidak ditemukan"
                            500 -> "Server error"
                            else -> "Login gagal: ${e.message()}"
                        }
                        Log.e("MainActivity", "HTTP Error: ${e.code()}", e)
                    } catch (e: java.net.SocketTimeoutException) {
                        errorMessage = "Timeout: Server tidak merespons. Pastikan Laravel server berjalan dengan: php artisan serve --host=0.0.0.0 --port=8000"
                        Log.e("MainActivity", "Timeout error", e)
                    } catch (e: java.net.ConnectException) {
                        errorMessage = "Tidak dapat terhubung ke server. Pastikan Laravel server berjalan di http://10.0.2.2:8000"
                        Log.e("MainActivity", "Connection error", e)
                    } catch (e: java.io.IOException) {
                        if (e.message?.contains("timeout", ignoreCase = true) == true) {
                            errorMessage = "Timeout: Server tidak merespons. Cek koneksi dan pastikan server berjalan."
                        } else {
                            errorMessage = "Error koneksi: ${e.message}"
                        }
                        Log.e("MainActivity", "IO Error", e)
                    } catch (e: Exception) {
                        errorMessage = when {
                            e.message?.contains("timeout", ignoreCase = true) == true -> 
                                "Timeout: Server tidak merespons. Pastikan Laravel server berjalan."
                            e.message?.contains("connect", ignoreCase = true) == true -> 
                                "Tidak dapat terhubung ke server. Pastikan server berjalan di http://10.0.2.2:8000"
                            else -> "Error: ${e.message}"
                        }
                        Log.e("MainActivity", "General error", e)
                    } finally {
                        isLoading = false
                    }
                }
            },
            enabled = !isLoading && username.isNotEmpty() && password.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text("Login", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AplikasiMonitoringKelasTheme {
        LoginScreen()
    }
}