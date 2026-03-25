package com.gymecommerce.musclecart.domain.model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class Province(
    val id: String,
    val name: String
)

data class City(
    val id: String,
    val provinceId: String,
    val type: String,       // "Kota" or "Kabupaten"
    val name: String,
    val postalCode: String
) {
    val displayName: String get() = "$type $name"
}

data class CourierService(
    val courier: String,        // "JNE"
    val service: String,        // "REG"
    val description: String,    // "Layanan Reguler"
    val cost: Int,              // in Rupiah
    val etd: String             // "2-3 HARI"
) {
    fun getFormattedCost(): String {
        val symbols = DecimalFormatSymbols(Locale("id", "ID"))
        val formatter = DecimalFormat("#,###", symbols)
        return "Rp ${formatter.format(cost)}"
    }

    val displayLabel: String get() = "$courier $service"
    val displayDetail: String get() = "$description · $etd · ${getFormattedCost()}"
}
