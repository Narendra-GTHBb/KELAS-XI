package com.gymecommerce.musclecart.presentation.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gymecommerce.musclecart.navigation.NavRoutes

@Composable
fun SplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600),
        label = "splash_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "MuscleCart",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }

    val currentOnAuth by rememberUpdatedState(onNavigateToAuth)
    val currentOnMain by rememberUpdatedState(onNavigateToMain)

    LaunchedEffect(uiState.isLoggedIn) {
        if (!uiState.isLoading && uiState.isLoggedIn != null) {
            if (uiState.isLoggedIn == true) {
                currentOnMain()
            } else {
                currentOnAuth()
            }
        }
    }
}
