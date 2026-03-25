package com.gymecommerce.musclecart.data.repository

import com.gymecommerce.musclecart.data.remote.api.ShippingApiService
import com.gymecommerce.musclecart.domain.model.City
import com.gymecommerce.musclecart.domain.model.CourierService
import com.gymecommerce.musclecart.domain.model.Province
import com.gymecommerce.musclecart.domain.model.Result
import com.gymecommerce.musclecart.domain.repository.ShippingRepository
import javax.inject.Inject

class ShippingRepositoryImpl @Inject constructor(
    private val shippingApiService: ShippingApiService
) : ShippingRepository {

    override suspend fun getProvinces(): Result<List<Province>> {
        return try {
            val response = shippingApiService.getProvinces()
            if (response.isSuccessful && response.body()?.status == "success") {
                val provinces = response.body()!!.data.map { dto ->
                    Province(id = dto.province_id, name = dto.province)
                }
                Result.Success(provinces)
            } else {
                Result.Error(response.body()?.let { "Failed to load provinces" } ?: "Network error")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load provinces")
        }
    }

    override suspend fun getPostalCode(cityId: String): String {
        return try {
            val response = shippingApiService.getPostalCode(cityId)
            if (response.isSuccessful && response.body()?.status == "success") {
                response.body()?.postal_code ?: ""
            } else ""
        } catch (e: Exception) {
            ""
        }
    }

    override suspend fun getCities(provinceId: String): Result<List<City>> {
        return try {
            val response = shippingApiService.getCities(provinceId)
            if (response.isSuccessful && response.body()?.status == "success") {
                val cities = response.body()!!.data.map { dto ->
                    City(
                        id = dto.city_id,
                        provinceId = dto.province_id,
                        type = dto.type,
                        name = dto.city_name,
                        postalCode = dto.postal_code
                    )
                }
                Result.Success(cities.sortedBy { it.name })
            } else {
                Result.Error("Failed to load cities")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to load cities")
        }
    }

    override suspend fun getCost(
        destinationCityId: String,
        weightGrams: Int,
        couriers: List<String>
    ): Result<List<CourierService>> {
        return try {
            val response = shippingApiService.getCost(destinationCityId, weightGrams, couriers)
            if (response.isSuccessful && response.body()?.status == "success") {
                val services = response.body()!!.data.map { dto ->
                    CourierService(
                        courier     = dto.courier,
                        service     = dto.service,
                        description = dto.description,
                        cost        = dto.cost,
                        etd         = dto.etd
                    )
                }
                Result.Success(services)
            } else {
                Result.Error("Failed to calculate shipping cost")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to calculate shipping cost")
        }
    }
}
