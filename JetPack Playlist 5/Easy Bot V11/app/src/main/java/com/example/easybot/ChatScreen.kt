package com.example.easybot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Warna untuk gelembung mesej
private val UserBubbleColor = Color(0xFF007AFF)
private val ModelBubbleColor = Color(0xFFE8E8E8)
private val UserTextColor = Color.White
private val ModelTextColor = Color.Black

/**
 * Skrin utama chat yang mengandungi semua komponen
 */
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val messageList = viewModel.messageList
    val listState = rememberLazyListState()
    
    // Auto-scroll ke mesej terbaru
    LaunchedEffect(messageList.size) {
        if (messageList.isNotEmpty()) {
            listState.animateScrollToItem(messageList.size - 1)
        }
    }
    
    Scaffold(
        topBar = { AppHeader() },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Kawasan paparan mesej
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (messageList.isEmpty()) {
                    // Paparan kosong (Empty State)
                    EmptyState()
                } else {
                    // Senarai mesej
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(messageList) { message ->
                            MessageRow(message = message)
                        }
                    }
                }
            }
            
            // Input mesej di bahagian bawah
            MessageInput(
                onSendMessage = { message ->
                    viewModel.sendMessage(message)
                }
            )
        }
    }
}

/**
 * Bar atas aplikasi (Header)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader() {
    TopAppBar(
        title = {
            Text(
                text = "EasyBot",
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

/**
 * Paparan kosong apabila tiada mesej
 */
@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.QuestionAnswer,
                contentDescription = "Chat Icon",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ask me anything",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * Komponen input mesej dengan TextField dan butang hantar
 */
@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // TextField dengan weight(1f) supaya ikon sentiasa kelihatan
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Taip mesej anda...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                maxLines = 4
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Butang hantar
            IconButton(
                onClick = {
                    // Pengesahan: jangan hantar mesej kosong
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText)
                        inputText = "" // Reset input
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Hantar",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

/**
 * Baris mesej (gelembung) dengan alignment berbeza untuk User dan Model
 */
@Composable
fun MessageRow(message: MessageModel) {
    val isModel = message.role == MessageModel.ROLE_MODEL
    
    // Alignment: kiri untuk Model, kanan untuk User
    val alignment = if (isModel) Alignment.Start else Alignment.End
    
    // Warna gelembung berbeza
    val bubbleColor = if (isModel) ModelBubbleColor else UserBubbleColor
    val textColor = if (isModel) ModelTextColor else UserTextColor
    
    // Shape dengan sudut berbeza
    val bubbleShape = if (isModel) {
        RoundedCornerShape(4.dp, 16.dp, 16.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 4.dp, 16.dp, 16.dp)
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = alignment
    ) {
        // Label role
        Text(
            text = if (isModel) "EasyBot" else "Anda",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
        )
        
        // Gelembung mesej dengan SelectionContainer untuk membolehkan salin
        SelectionContainer {
            Box(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .clip(bubbleShape)
                    .background(bubbleColor)
                    .padding(12.dp)
            ) {
                Text(
                    text = message.message,
                    color = textColor,
                    fontSize = 16.sp
                )
            }
        }
    }
}
