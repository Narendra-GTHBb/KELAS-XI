package com.apk.stateexample.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Stateless Composable untuk input teks
 * State hoisting - state dan event handler diterima dari parent
 * Composable ini tidak memiliki state internal, sehingga mudah diuji dan digunakan kembali
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatelessTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        modifier = modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}

/**
 * Stateless Composable untuk menampilkan greeting
 * Menerima data dan styling dari parent
 */
@Composable
fun GreetingDisplay(
    name: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 20
) {
    Text(
        text = "Hello $name",
        modifier = modifier,
        fontSize = fontSize.sp,
        fontWeight = FontWeight.Medium
    )
}

/**
 * Stateless Composable untuk counter
 * Semua state dan event handling dilakukan di parent
 */
@Composable
fun CounterDisplay(
    count: Int,
    onIncrement: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Counter: $count",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onIncrement) {
                Text("Increment")
            }
            
            Button(
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Reset")
            }
        }
    }
}

/**
 * Stateless Composable untuk menampilkan list items
 * Menerima data dan callback functions dari parent
 */
@Composable
fun ItemsList(
    items: List<String>,
    onRemoveItem: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Items List:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        items.forEachIndexed { index, item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item,
                        fontSize = 14.sp
                    )
                    
                    Button(
                        onClick = { onRemoveItem(index) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.size(80.dp, 36.dp)
                    ) {
                        Text("Remove", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

/**
 * Composable untuk section header
 * Stateless dan reusable
 */
@Composable
fun SectionHeader(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = description,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}