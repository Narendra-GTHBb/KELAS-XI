package com.gymecommerce.musclecart.util

/**
 * Konfigurasi server - ubah HOST dan PORT di sini saja.
 *
 * Emulator Android  : "10.0.2.2"
 * HP Fisik (WiFi)   : IP LAN komputer, contoh "192.168.30.35"
 */
object ServerConfig {
    // ======== UBAH DI SINI ========
    private const val HOST = "192.168.30.35"
    private const val PORT = 8000
    // ==============================

    const val BASE_URL = "http://$HOST:$PORT/api/v1/"
    const val SERVER_URL = "http://$HOST:$PORT"

    /**
     * Perbaiki URL gambar dari backend agar mengarah ke server yang benar.
     * Backend bisa mengembalikan URL dengan 127.0.0.1 / localhost,
     * fungsi ini menggantinya ke HOST:PORT yang dikonfigurasi di atas.
     */
    fun fixImageUrl(url: String): String {
        return url
            .replace(Regex("http://127\\.0\\.0\\.1:\\d+"), SERVER_URL)
            .replace(Regex("http://localhost:\\d+"), SERVER_URL)
            .replace("http://127.0.0.1/", "$SERVER_URL/")
            .replace("http://localhost/", "$SERVER_URL/")
            .replace(Regex("http://10\\.0\\.2\\.2:\\d+"), SERVER_URL)
    }
}
