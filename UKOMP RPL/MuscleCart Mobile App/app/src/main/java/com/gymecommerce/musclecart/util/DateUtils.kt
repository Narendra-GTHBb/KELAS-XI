package com.gymecommerce.musclecart.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val iso8601Format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    /**
     * Parse ISO 8601 date string to timestamp in milliseconds
     * Returns current timestamp if parsing fails or input is null
     */
    fun parseIso8601ToTimestamp(dateString: String?): Long {
        if (dateString.isNullOrBlank()) {
            return System.currentTimeMillis()
        }
        return try {
            iso8601Format.parse(dateString)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    /**
     * Format timestamp to ISO 8601 date string
     */
    fun formatTimestampToIso8601(timestamp: Long): String {
        return iso8601Format.format(Date(timestamp))
    }
}
