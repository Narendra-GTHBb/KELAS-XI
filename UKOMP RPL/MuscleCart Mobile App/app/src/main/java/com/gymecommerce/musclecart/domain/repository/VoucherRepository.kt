package com.gymecommerce.musclecart.domain.repository

import com.gymecommerce.musclecart.domain.model.Result

data class VoucherResult(
    val code: String,
    val description: String?,
    val discountAmount: Int
)

interface VoucherRepository {
    suspend fun applyVoucher(code: String, subtotal: Double): Result<VoucherResult>
}
