package com.gymecommerce.musclecart.data.remote.api

import com.gymecommerce.musclecart.data.remote.dto.ApplyVoucherRequest
import com.gymecommerce.musclecart.data.remote.dto.ApplyVoucherResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface VoucherApiService {
    @POST("vouchers/apply")
    suspend fun applyVoucher(@Body request: ApplyVoucherRequest): Response<ApplyVoucherResponse>
}
