package com.example.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel = viewModel()
) {
    val equation by viewModel.equation.observeAsState("")
    val result by viewModel.result.observeAsState("")
    
    // Button layout as shown in the screenshot
    val buttons = listOf(
        "C", "(", ")", "÷",
        "7", "8", "9", "×",
        "4", "5", "6", "+",
        "1", "2", "3", "-",
        "AC", "0", ".", "="
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1C))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Display Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            // Small result at top right
            Text(
                text = result,
                fontSize = 32.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp, top = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Main equation display
            Text(
                text = equation.ifEmpty { "0" },
                fontSize = 64.sp,
                color = Color.White,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp, bottom = 32.dp)
            )
        }
        
        // Button Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(buttons) { button ->
                CalculatorButton(
                    text = button,
                    onClick = { viewModel.onButtonClick(button) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun CalculatorButton(
    text: String,
    onClick: () -> Unit
) {
    val buttonColor = when (text) {
        "C", "AC" -> Color(0xFFFF5252) // Red
        "÷", "×", "+", "-", "=" -> Color(0xFFFF9800) // Orange
        "(", ")" -> Color(0xFF757575) // Gray
        else -> Color(0xFF00BCD4) // Cyan for numbers and dot
    }
    
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .size(80.dp),
        shape = CircleShape,
        containerColor = buttonColor,
        contentColor = Color.White
    ) {
        Text(
            text = text,
            fontSize = 28.sp,
            fontWeight = FontWeight.Normal
        )
    }
}
