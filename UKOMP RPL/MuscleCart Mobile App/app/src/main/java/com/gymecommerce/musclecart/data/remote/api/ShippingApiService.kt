package com.gymecommerce.musclecart.data.remote.api

import com.gymecommerce.musclecart.data.remote.dto.CityDto
import com.gymecommerce.musclecart.data.remote.dto.CourierServiceDto
import com.gymecommerce.musclecart.data.remote.dto.PostalCodeResponse
import com.gymecommerce.musclecart.data.remote.dto.ProvinceDto
import com.gymecommerce.musclecart.data.remote.dto.ShippingListResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ShippingApiService {

    @GET("shipping/provinces")
    suspend fun getProvinces(): Response<ShippingListResponse<ProvinceDto>>

    @GET("shipping/cities")
    suspend fun getCities(
        @Query("province_id") provinceId: String
    ): Response<ShippingListResponse<CityDto>>

    @GET("shipping/postal-code")
    suspend fun getPostalCode(
        @Query("city_id") cityId: String
    ): Response<PostalCodeResponse>

    @FormUrlEncoded
    @POST("shipping/cost")
    suspend fun getCost(
        @Field("destination_city_id") destinationCityId: String,
        @Field("weight") weight: Int,
        @Field("couriers[]") couriers: List<String>
    ): Response<ShippingListResponse<CourierServiceDto>>
}
