package com.gymecommerce.musclecart.navigation

import android.net.Uri

/**
 * Central navigation routes for MuscleCart.
 * Supports deep linking for product and order details.
 */
object NavRoutes {
    // Root / auth
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"

    // Main app tabs (used for bottom nav and start destinations)
    const val HOME = "home"
    const val CART = "cart"
    const val ORDERS = "orders"
    const val PROFILE = "profile"
    const val WISHLIST = "wishlist"
    // shop supports optional categoryId deep link
    const val SHOP = "shop?categoryId={categoryId}"
    const val SHOP_BASE = "shop"

    fun shop(categoryId: Int? = null) =
        if (categoryId != null) "shop?categoryId=$categoryId" else "shop"

    // Detail screens (nested under main)
    const val SEARCH = "search"
    const val EDIT_PROFILE = "edit_profile"
    const val EDIT_ADDRESS = "edit_address"
    const val TRACK_PACKAGES = "track_packages"
    const val PRODUCT_DETAIL = "product/{productId}"
    const val CHECKOUT = "checkout"
    const val ORDER_CONFIRMATION = "order_confirmation/{orderId}?address={address}&productNames={productNames}"
    const val ORDER_DETAIL = "order/{orderId}"
    const val REVIEW = "review/{productId}/{orderId}?productName={productName}"

    const val NOTIFICATIONS = "notifications"
    const val POINTS_HISTORY = "points_history"
    const val CHANGE_PASSWORD = "change_password"

    fun productDetail(productId: Int) = "product/$productId"
    fun orderConfirmation(orderId: Int, address: String = "", productNames: String = "") =
        "order_confirmation/$orderId?address=${Uri.encode(address)}&productNames=${Uri.encode(productNames)}"
    fun orderDetail(orderId: Int) = "order/$orderId"
    fun review(productId: Int, orderId: Int, productName: String = "") =
        "review/$productId/$orderId?productName=${Uri.encode(productName)}"
}
