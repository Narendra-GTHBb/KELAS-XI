package com.gymecommerce.musclecart.presentation.points

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gymecommerce.musclecart.domain.repository.PointsHistoryItem
import com.gymecommerce.musclecart.domain.repository.PointsRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsHistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: PointsHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reward Points", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Balance card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFFF8E1),
                    shadowElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(shape = CircleShape, color = Color(0xFFF59E0B)) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(52.dp).padding(12.dp),
                                tint = Color.White
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Total Reward Points", fontSize = 13.sp, color = Color(0xFF757575))
                        Text(
                            "${uiState.balance} poin",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706)
                        )
                        Text(
                            "= Rp ${String.format("%,d", uiState.balance / 10).replace(',', '.')}",
                            fontSize = 14.sp,
                            color = Color(0xFF9E9E9E)
                        )
                        Spacer(Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Text(
                                "10 poin = Rp 1 diskon saat checkout",
                                fontSize = 12.sp,
                                color = Color(0xFFD97706),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // History header
            item {
                Text(
                    "RIWAYAT TRANSAKSI",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF9E9E9E),
                    letterSpacing = 1.sp
                )
            }

            if (uiState.history.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color(0xFFBDBDBD)
                            )
                            Text(
                                "Belum ada riwayat poin",
                                color = Color(0xFF9E9E9E),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Lakukan pembelian untuk mendapatkan poin reward",
                                fontSize = 12.sp,
                                color = Color(0xFFBDBDBD),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(uiState.history) { item ->
                    PointsHistoryItem(item)
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun PointsHistoryItem(item: PointsHistoryItem) {
    val isEarn = item.type == "earn"
    val pointsColor = if (isEarn) Color(0xFF10B981) else Color(0xFFEF4444)
    val sign = if (isEarn) "+" else "-"
    val icon = if (isEarn) Icons.Default.Add else Icons.Default.Remove
    val iconBg = if (isEarn) Color(0xFF10B981) else Color(0xFFEF4444)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = CircleShape, color = iconBg.copy(alpha = 0.12f)) {
                Icon(icon, contentDescription = null,
                    modifier = Modifier.size(38.dp).padding(8.dp), tint = iconBg)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.description ?: if (isEarn) "Poin Reward" else "Penukaran Poin",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF212121)
                )
                if (item.orderNumber != null) {
                    Text(
                        "Pesanan ${item.orderNumber}",
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }
                if (item.createdAt != null) {
                    Text(
                        formatDate(item.createdAt),
                        fontSize = 11.sp,
                        color = Color(0xFFBDBDBD)
                    )
                }
            }
            Text(
                "$sign${item.points} poin",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = pointsColor
            )
        }
    }
}

private fun formatDate(iso: String): String {
    return try {
        val parts = iso.split("T")
        val date = parts[0].split("-")
        "${date[2]}/${date[1]}/${date[0]}"
    } catch (e: Exception) { iso }
}
