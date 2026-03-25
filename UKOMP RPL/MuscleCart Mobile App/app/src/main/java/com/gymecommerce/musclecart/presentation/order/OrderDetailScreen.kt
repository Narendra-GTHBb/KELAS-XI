package com.gymecommerce.musclecart.presentation.order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.gymecommerce.musclecart.domain.model.Order
import com.gymecommerce.musclecart.domain.model.OrderItem
import com.gymecommerce.musclecart.domain.model.OrderStatus
import com.gymecommerce.musclecart.presentation.common.HomeButton
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    orderId: Int,
    onNavigateBack: () -> Unit,
    onHomeClick: (() -> Unit)? = null,
    onReviewClick: ((productId: Int, productName: String) -> Unit)? = null,
    viewModel: OrderDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Start auto-refresh polling; stop when screen leaves composition
    LaunchedEffect(orderId) {
        viewModel.startPolling(orderId)
    }
    DisposableEffect(Unit) {
        onDispose { viewModel.stopPolling() }
    }

    // Show success snackbar
    val successMessage = uiState.successMessage
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            snackbarHostState.showSnackbar(successMessage)
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detail Pesanan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (onHomeClick != null) {
                        HomeButton(onClick = onHomeClick, modifier = Modifier.padding(end = 8.dp))
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                uiState.isLoading && uiState.order == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null && uiState.order == null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Error", fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onErrorContainer)
                            Text(uiState.error!!, color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(top = 4.dp))
                            Button(onClick = { viewModel.loadOrder(orderId) },
                                modifier = Modifier.padding(top = 8.dp)) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }
                uiState.order != null -> {
                    OrderDetailContent(
                        order = uiState.order!!,
                        isConfirmingReceived = uiState.isConfirmingReceived,
                        isCancelling = uiState.isCancelling,
                        onConfirmReceived = { viewModel.confirmReceived() },
                        onCancelOrder = { viewModel.cancelOrder() },
                        onReviewClick = onReviewClick
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderDetailContent(
    order: Order,
    isConfirmingReceived: Boolean,
    isCancelling: Boolean = false,
    onConfirmReceived: () -> Unit,
    onCancelOrder: () -> Unit = {},
    onReviewClick: ((productId: Int, productName: String) -> Unit)? = null
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")) }
    var showCancelDialog by remember { mutableStateOf(false) }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Batalkan Pesanan?") },
            text = { Text("Apakah kamu yakin ingin membatalkan pesanan ini? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        onCancelOrder()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Ya, Batalkan")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showCancelDialog = false }) {
                    Text("Tidak")
                }
            }
        )
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Card
        item {
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Pesanan #${order.id}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        OrderStatusChip(status = order.status)
                    }
                    Text(
                        text = "Dibuat ${dateFormat.format(Date(order.createdAt))}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OrderTimeline(order = order)
                }
            }
        }

        // Tracking Info (shown when shipped or later)
        if (order.status == OrderStatus.SHIPPED || order.status == OrderStatus.DELIVERED ||
            order.status == OrderStatus.COMPLETED) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9FF))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null,
                                tint = Color(0xFF0369A1), modifier = Modifier.size(20.dp))
                            Text("Info Pengiriman", fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 8.dp))
                        }
                        if (!order.courier.isNullOrBlank()) {
                            Row(modifier = Modifier.padding(bottom = 4.dp)) {
                                Text("Kurir: ", fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(order.courier, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        if (!order.trackingNumber.isNullOrBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("No. Resi: ", fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(order.trackingNumber, fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                            }
                        }
                        if (order.trackingNumber.isNullOrBlank() && order.courier.isNullOrBlank()) {
                            Text("Informasi pengiriman belum tersedia",
                                fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Cancel Order Button (when PENDING or PAID)
        if (order.canBeCancelled()) {
            item {
                Button(
                    onClick = { showCancelDialog = true },
                    enabled = !isCancelling,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    if (isCancelling) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Icon(Icons.Default.Cancel, contentDescription = null,
                        modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Batalkan Pesanan", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Confirm Received Button (when DELIVERED)
        if (order.status == OrderStatus.DELIVERED) {
            item {
                Button(
                    onClick = onConfirmReceived,
                    enabled = !isConfirmingReceived,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                ) {
                    if (isConfirmingReceived) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                    Icon(Icons.Default.ThumbUp, contentDescription = null,
                        modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Konfirmasi Pesanan Diterima", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Shipping Address
        item {
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary)
                        Text("Alamat Pengiriman", fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 8.dp))
                    }
                    Text(text = order.shippingAddress, fontSize = 14.sp, lineHeight = 20.sp)
                }
            }
        }

        // Order Items header
        item {
            Text("Item Pesanan", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
        items(order.items) { orderItem ->
            OrderItemCard(
                orderItem = orderItem,
                showReviewButton = (order.status == OrderStatus.DELIVERED || order.status == OrderStatus.COMPLETED)
                    && onReviewClick != null,
                onReviewClick = { onReviewClick?.invoke(orderItem.productId, orderItem.product?.name ?: "") }
            )
        }

        // Order Summary
        item {
            val symbols = remember { DecimalFormatSymbols(Locale("id", "ID")) }
            val fmt = remember { DecimalFormat("#,###", symbols) }
            val shipping = order.shippingCost
            val tax = order.taxAmount
            val voucherDiscount = order.discountAmount
            val pointsDiscount = order.pointsUsed / 10
            // Raw items subtotal = totalAmount + voucherDiscount - tax - shipping
            val subtotal = (order.totalPrice + voucherDiscount - shipping - tax).coerceAtLeast(0.0)

            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Ringkasan Pesanan", fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp))
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Item (${order.getTotalItems()})", fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Rp ${fmt.format(subtotal.toLong())}", fontSize = 14.sp)
                    }
                    if (tax > 0) {
                        Spacer(Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pajak (10%)", fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Rp ${fmt.format(tax)}", fontSize = 14.sp)
                        }
                    }
                    if (shipping > 0) {
                        Spacer(Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Ongkos Kirim${if (!order.courier.isNullOrBlank()) " (${order.courier})" else ""}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Rp ${fmt.format(shipping)}", fontSize = 14.sp)
                        }
                    }
                    if (voucherDiscount > 0) {
                        Spacer(Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Diskon Voucher${if (!order.voucherCode.isNullOrBlank()) " (${order.voucherCode})" else ""}",
                                fontSize = 14.sp, color = Color(0xFF10B981))
                            Text("- Rp ${fmt.format(voucherDiscount)}", fontSize = 14.sp, color = Color(0xFF10B981))
                        }
                    }
                    if (pointsDiscount > 0) {
                        Spacer(Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Diskon Poin (${order.pointsUsed} poin)",
                                fontSize = 14.sp, color = Color(0xFF10B981))
                            Text("- Rp ${fmt.format(pointsDiscount)}", fontSize = 14.sp, color = Color(0xFF10B981))
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(order.getFormattedTotalPrice(), fontSize = 18.sp,
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderItemCard(
    orderItem: OrderItem,
    showReviewButton: Boolean = false,
    onReviewClick: () -> Unit = {}
) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                orderItem.product?.let { product ->
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.imageUrl).crossfade(true).build(),
                        contentDescription = product.name,
                        modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = orderItem.product?.name ?: "Product #${orderItem.productId}",
                        fontSize = 14.sp, fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 4.dp))
                    Text(text = "${orderItem.getFormattedPrice()} x ${orderItem.quantity}",
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(text = orderItem.getFormattedTotalPrice(), fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            }
            if (showReviewButton) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onReviewClick,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 6.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Beri Ulasan", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun OrderTimeline(order: Order) {
    data class TrackStep(
        val icon: ImageVector,
        val label: String,
        val description: String,
        val done: Boolean,
        val active: Boolean
    )

    val statusOrdinal = when (order.status) {
        OrderStatus.PENDING    -> 0
        OrderStatus.PAID       -> 1
        OrderStatus.PROCESSING -> 2
        OrderStatus.SHIPPED    -> 3
        OrderStatus.DELIVERED  -> 4
        OrderStatus.COMPLETED  -> 5
        OrderStatus.CANCELLED  -> -1
    }

    val steps = listOf(
        TrackStep(Icons.Default.ShoppingBag, "Pesanan Dibuat",
            "Pesanan berhasil diterima",
            done = statusOrdinal >= 1 || order.status == OrderStatus.CANCELLED,
            active = order.status == OrderStatus.PENDING),
        TrackStep(Icons.Default.Payment, "Pembayaran Dikonfirmasi",
            "Pembayaran berhasil diverifikasi",
            done = statusOrdinal >= 2,
            active = order.status == OrderStatus.PAID),
        TrackStep(Icons.Default.Inventory, "Sedang Diproses",
            "Pesanan sedang disiapkan",
            done = statusOrdinal >= 3,
            active = order.status == OrderStatus.PROCESSING),
        TrackStep(Icons.Default.LocalShipping, "Dikirim",
            "Paket dalam perjalanan",
            done = statusOrdinal >= 4,
            active = order.status == OrderStatus.SHIPPED),
        TrackStep(Icons.Default.CheckCircle, "Diterima",
            "Pesanan berhasil diterima",
            done = statusOrdinal >= 5,
            active = order.status == OrderStatus.DELIVERED || order.status == OrderStatus.COMPLETED)
    )

    val primaryGreen = Color(0xFF22C55E)
    val inactiveGray = Color(0xFFD1D5DB)
    val cancelledRed = Color(0xFFEF4444)

    if (order.status == OrderStatus.CANCELLED) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(36.dp).clip(CircleShape).background(cancelledRed)
            ) {
                Icon(Icons.Default.ShoppingBag, contentDescription = null,
                    tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Pesanan Dibatalkan", fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp, color = cancelledRed)
                Text("Pesanan ini telah dibatalkan", fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        return
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, step ->
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(44.dp)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                            .background(when {
                                step.done   -> primaryGreen
                                step.active -> Color(0xFF3B82F6)
                                else        -> Color(0xFFF3F4F6)
                            })
                            .then(if (!step.done && !step.active)
                                Modifier.border(1.5.dp, inactiveGray, CircleShape) else Modifier)
                    ) {
                        Icon(step.icon, contentDescription = step.label,
                            modifier = Modifier.size(20.dp),
                            tint = if (step.done || step.active) Color.White else inactiveGray)
                    }
                    if (index < steps.lastIndex) {
                        Box(modifier = Modifier.width(2.dp).height(36.dp)
                            .background(if (step.done) primaryGreen else inactiveGray))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f).padding(top = 8.dp)) {
                    Text(step.label, fontSize = 14.sp,
                        fontWeight = if (step.done || step.active) FontWeight.SemiBold else FontWeight.Normal,
                        color = when {
                            step.active -> Color(0xFF3B82F6)
                            step.done   -> Color(0xFF111827)
                            else        -> Color(0xFF9CA3AF)
                        })
                    Text(step.description, fontSize = 12.sp,
                        color = if (step.done || step.active) Color(0xFF6B7280) else Color(0xFFD1D5DB),
                        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp))
                }
            }
        }
    }
}

@Composable
private fun OrderStatusChip(status: OrderStatus) {
    val (bgColor, textColor, label) = when (status) {
        OrderStatus.PENDING    -> Triple(Color(0xFFFFF3CD), Color(0xFF92400E), "â³ Menunggu")
        OrderStatus.PAID       -> Triple(Color(0xFFDBEAFE), Color(0xFF1E40AF), "âœ… Dibayar")
        OrderStatus.PROCESSING -> Triple(Color(0xFFEDE9FE), Color(0xFF4C1D95), "âš™ï¸ Diproses")
        OrderStatus.SHIPPED    -> Triple(Color(0xFFF3E8FF), Color(0xFF6B21A8), "ðŸšš Dikirim")
        OrderStatus.DELIVERED  -> Triple(Color(0xFFD1FAE5), Color(0xFF065F46), "ðŸ“¦ Tiba")
        OrderStatus.COMPLETED  -> Triple(Color(0xFFD1FAE5), Color(0xFF064E3B), "ðŸŽ‰ Selesai")
        OrderStatus.CANCELLED  -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), "âŒ Dibatalkan")
    }
    Surface(shape = RoundedCornerShape(20.dp), color = bgColor) {
        Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp))
    }
}
