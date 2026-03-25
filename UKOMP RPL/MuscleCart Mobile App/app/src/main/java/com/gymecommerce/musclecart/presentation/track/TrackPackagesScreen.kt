package com.gymecommerce.musclecart.presentation.track

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.gymecommerce.musclecart.domain.model.Order
import com.gymecommerce.musclecart.domain.model.OrderStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackPackagesScreen(
    onOrderClick: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: TrackPackagesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(uiState.isRefreshing)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Lacak Paket", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(44.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("Memuat paket...", fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                uiState.activeOrders.isEmpty() && !uiState.isLoading -> {
                    EmptyPackagesState(onRefresh = { viewModel.loadPackages() })
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Summary header
                        item {
                            ActivePackagesSummary(orders = uiState.activeOrders)
                        }

                        // Package cards
                        items(uiState.activeOrders, key = { it.id }) { order ->
                            PackageTrackCard(
                                order = order,
                                onClick = { onOrderClick(order.id) }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

// ─── Summary header ──────────────────────────────────────────────────────────

@Composable
private fun ActivePackagesSummary(orders: List<Order>) {
    val shipped = orders.count { it.status == OrderStatus.SHIPPED }
    val processing = orders.count { it.status == OrderStatus.PROCESSING || it.status == OrderStatus.PAID }
    val arrived = orders.count { it.status == OrderStatus.DELIVERED }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryStatItem(count = orders.size, label = "Total Aktif", icon = Icons.Default.Inventory2,
                iconTint = Color(0xFF90CAF9))
            VerticalDividerLine()
            SummaryStatItem(count = processing, label = "Diproses", icon = Icons.Default.Settings,
                iconTint = Color(0xFFA5D6A7))
            VerticalDividerLine()
            SummaryStatItem(count = shipped, label = "Dikirim", icon = Icons.Default.LocalShipping,
                iconTint = Color(0xFFCE93D8))
            VerticalDividerLine()
            SummaryStatItem(count = arrived, label = "Tiba", icon = Icons.Default.CheckCircle,
                iconTint = Color(0xFFFFCC80))
        }
    }
}

@Composable
private fun SummaryStatItem(count: Int, label: String, icon: ImageVector, iconTint: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
        Text(count.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(label, fontSize = 10.sp, color = Color(0xFFBBDEFB), letterSpacing = 0.3.sp)
    }
}

@Composable
private fun VerticalDividerLine() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(48.dp)
            .background(Color.White.copy(alpha = 0.2f))
    )
}

// ─── Package card ─────────────────────────────────────────────────────────────

@Composable
private fun PackageTrackCard(order: Order, onClick: () -> Unit) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")) }
    val productSummary = when {
        order.items.isEmpty() -> "Pesanan #${order.id}"
        order.items.size == 1 -> order.items[0].product?.name ?: "Produk #${order.items[0].productId}"
        else -> "${order.items[0].product?.name ?: "Produk"} + ${order.items.size - 1} item lainnya"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Top row: order ID + status chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Pesanan #${order.id}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        dateFormat.format(Date(order.createdAt)),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                PackageStatusChip(status = order.status)
            }

            // Product summary
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    productSummary,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            // Shipping info row
            if (!order.courier.isNullOrBlank() || !order.trackingNumber.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF0F4FF)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null,
                            tint = Color(0xFF1565C0), modifier = Modifier.size(16.dp))
                        Column {
                            if (!order.courier.isNullOrBlank()) {
                                Text(order.courier, fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold, color = Color(0xFF1565C0))
                            }
                            if (!order.trackingNumber.isNullOrBlank()) {
                                Text("Resi: ${order.trackingNumber}", fontSize = 12.sp,
                                    color = Color(0xFF1565C0))
                            }
                        }
                    }
                }
            } else if (order.status == OrderStatus.SHIPPED) {
                // Shipped but no tracking info yet
                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFF8E1)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null,
                            tint = Color(0xFFF57F17), modifier = Modifier.size(16.dp))
                        Text("Nomor resi belum tersedia", fontSize = 12.sp,
                            color = Color(0xFFF57F17))
                    }
                }
            }

            // Progress stepper
            PackageProgressStepper(status = order.status)

            // See detail button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onClick,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                    Text("Lihat Detail", fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary)
                    Icon(Icons.Default.ChevronRight, contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

// ─── Progress stepper ─────────────────────────────────────────────────────────

@Composable
private fun PackageProgressStepper(status: OrderStatus) {
    data class Step(val icon: ImageVector, val label: String, val targetStatus: OrderStatus)

    val steps = listOf(
        Step(Icons.Default.Payment,      "Dibayar",  OrderStatus.PAID),
        Step(Icons.Default.Settings,     "Diproses", OrderStatus.PROCESSING),
        Step(Icons.Default.LocalShipping,"Dikirim",  OrderStatus.SHIPPED),
        Step(Icons.Default.CheckCircle,  "Tiba",     OrderStatus.DELIVERED),
    )

    val currentStep = when (status) {
        OrderStatus.PAID       -> 0
        OrderStatus.PROCESSING -> 1
        OrderStatus.SHIPPED    -> 2
        OrderStatus.DELIVERED  -> 3
        else                   -> -1
    }

    val activeBlue  = Color(0xFF1565C0)
    val doneGreen   = Color(0xFF2E7D32)
    val inactiveGray = Color(0xFFE0E0E0)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            val isDone   = index < currentStep
            val isActive = index == currentStep

            // Node
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isDone   -> doneGreen
                                isActive -> activeBlue
                                else     -> inactiveGray
                            }
                        )
                ) {
                    Icon(
                        step.icon,
                        contentDescription = step.label,
                        modifier = Modifier.size(16.dp),
                        tint = if (isDone || isActive) Color.White else Color(0xFF9E9E9E)
                    )
                }
                Text(
                    step.label,
                    fontSize = 9.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isDone   -> doneGreen
                        isActive -> activeBlue
                        else     -> Color(0xFF9E9E9E)
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }

            // Connector line between steps
            if (index < steps.lastIndex) {
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .height(2.dp)
                        .background(if (isDone) doneGreen else inactiveGray)
                )
            }
        }
    }
}

// ─── Status chip ──────────────────────────────────────────────────────────────

@Composable
private fun PackageStatusChip(status: OrderStatus) {
    val (bgColor, textColor, label) = when (status) {
        OrderStatus.PAID       -> Triple(Color(0xFFDBEAFE), Color(0xFF1E40AF), "✅ Dibayar")
        OrderStatus.PROCESSING -> Triple(Color(0xFFEDE9FE), Color(0xFF4C1D95), "⚙️ Diproses")
        OrderStatus.SHIPPED    -> Triple(Color(0xFFF3E8FF), Color(0xFF6B21A8), "🚚 Dikirim")
        OrderStatus.DELIVERED  -> Triple(Color(0xFFD1FAE5), Color(0xFF065F46), "📦 Tiba")
        else                   -> Triple(Color(0xFFF3F4F6), Color(0xFF6B7280), status.getDisplayName())
    }
    Surface(shape = RoundedCornerShape(20.dp), color = bgColor) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
    }
}

// ─── Empty state ──────────────────────────────────────────────────────────────

@Composable
private fun EmptyPackagesState(onRefresh: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFE3F2FD),
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    Icons.Default.LocalShipping,
                    contentDescription = null,
                    tint = Color(0xFF1565C0),
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Tidak Ada Paket Aktif",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Paket akan muncul di sini setelah pembayaran dikonfirmasi dan sedang dalam proses pengiriman.",
            fontSize = 14.sp,
            color = Color(0xFF757575),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onRefresh,
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null,
                modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Refresh")
        }
    }
}
