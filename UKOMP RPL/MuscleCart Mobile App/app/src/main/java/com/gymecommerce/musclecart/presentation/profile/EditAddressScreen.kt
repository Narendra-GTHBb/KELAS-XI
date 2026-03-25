package com.gymecommerce.musclecart.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAddressScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showProvinceDropdown by remember { mutableStateOf(false) }
    var showCityDropdown by remember { mutableStateOf(false) }

    // Navigate back on success
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            snackbarHostState.showSnackbar("Alamat berhasil disimpan!")
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Alamat Pengiriman", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            "Atur Alamat Tetap",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            "Alamat ini akan digunakan otomatis saat checkout",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Province dropdown
            ExposedDropdownMenuBox(
                expanded = showProvinceDropdown,
                onExpandedChange = { showProvinceDropdown = it && uiState.provinces.isNotEmpty() }
            ) {
                OutlinedTextField(
                    value = uiState.selectedProvince?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Provinsi *") },
                    trailingIcon = {
                        if (uiState.isLoadingProvinces)
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        else
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    enabled = !uiState.isSaving
                )
                ExposedDropdownMenu(
                    expanded = showProvinceDropdown,
                    onDismissRequest = { showProvinceDropdown = false }
                ) {
                    uiState.provinces.forEach { province ->
                        DropdownMenuItem(
                            text = { Text(province.name) },
                            onClick = {
                                viewModel.onProvinceSelected(province)
                                showProvinceDropdown = false
                            }
                        )
                    }
                }
            }

            // City dropdown (shown only after province selected)
            if (uiState.selectedProvince != null) {
                ExposedDropdownMenuBox(
                    expanded = showCityDropdown,
                    onExpandedChange = { showCityDropdown = it && uiState.cities.isNotEmpty() }
                ) {
                    OutlinedTextField(
                        value = uiState.selectedCity?.displayName ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kota / Kabupaten *") },
                        trailingIcon = {
                            if (uiState.isLoadingCities)
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            else
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        enabled = !uiState.isSaving
                    )
                    ExposedDropdownMenu(
                        expanded = showCityDropdown,
                        onDismissRequest = { showCityDropdown = false }
                    ) {
                        uiState.cities.forEach { city ->
                            DropdownMenuItem(
                                text = { Text(city.displayName) },
                                onClick = {
                                    viewModel.onCitySelected(city)
                                    showCityDropdown = false
                                }
                            )
                        }
                    }
                }

                // Postal code â€” auto-filled from city but user can edit
                OutlinedTextField(
                    value = uiState.postalCode,
                    onValueChange = { viewModel.onPostalCodeChange(it) },
                    label = { Text("Kode Pos") },
                    placeholder = { Text("Contoh: 60111") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    enabled = !uiState.isSaving,
                    supportingText = {
                        Text(
                            "Terisi otomatis dari kota yang dipilih, bisa diedit manual",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }

            // Street detail field
            OutlinedTextField(
                value = uiState.streetDetail,
                onValueChange = { viewModel.onStreetDetailChange(it) },
                label = { Text("Jalan / No. Rumah / RT-RW / Detail *") },
                placeholder = {
                    Text(
                        "Cth: Jl. Merdeka No.12, RT 02/RW 05, Kec. Giri",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                isError = uiState.streetDetail.isNotEmpty() && uiState.streetDetail.length < 5,
                supportingText = {
                    if (uiState.streetDetail.isNotEmpty() && uiState.streetDetail.length < 5)
                        Text("Minimal 5 karakter", color = MaterialTheme.colorScheme.error)
                },
                enabled = !uiState.isSaving
            )

            // Address consistency warning
            if (uiState.addressWarning != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF8E1) // amber 50
                    ),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFF8F00))
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFFF8F00),
                            modifier = Modifier.size(20.dp).padding(top = 1.dp)
                        )
                        Column {
                            Text(
                                "Peringatan Alamat",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Color(0xFF5F4200)
                            )
                            Text(
                                uiState.addressWarning!!,
                                fontSize = 12.sp,
                                color = Color(0xFF7A5500)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Save button
            Button(
                onClick = { viewModel.saveAddress() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !uiState.isSaving &&
                        uiState.streetDetail.length >= 5 &&
                        uiState.selectedCity != null
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Simpan Alamat", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            OutlinedButton(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !uiState.isSaving
            ) {
                Text("Batal", fontSize = 16.sp)
            }
        }
    }
}
