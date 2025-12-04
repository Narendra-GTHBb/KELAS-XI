package com.example.realtimeweather

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realtimeweather.api.NetworkResponse
import com.example.realtimeweather.api.RetrofitInstance
import com.example.realtimeweather.api.WeatherModel
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    
    private val weatherAPI = RetrofitInstance.weatherAPI
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherModel>>()
    val weatherResult: LiveData<NetworkResponse<WeatherModel>> = _weatherResult
    
    // PENTING: Ganti dengan API key Anda dari weatherapi.com
    // Daftar gratis di: https://www.weatherapi.com/signup.aspx
    // Untuk mendapatkan API key GRATIS yang VALID:
    // 1. Kunjungi: https://www.weatherapi.com/signup.aspx
    // 2. Daftar dengan email Anda (GRATIS, tidak perlu kartu kredit)
    // API key dari weatherapi.com (sudah valid!)
    // Daftar di: https://www.weatherapi.com/signup.aspx
    private val API_KEY = "7fc58abd969f4075bc6181505251210"
    
    fun getData(city: String) {
        // Validasi: Cek apakah API key sudah diganti
        if (API_KEY == "MASUKKAN_API_KEY_ANDA_DISINI" || API_KEY.isEmpty()) {
            _weatherResult.value = NetworkResponse.Error(
                "⚠️ API KEY BELUM DIISI!\n\n" +
                "Silakan:\n" +
                "1. Daftar GRATIS di weatherapi.com\n" +
                "2. Copy API key dari dashboard\n" +
                "3. Paste ke WeatherViewModel.kt\n\n" +
                "Lihat file: TUTORIAL_API_KEY_LENGKAP.txt"
            )
            Log.e("WeatherViewModel", "API KEY belum diisi! Baca TUTORIAL_API_KEY_LENGKAP.txt")
            return
        }
        
        _weatherResult.value = NetworkResponse.Loading
        
        viewModelScope.launch {
            try {
                Log.d("WeatherViewModel", "Fetching weather for: $city")
                val response = weatherAPI.getWeather(API_KEY, city)
                
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("WeatherViewModel", "Success: ${it.location.name}")
                        _weatherResult.value = NetworkResponse.Success(it)
                    } ?: run {
                        Log.e("WeatherViewModel", "Response body is null")
                        _weatherResult.value = NetworkResponse.Error("Empty response from server")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("WeatherViewModel", "API Error: ${response.code()} - $errorBody")
                    
                    // Parse error message untuk user yang lebih friendly
                    val userMessage = when (response.code()) {
                        401, 403 -> "❌ API KEY TIDAK VALID!\n\n" +
                                "API key yang Anda masukkan salah atau sudah expired.\n\n" +
                                "Solusi:\n" +
                                "1. Buka weatherapi.com/signup.aspx\n" +
                                "2. Daftar akun baru (GRATIS)\n" +
                                "3. Copy API key dari dashboard\n" +
                                "4. Paste ke WeatherViewModel.kt\n\n" +
                                "Detail error: $errorBody"
                        400 -> "❌ Nama kota tidak valid!\n\nGunakan nama kota dalam bahasa Inggris (contoh: London, Jakarta, Tokyo)"
                        else -> "API Error: ${response.code()}\n$errorBody"
                    }
                    
                    _weatherResult.value = NetworkResponse.Error(userMessage)
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Exception: ${e.message}", e)
                
                val userMessage = when {
                    e.message?.contains("Unable to resolve host") == true -> 
                        "❌ Tidak ada koneksi internet!\n\nPastikan device/emulator Anda terhubung ke internet."
                    e.message?.contains("timeout") == true -> 
                        "⏱️ Request timeout!\n\nKoneksi terlalu lambat, coba lagi."
                    else -> "Error: ${e.message}"
                }
                
                _weatherResult.value = NetworkResponse.Error(userMessage)
            }
        }
    }
}
