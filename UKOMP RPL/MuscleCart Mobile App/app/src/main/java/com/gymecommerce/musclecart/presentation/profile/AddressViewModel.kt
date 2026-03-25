package com.gymecommerce.musclecart.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gymecommerce.musclecart.domain.model.City
import com.gymecommerce.musclecart.domain.model.NetworkResult
import com.gymecommerce.musclecart.domain.model.Province
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.repository.AuthRepository
import com.gymecommerce.musclecart.domain.repository.ShippingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddressUiState(
    val streetDetail: String = "",
    val postalCode: String = "",
    val provinces: List<Province> = emptyList(),
    val cities: List<City> = emptyList(),
    val selectedProvince: Province? = null,
    val selectedCity: City? = null,
    val isLoadingProvinces: Boolean = false,
    val isLoadingCities: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null,
    val addressWarning: String? = null
)

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val shippingRepository: ShippingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddressUiState())
    val uiState: StateFlow<AddressUiState> = _uiState.asStateFlow()

    // Map well-known city/area keywords → their province (RajaOngkir names)
    private val CITY_PROVINCE_MAP = mapOf(
        // DKI Jakarta
        "jakarta" to "DKI Jakarta", "menteng" to "DKI Jakarta",
        "kemayoran" to "DKI Jakarta", "gambir" to "DKI Jakarta",
        // Jawa Barat
        "bandung" to "Jawa Barat", "bekasi" to "Jawa Barat",
        "depok" to "Jawa Barat", "bogor" to "Jawa Barat",
        "cirebon" to "Jawa Barat", "garut" to "Jawa Barat",
        "sukabumi" to "Jawa Barat", "karawang" to "Jawa Barat",
        // Banten
        "tangerang" to "Banten", "serang" to "Banten", "cilegon" to "Banten",
        // Jawa Tengah
        "semarang" to "Jawa Tengah", "solo" to "Jawa Tengah",
        "surakarta" to "Jawa Tengah", "pekalongan" to "Jawa Tengah",
        "tegal" to "Jawa Tengah", "purwokerto" to "Jawa Tengah",
        // DI Yogyakarta
        "yogyakarta" to "DI Yogyakarta", "jogja" to "DI Yogyakarta",
        "sleman" to "DI Yogyakarta", "bantul" to "DI Yogyakarta",
        // Jawa Timur
        "surabaya" to "Jawa Timur", "malang" to "Jawa Timur",
        "sidoarjo" to "Jawa Timur", "gresik" to "Jawa Timur",
        "pasuruan" to "Jawa Timur", "blitar" to "Jawa Timur",
        "kediri" to "Jawa Timur", "jember" to "Jawa Timur",
        "banyuwangi" to "Jawa Timur", "mojokerto" to "Jawa Timur",
        "probolinggo" to "Jawa Timur", "madiun" to "Jawa Timur",
        // Bali
        "denpasar" to "Bali", "kuta" to "Bali", "ubud" to "Bali",
        "seminyak" to "Bali", "gianyar" to "Bali",
        // Sumatera Utara
        "medan" to "Sumatera Utara", "binjai" to "Sumatera Utara",
        "deli serdang" to "Sumatera Utara",
        // Sumatera Barat
        "padang" to "Sumatera Barat", "bukittinggi" to "Sumatera Barat",
        // Riau
        "pekanbaru" to "Riau", "dumai" to "Riau",
        // Kepulauan Riau
        "batam" to "Kepulauan Riau", "tanjungpinang" to "Kepulauan Riau",
        // Sumatera Selatan
        "palembang" to "Sumatera Selatan",
        // Lampung
        "bandar lampung" to "Lampung",
        // Kalimantan Timur
        "balikpapan" to "Kalimantan Timur", "samarinda" to "Kalimantan Timur",
        // Kalimantan Barat
        "pontianak" to "Kalimantan Barat",
        // Kalimantan Selatan
        "banjarmasin" to "Kalimantan Selatan",
        // Sulawesi Selatan
        "makassar" to "Sulawesi Selatan",
        // Sulawesi Utara
        "manado" to "Sulawesi Utara",
        // Papua
        "jayapura" to "Papua",
        // Nusa Tenggara Barat
        "mataram" to "Nusa Tenggara Barat",
        // Nusa Tenggara Timur
        "kupang" to "Nusa Tenggara Timur",
        // Maluku
        "ambon" to "Maluku",
        // Aceh
        "banda aceh" to "Aceh",
        // Gorontalo
        "gorontalo" to "Gorontalo"
    )

    init {
        loadCurrentAddress()
    }

    private fun loadCurrentAddress() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            // Pre-fill street + postal code from saved user
            _uiState.value = _uiState.value.copy(
                streetDetail = user?.address ?: "",
                postalCode = user?.postalCode ?: ""
            )

            if (!user?.provinceId.isNullOrBlank()) {
                // Load provinces AND pre-select the saved one
                _uiState.value = _uiState.value.copy(isLoadingProvinces = true)
                val provinceResult = shippingRepository.getProvinces()
                if (provinceResult is Result.Success) {
                    val province = provinceResult.data.find { it.id == user!!.provinceId }
                    _uiState.value = _uiState.value.copy(
                        provinces = provinceResult.data,
                        selectedProvince = province,
                        isLoadingProvinces = false
                    )
                    if (province != null) {
                        loadCities(province.id, preSelectCityId = user!!.cityId)
                    }
                } else {
                    _uiState.value = _uiState.value.copy(isLoadingProvinces = false)
                    loadProvinces() // Retry with standard loader
                }
            } else {
                loadProvinces()
            }
        }
    }

    private fun loadProvinces() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingProvinces = true, error = null)
            when (val result = shippingRepository.getProvinces()) {
                is Result.Success -> _uiState.value = _uiState.value.copy(
                    provinces = result.data,
                    isLoadingProvinces = false
                )
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isLoadingProvinces = false,
                    error = result.message
                )
                else -> _uiState.value = _uiState.value.copy(isLoadingProvinces = false)
            }
        }
    }

    fun onProvinceSelected(province: Province) {
        _uiState.value = _uiState.value.copy(
            selectedProvince = province,
            selectedCity = null,
            cities = emptyList(),
            postalCode = "",
            addressWarning = checkAddressConsistency(_uiState.value.streetDetail, province)
        )
        loadCities(province.id)
    }

    private fun loadCities(provinceId: String, preSelectCityId: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingCities = true, error = null)
            when (val result = shippingRepository.getCities(provinceId)) {
                is Result.Success -> {
                    val city = if (preSelectCityId != null)
                        result.data.find { it.id == preSelectCityId } else null
                    val resolvedPostalCode = city?.postalCode?.ifBlank { null }
                        ?: _uiState.value.postalCode
                    _uiState.value = _uiState.value.copy(
                        cities = result.data,
                        selectedCity = city,
                        postalCode = resolvedPostalCode,
                        isLoadingCities = false
                    )
                    // If the city's postal code is blank, fetch it on demand
                    if (city != null && city.postalCode.isBlank() && resolvedPostalCode.isBlank()) {
                        val fetched = shippingRepository.getPostalCode(city.id)
                        if (fetched.isNotBlank()) {
                            _uiState.value = _uiState.value.copy(postalCode = fetched)
                        }
                    }
                }
                is Result.Error -> _uiState.value = _uiState.value.copy(
                    isLoadingCities = false,
                    error = result.message
                )
                else -> _uiState.value = _uiState.value.copy(isLoadingCities = false)
            }
        }
    }

    fun onCitySelected(city: City) {
        _uiState.value = _uiState.value.copy(
            selectedCity = city,
            postalCode = city.postalCode
        )
        if (city.postalCode.isBlank()) {
            viewModelScope.launch {
                val fetched = shippingRepository.getPostalCode(city.id)
                if (fetched.isNotBlank()) {
                    _uiState.value = _uiState.value.copy(postalCode = fetched)
                }
            }
        }
    }

    fun onStreetDetailChange(value: String) {
        _uiState.value = _uiState.value.copy(
            streetDetail = value,
            addressWarning = checkAddressConsistency(value, _uiState.value.selectedProvince)
        )
    }

    fun onPostalCodeChange(value: String) {
        // Only allow digits, max 5 chars
        val filtered = value.filter { it.isDigit() }.take(5)
        _uiState.value = _uiState.value.copy(postalCode = filtered)
    }

    private fun checkAddressConsistency(streetDetail: String, selectedProvince: Province?): String? {
        if (selectedProvince == null || streetDetail.length < 8) return null
        val lowerProvince = selectedProvince.name.lowercase()

        for ((keyword, provinceHint) in CITY_PROVINCE_MAP) {
            val pattern = Regex("\\b${Regex.escape(keyword)}\\b", RegexOption.IGNORE_CASE)
            if (pattern.containsMatchIn(streetDetail)) {
                val lowerHint = provinceHint.lowercase()
                // Same province → no warning
                val isSame = lowerProvince.contains(lowerHint) ||
                        lowerHint.contains(lowerProvince) ||
                        lowerProvince.split(" ").any { word -> word.length > 3 && lowerHint.contains(word) }
                if (!isSame) {
                    val displayKeyword = keyword.split(" ")
                        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
                    return "\"$displayKeyword\" termasuk wilayah $provinceHint, bukan ${selectedProvince.name}. Cek kembali alamat Anda."
                }
            }
        }
        return null
    }

    fun saveAddress() {
        viewModelScope.launch {
            val state = _uiState.value
            val user = authRepository.getCurrentUser() ?: run {
                _uiState.value = state.copy(error = "Sesi login tidak ditemukan, silakan login ulang")
                return@launch
            }
            if (state.streetDetail.isBlank()) {
                _uiState.value = state.copy(error = "Detail jalan / alamat tidak boleh kosong")
                return@launch
            }
            if (state.streetDetail.length < 5) {
                _uiState.value = state.copy(error = "Alamat terlalu singkat (minimal 5 karakter)")
                return@launch
            }
            if (state.selectedCity == null) {
                _uiState.value = state.copy(error = "Pilih kota / kabupaten tujuan pengiriman")
                return@launch
            }

            _uiState.value = state.copy(isSaving = true, error = null)

            val result = authRepository.updateProfile(
                name = user.name,
                email = user.email,
                phone = user.phone,
                address = state.streetDetail.trim(),
                city = state.selectedCity.displayName,
                postalCode = state.postalCode.ifBlank { state.selectedCity.postalCode },
                provinceId = state.selectedProvince?.id,
                cityId = state.selectedCity.id
            )

            when (result) {
                is NetworkResult.Success -> _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true,
                    error = null
                )
                is NetworkResult.Error -> _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = result.message ?: "Gagal menyimpan alamat"
                )
                else -> _uiState.value = _uiState.value.copy(isSaving = false)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

