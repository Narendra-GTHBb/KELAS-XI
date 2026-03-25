package com.gymecommerce.musclecart.presentation.review

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gymecommerce.musclecart.domain.model.Review
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    productId: Int,
    orderId: Int,
    productName: String,
    onNavigateBack: () -> Unit,
    viewModel: ReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        viewModel.init(productId, orderId, productName)
    }

    // Snackbar sukses
    LaunchedEffect(uiState.submitSuccess) {
        if (uiState.submitSuccess) viewModel.clearSuccess()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ulasan Produk") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Ringkasan rating
            item {
                RatingSummaryCard(
                    avgRating = uiState.avgRating,
                    totalReviews = uiState.totalReviews
                )
            }

            // Form tulis ulasan (hanya tampil jika orderId > 0 dan belum review)
            if (orderId > 0) {
                item {
                    if (uiState.hasReviewed) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text(
                                "Kamu sudah memberikan ulasan untuk produk ini.",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    } else {
                        WriteReviewCard(
                            productName = productName,
                            selectedRating = uiState.selectedRating,
                            comment = uiState.comment,
                            isSubmitting = uiState.isSubmitting,
                            error = uiState.error,
                            onRatingSelected = viewModel::onRatingSelected,
                            onCommentChanged = viewModel::onCommentChanged,
                            onSubmit = viewModel::submitReview
                        )
                    }
                }
            }

            // Daftar ulasan
            if (uiState.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.reviews.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(
                            "Belum ada ulasan untuk produk ini.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                item {
                    Text(
                        "Semua Ulasan (${uiState.totalReviews})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                items(uiState.reviews) { review ->
                    ReviewCard(review = review)
                }
            }
        }
    }
}

@Composable
private fun RatingSummaryCard(avgRating: Double, totalReviews: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (avgRating > 0) String.format("%.1f", avgRating) else "-",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                StarRow(rating = avgRating.toInt(), size = 20)
                Text(
                    text = "$totalReviews ulasan",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun WriteReviewCard(
    productName: String,
    selectedRating: Int,
    comment: String,
    isSubmitting: Boolean,
    error: String?,
    onRatingSelected: (Int) -> Unit,
    onCommentChanged: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Tulis Ulasanmu", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(
                productName,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Pilih bintang
            Text("Rating", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 1..5) {
                    IconButton(
                        onClick = { onRatingSelected(i) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (i <= selectedRating) Icons.Filled.Star
                                          else Icons.Outlined.StarOutline,
                            contentDescription = "$i bintang",
                            tint = if (i <= selectedRating) Color(0xFFFFC107)
                                   else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Komentar
            OutlinedTextField(
                value = comment,
                onValueChange = onCommentChanged,
                label = { Text("Komentar (opsional)") },
                placeholder = { Text("Ceritakan pengalamanmu dengan produk ini...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(8.dp)
            )

            if (error != null) {
                Text(error, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting && selectedRating > 0
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text("Kirim Ulasan")
            }
        }
    }
}

@Composable
private fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(review.userName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(
                    formatDate(review.createdAt),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(4.dp))
            StarRow(rating = review.rating, size = 16)
            if (!review.comment.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(review.comment, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun StarRow(rating: Int, size: Int) {
    Row {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = if (i <= rating) Color(0xFFFFC107)
                       else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(size.dp)
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) return ""
    val sdf = SimpleDateFormat("d MMM yyyy", Locale("id", "ID"))
    return sdf.format(Date(timestamp))
}
