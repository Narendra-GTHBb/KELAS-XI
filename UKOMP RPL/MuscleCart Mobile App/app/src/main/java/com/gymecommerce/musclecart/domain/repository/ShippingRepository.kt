package com.gymecommerce.musclecart.domain.repository

import com.gymecommerce.musclecart.domain.model.City
import com.gymecommerce.musclecart.domain.model.CourierService
import com.gymecommerce.musclecart.domain.model.Province
import com.gymecommerce.musclecart.domain.model.Result

interface ShippingRepository {
    suspend fun getProvinces(): Result<List<Province>>
    suspend fun getCities(provinceId: String): Result<List<City>>
    suspend fun getPostalCode(cityId: String): String
    suspend fun getCost(destinationCityId: String, weightGrams: Int, couriers: List<String>): Result<List<CourierService>>
}
