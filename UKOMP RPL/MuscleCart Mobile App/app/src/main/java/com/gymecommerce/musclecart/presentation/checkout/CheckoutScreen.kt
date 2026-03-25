package com.gymecommerce.musclecart.presentation.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gymecommerce.musclecart.domain.model.CourierService
import com.gymecommerce.musclecart.domain.repository.VoucherResult
import com.gymecommerce.musclecart.presentation.common.HomeButton
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onNavigateBack: () -> Unit,
    onOrderSuccess: (Int, String, String) -> Unit,
    onNavigateToAddress: () -> Unit = {},
    onHomeClick: (() -> Unit)? = null,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.orderSuccess) {
        uiState.orderSuccess?.let { orderId ->
            val items = uiState.cart.items
            val productNames = when {
                items.isEmpty() -> ""
                items.size == 1 -> items[0].product.name
                else -> "${items[0].product.name} + ${items.size - 1} lainnya"
            }
            val address = uiState.savedUser?.formattedAddress ?: ""
            onOrderSuccess(orderId, address, productNames)
        }
    }

    val shippingCost = uiState.selectedCourierService?.cost ?: 0
    val subtotal     = uiState.cart.getSubtotal()
    val tax          = uiState.cart.getTaxAmount()
    val discount     = uiState.appliedVoucher?.discountAmount ?: 0
    val pointsDiscount = uiState.appliedPoints / 10
    val grandTotal   = subtotal + tax + shippingCost - discount - pointsDiscount
    val formattedShipping = if (shippingCost == 0 && uiState.selectedCourierService == null)
        "Pilih kurir" else formatRp(shippingCost.toDouble())
    val formattedGrandTotal = formatRp(grandTotal)

    val canPlaceOrder = uiState.savedUser?.hasCompleteAddress == true &&
            uiState.selectedCourierService != null &&
            !uiState.isProcessingOrder

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Checkout") },
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

        when {
            uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            uiState.cart.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Keranjang kosong", fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // ── Order Summary ──────────────────────────────────────────────
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Ringkasan Pesanan", fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(bottom = 12.dp))
                                uiState.cart.items.forEach { cartItem ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(cartItem.product.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                            Text("${cartItem.product.getFormattedPrice()} x ${cartItem.quantity}",
                                                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Text(cartItem.getFormattedTotalPrice(), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                SummaryRow("Subtotal", uiState.cart.getFormattedSubtotal())
                                SummaryRow("Pajak (10%)", uiState.cart.getFormattedTax())
                                SummaryRow(
                                    label = "Ongkos Kirim",
                                    value = formattedShipping,
                                    valueColor = if (uiState.selectedCourierService == null)
                                        MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
                                )
                                if (discount > 0) {
                                    SummaryRow(
                                        label = "Diskon (${uiState.appliedVoucher?.code})",
                                        value = "- ${formatRp(discount.toDouble())}",
                                        valueColor = Color(0xFF10B981)
                                    )
                                }
                                if (pointsDiscount > 0) {
                                    SummaryRow(
                                        label = "Poin (${uiState.appliedPoints} poin)",
                                        value = "- ${formatRp(pointsDiscount.toDouble())}",
                                        valueColor = Color(0xFF10B981)
                                    )
                                }
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Total", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Text(formattedGrandTotal, fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }

                    // ── Alamat Pengiriman ──────────────────────────────────────────
                    item {
                        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null,
                                            modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                                        Text("Alamat Pengiriman", fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(start = 8.dp))
                                    }
                                    TextButton(onClick = onNavigateToAddress) {
                                        Icon(Icons.Default.Edit, contentDescription = null,
                                            modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Ubah")
                                    }
                                }

                                if (uiState.needsAddress || uiState.savedUser?.hasCompleteAddress != true) {
                                    // No saved address — show warning
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.errorContainer
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.Warning, contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(20.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Alamat belum diatur",
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 14.sp,
                                                    color = MaterialTheme.colorScheme.onErrorContainer)
                                                Text("Silakan atur alamat pengiriman terlebih dahulu",
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onErrorContainer)
                                            }
                                        }
                                    }
                                    Button(
                                        onClick = onNavigateToAddress,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Set Alamat Pengiriman")
                                    }
                                } else {
                                    // Saved address card
                                    val user = uiState.savedUser!!
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(user.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                            if (!user.phone.isNullOrBlank()) {
                                                Text(user.phone, fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Text(user.formattedAddress, fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Pilih Kurir (only when address is set) ────────────────────
                    if (uiState.savedUser?.hasCompleteAddress == true) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Pilih Kurir", fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(bottom = 12.dp))

                                    when {
                                        uiState.isLoadingCost -> {
                                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp),
                                                contentAlignment = Alignment.Center) {
                                                Row(verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                                    Text("Menghitung ongkir...", fontSize = 14.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }
                                        }
                                        uiState.shippingError != null -> {
                                            Text(uiState.shippingError!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                                        }
                                        uiState.courierServices.isEmpty() -> {
                                            Text("Tidak ada layanan kurir tersedia", fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        else -> {
                                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                uiState.courierServices.forEach { service ->
                                                    CourierServiceCard(
                                                        service = service,
                                                        isSelected = uiState.selectedCourierService == service,
                                                        onSelected = { viewModel.onCourierServiceSelected(service) }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ── Metode Pembayaran ──────────────────────────────────────────
                    item {
                        PaymentMethodCard(
                            selected = uiState.selectedPaymentMethod,
                            onSelect = viewModel::selectPaymentMethod
                        )
                    }

                    // ── Voucher ──────────────────────────────────────────────────
                    item {
                        VoucherCard(
                            voucherCode    = uiState.voucherCode,
                            appliedVoucher = uiState.appliedVoucher,
                            isApplying     = uiState.isApplyingVoucher,
                            error          = uiState.voucherError,
                            onCodeChanged  = viewModel::onVoucherCodeChanged,
                            onApply        = viewModel::applyVoucher,
                            onRemove       = viewModel::removeVoucher
                        )
                    }
                    // ── Reward Points ─────────────────────────────────────
                    item {
                        PointsCard(
                            pointsBalance  = uiState.pointsBalance,
                            pointsInput    = uiState.pointsInput,
                            appliedPoints  = uiState.appliedPoints,
                            isLoading      = uiState.isLoadingPoints,
                            error          = uiState.pointsError,
                            onInputChanged = viewModel::onPointsInputChanged,
                            onApply        = viewModel::applyPoints,
                            onRemove       = viewModel::removePoints
                        )
                    }
                    // ── Error ──────────────────────────────────────────────────────
                    if (uiState.error != null) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                                Text(uiState.error!!, color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(16.dp))
                            }
                        }
                    }
                }

                // ── Place Order Button ─────────────────────────────────────────
                Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (!canPlaceOrder && !uiState.isProcessingOrder) {
                            Text(
                                text = when {
                                    uiState.savedUser?.hasCompleteAddress != true -> "Atur alamat pengiriman terlebih dahulu"
                                    uiState.selectedCourierService == null -> "Pilih kurir pengiriman"
                                    else -> ""
                                },
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                        }
                        Button(
                            onClick = { viewModel.placeOrder() },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            enabled = canPlaceOrder,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (uiState.isProcessingOrder) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary)
                            } else {
                                Text("Buat Pesanan · $formattedGrandTotal",
                                    fontSize = 16.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 14.sp, color = valueColor)
    }
}

@Composable
private fun PaymentMethodCard(selected: String, onSelect: (String) -> Unit) {
    data class PaymentOption(val id: String, val label: String, val desc: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
    val options = listOf(
        PaymentOption("cash",     "Bayar di Tempat (COD)",   "Bayar saat pesanan tiba",        Icons.Default.Payments),
        PaymentOption("transfer", "Transfer Bank",            "BCA / Mandiri / BRI / BNI",      Icons.Default.Payments),
        PaymentOption("e_wallet", "Dompet Digital",           "GoPay / OVO / DANA / ShopeePay", Icons.Default.Payments)
    )
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Metode Pembayaran", fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp))
            options.forEach { opt ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(opt.id) }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RadioButton(selected = selected == opt.id, onClick = { onSelect(opt.id) })
                    Icon(opt.icon, contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = if (selected == opt.id) MaterialTheme.colorScheme.primary else Color(0xFF757575))
                    Column {
                        Text(opt.label, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        Text(opt.desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun CourierServiceCard(
    service: CourierService,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val bgColor     = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onSelected() },
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = isSelected, onClick = onSelected)
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text("${service.courier} ${service.service}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(service.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Estimasi ${service.etd}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(service.getFormattedCost(), fontWeight = FontWeight.Bold, fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary)
        }
    }
}

private fun formatRp(amount: Double): String {
    val symbols = DecimalFormatSymbols(Locale("id", "ID"))
    val formatter = DecimalFormat("#,###", symbols)
    return "Rp ${formatter.format(amount.toLong())}"
}

@Composable
private fun VoucherCard(
    voucherCode: String,
    appliedVoucher: VoucherResult?,
    isApplying: Boolean,
    error: String?,
    onCodeChanged: (String) -> Unit,
    onApply: () -> Unit,
    onRemove: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocalOffer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Kode Voucher", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (appliedVoucher != null) {
                // Applied state
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, Color(0xFF10B981))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                appliedVoucher.code,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                            if (!appliedVoucher.description.isNullOrBlank()) {
                                Text(
                                    appliedVoucher.description,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                "Hemat ${formatRp(appliedVoucher.discountAmount.toDouble())}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF10B981)
                            )
                        }
                        IconButton(onClick = onRemove) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Hapus voucher",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Input state
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = voucherCode,
                        onValueChange = onCodeChanged,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Masukkan kode voucher") },
                        singleLine = true,
                        isError = error != null,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Button(
                        onClick = onApply,
                        enabled = voucherCode.isNotBlank() && !isApplying,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isApplying) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Pakai")
                        }
                    }
                }
                if (error != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(error, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun PointsCard(
    pointsBalance: Int,
    pointsInput: String,
    appliedPoints: Int,
    isLoading: Boolean,
    error: String?,
    onInputChanged: (String) -> Unit,
    onApply: () -> Unit,
    onRemove: () -> Unit
) {
    val pointsValue = appliedPoints / 10
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reward Points", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.weight(1f))
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        "$pointsBalance poin",
                        fontSize = 13.sp,
                        color = Color(0xFFF59E0B),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                "10 poin = Rp1 diskon (maks 20% dari total pesanan)",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
            )

            if (appliedPoints > 0) {
                // Applied state
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF59E0B).copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "$appliedPoints poin digunakan",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706)
                            )
                            Text(
                                "Hemat ${formatRp(pointsValue.toDouble())}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFD97706)
                            )
                        }
                        IconButton(onClick = onRemove) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Hapus poin",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Input state
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = pointsInput,
                        onValueChange = onInputChanged,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Jumlah poin") },
                        singleLine = true,
                        isError = error != null,
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        )
                    )
                    Button(
                        onClick = onApply,
                        enabled = pointsInput.isNotBlank() && pointsBalance > 0,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Pakai")
                    }
                }
                if (error != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(error, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        }
    }
}
