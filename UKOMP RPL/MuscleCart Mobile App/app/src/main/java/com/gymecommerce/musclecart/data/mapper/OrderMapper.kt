package com.gymecommerce.musclecart.data.mapper

import com.gymecommerce.musclecart.data.local.entity.OrderEntity
import com.gymecommerce.musclecart.data.remote.dto.OrderDto
import com.gymecommerce.musclecart.domain.model.Order
import com.gymecommerce.musclecart.domain.model.OrderItem
import com.gymecommerce.musclecart.domain.model.OrderStatus
import com.gymecommerce.musclecart.domain.model.OrderStatusHistoryItem
import com.gymecommerce.musclecart.util.DateUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderMapper @Inject constructor(
    private val productMapper: ProductMapper
) {

    fun entityToDomain(entity: OrderEntity, items: List<com.gymecommerce.musclecart.domain.model.OrderItem> = emptyList()): Order {
        return Order(
            id = entity.id,
            userId = entity.userId,
            totalPrice = entity.totalAmount,
            status = OrderStatus.fromString(entity.status),
            shippingAddress = entity.shippingAddress,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            isSynced = entity.isSynced,
            items = items
        )
    }

    fun domainToEntity(order: Order): OrderEntity {
        return OrderEntity(
            id = order.id,
            userId = order.userId,
            totalAmount = order.totalPrice,
            status = order.status.toString(),
            shippingAddress = order.shippingAddress,
            isSynced = order.isSynced,
            paymentMethod = "CASH",
            notes = null,
            createdAt = order.createdAt,
            updatedAt = order.updatedAt
        )
    }

    fun domainToDto(order: Order): OrderDto {
        return OrderDto(
            id = order.id,
            userId = order.userId,
            totalAmount = order.totalPrice,
            status = order.status.toString(),
            shippingAddress = order.shippingAddress,
            createdAt = DateUtils.formatTimestampToIso8601(order.createdAt),
            updatedAt = DateUtils.formatTimestampToIso8601(order.updatedAt)
        )
    }

    fun dtoToDomain(dto: OrderDto): Order {
        return Order(
            id = dto.id,
            userId = dto.userId,
            totalPrice = dto.totalAmount,
            status = OrderStatus.fromString(dto.status),
            shippingAddress = formatShippingAddress(dto.shippingAddress),
            createdAt = DateUtils.parseIso8601ToTimestamp(dto.createdAt),
            updatedAt = DateUtils.parseIso8601ToTimestamp(dto.updatedAt),
            isSynced = true,
            shippingCost = dto.shippingAmount.toInt(),
            taxAmount = dto.taxAmount.toInt(),
            discountAmount = dto.discountAmount.toInt(),
            voucherCode = dto.voucherCode,
            pointsEarned = dto.pointsEarned,
            pointsUsed = dto.pointsUsed,
            finalPrice = if (dto.finalPrice > 0.0) dto.finalPrice else dto.totalAmount,
            trackingNumber = dto.trackingNumber,
            courier = dto.courier,
            items = dto.orderItems?.map { item ->
                OrderItem(
                    id = item.id,
                    orderId = item.orderId,
                    productId = item.productId,
                    product = item.product?.let { p -> productMapper.dtoToDomain(p) },
                    quantity = item.quantity,
                    price = item.price,
                    createdAt = 0L,
                    updatedAt = 0L
                )
            } ?: emptyList(),
            statusHistory = dto.statusHistory?.map { h ->
                OrderStatusHistoryItem(
                    status = h.status,
                    previousStatus = h.previousStatus,
                    note = h.note,
                    changedByRole = h.changedByRole,
                    createdAt = DateUtils.parseIso8601ToTimestamp(h.createdAt)
                )
            } ?: emptyList()
        )
    }

    private fun formatShippingAddress(address: Any?): String {
        if (address == null) return ""
        if (address is String) return address.ifBlank { "" }
        // Gson deserializes JSON objects as LinkedTreeMap
        if (address is Map<*, *>) {
            val name = address["name"]?.toString().orEmpty()
            val phone = address["phone"]?.toString().orEmpty()
            val street = address["address"]?.toString().orEmpty()
            val city = address["city"]?.toString().orEmpty()
            val province = address["province"]?.toString().orEmpty()
            val postal = address["postal_code"]?.toString().orEmpty()
            return buildString {
                if (name.isNotBlank()) appendLine(name)
                if (phone.isNotBlank()) appendLine(phone)
                if (street.isNotBlank()) appendLine(street)
                val cityProvince = listOf(city, province).filter { it.isNotBlank() }.joinToString(", ")
                if (cityProvince.isNotBlank()) appendLine(cityProvince)
                if (postal.isNotBlank()) append(postal)
            }.trimEnd()
        }
        return address.toString()
    }
}
