package com.gymecommerce.musclecart.data.repository

import com.gymecommerce.musclecart.data.remote.api.VoucherApiService
import com.gymecommerce.musclecart.data.remote.dto.ApplyVoucherRequest
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.repository.VoucherRepository
import com.gymecommerce.musclecart.domain.repository.VoucherResult
import javax.inject.Inject

class VoucherRepositoryImpl @Inject constructor(
    private val voucherApiService: VoucherApiService
) : VoucherRepository {

    override suspend fun applyVoucher(code: String, subtotal: Double): Result<VoucherResult> {
        return try {
            val response = voucherApiService.applyVoucher(
                ApplyVoucherRequest(code = code.uppercase().trim(), subtotal = subtotal)
            )
            val body = response.body()
            if (response.isSuccessful && body?.status == "success" && body.data != null) {
                Result.Success(
                    VoucherResult(
                        code = body.data.code,
                        description = body.data.description,
                        discountAmount = body.data.discountAmount.toInt()
                    )
                )
            } else {
                val msg = body?.message ?: "Kode voucher tidak valid"
                Result.Error(msg)
            }
        } catch (e: Exception) {
            Result.Error("Gagal menghubungi server: ${e.message}")
        }
    }
}
