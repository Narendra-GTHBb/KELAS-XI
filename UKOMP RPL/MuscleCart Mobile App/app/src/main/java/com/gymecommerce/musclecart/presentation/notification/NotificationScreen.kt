package com.gymecommerce.musclecart.presentation.notification

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gymecommerce.musclecart.domain.repository.NotificationRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onNavigateBack: () -> Unit,
    onOrderClick: (Int) -> Unit = {},
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Notifikasi", fontWeight = FontWeight.SemiBold)
                        if (uiState.unreadCount > 0) {
                            Surface(shape = CircleShape, color = Color(0xFFEF4444)) {
                                Text(
                                    "${uiState.unreadCount}",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.unreadCount > 0) {
                        TextButton(onClick = { viewModel.readAll() }) {
                            Text("Baca Semua", fontSize = 13.sp, color = Color(0xFF1976D2))
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.notifications.isEmpty() -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.NotificationsNone, contentDescription = null,
                            modifier = Modifier.size(64.dp), tint = Color(0xFFBDBDBD))
                        Text("Belum ada notifikasi", color = Color(0xFF9E9E9E), fontSize = 15.sp)
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(uiState.notifications, key = { it.id }) { item ->
                        NotificationItem(
                            item = item,
                            onClick = {
                                if (!item.isRead) viewModel.read(item.id)
                                if (item.referenceType == "order" && item.referenceId != null) {
                                    onOrderClick(item.referenceId)
                                }
                            }
                        )
                        Divider(color = Color(0xFFF5F5F5))
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    item: NotificationRepository.NotificationItem,
    onClick: () -> Unit
) {
    val bgColor = if (item.isRead) Color.White else Color(0xFFF0F7FF)
    val icon: ImageVector = when (item.type) {
        "order_update" -> Icons.Default.LocalShipping
        "points"       -> Icons.Default.Star
        "promo"        -> Icons.Default.LocalOffer
        else           -> Icons.Default.Notifications
    }
    val iconColor: Color = when (item.type) {
        "order_update" -> Color(0xFF1976D2)
        "points"       -> Color(0xFFF59E0B)
        "promo"        -> Color(0xFF10B981)
        else           -> Color(0xFF757575)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(shape = CircleShape, color = iconColor.copy(alpha = 0.12f)) {
            Icon(icon, contentDescription = null,
                modifier = Modifier.size(40.dp).padding(9.dp), tint = iconColor)
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    item.title,
                    fontWeight = if (item.isRead) FontWeight.Normal else FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF212121),
                    modifier = Modifier.weight(1f)
                )
                if (!item.isRead) {
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier.size(8.dp).background(Color(0xFF1976D2), CircleShape)
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                item.body,
                fontSize = 13.sp,
                color = Color(0xFF616161),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (item.createdAt != null) {
                Spacer(Modifier.height(4.dp))
                Text(
                    formatNotifDate(item.createdAt),
                    fontSize = 11.sp,
                    color = Color(0xFF9E9E9E)
                )
            }
        }
    }
}

private fun formatNotifDate(iso: String): String {
    return try {
        val parts = iso.split("T")
        val date = parts[0].split("-")
        val time = parts.getOrNull(1)?.substring(0, 5) ?: ""
        "${date[2]}/${date[1]}/${date[0]} $time"
    } catch (e: Exception) { iso }
}
