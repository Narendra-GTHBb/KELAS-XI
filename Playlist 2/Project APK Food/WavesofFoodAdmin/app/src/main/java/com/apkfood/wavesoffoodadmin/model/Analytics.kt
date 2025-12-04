package com.apkfood.wavesoffoodadmin.model

data class Analytics(
    val totalOrders: Int = 0,
    val totalRevenue: Double = 0.0,
    val totalUsers: Int = 0,
    val totalRestaurants: Int = 0,
    val activeOrders: Int = 0,
    val completedOrders: Int = 0,
    val cancelledOrders: Int = 0,
    val averageOrderValue: Double = 0.0,
    val topRestaurants: List<TopRestaurant> = emptyList(),
    val revenueByDay: List<DailyRevenue> = emptyList(),
    val ordersByStatus: Map<String, Int> = emptyMap(),
    val date: String = "",
    val period: String = "today" // today, week, month, year
)

data class TopRestaurant(
    val id: String = "",
    val name: String = "",
    val totalOrders: Int = 0,
    val totalRevenue: Double = 0.0,
    val rating: Double = 0.0
)

data class DailyRevenue(
    val date: String = "",
    val revenue: Double = 0.0,
    val orders: Int = 0
)
