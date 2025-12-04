package com.apk.stateexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.apk.stateexample.components.*
import com.apk.stateexample.ui.theme.StateExampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StateExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StateManagementExamples(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

/**
 * Composable utama yang mendemonstrasikan berbagai teknik pengelolaan state:
 * 1. remember - State lokal yang akan hilang saat configuration change
 * 2. rememberSaveable - State lokal yang bertahan saat configuration change
 * 3. ViewModel - State terpusat yang bertahan seumur hidup Activity
 * 4. State Hoisting - Memindahkan state ke parent untuk reusability
 */
@Composable
fun StateManagementExamples(
    modifier: Modifier = Modifier,
    viewModel: StateExampleViewModel = viewModel()
) {
    // 1. REMEMBER - State lokal, hilang saat configuration change (rotasi layar)
    var rememberText by remember { mutableStateOf("") }
    var rememberCounter by remember { mutableIntStateOf(0) }
    
    // 2. REMEMBER SAVEABLE - State lokal, bertahan saat configuration change
    var saveableText by rememberSaveable { mutableStateOf("") }
    var saveableCounter by rememberSaveable { mutableIntStateOf(0) }
    
    // 3. VIEWMODEL - State terpusat menggunakan StateFlow dan observeAsState
    val userName by viewModel.userName.collectAsState()
    val viewModelCounter by viewModel.counter
    val items by viewModel.items.collectAsState()
    
    // State untuk input baru
    var newItemText by rememberSaveable { mutableStateOf("") }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        
        // SECTION 1: REMEMBER (State Lokal - Tidak Persisten)
        SectionHeader(
            title = "1. Remember (State Lokal)",
            description = "State hilang saat rotasi layar atau configuration change"
        )
        
        StatelessTextField(
            value = rememberText,
            onValueChange = { rememberText = it },
            label = "Remember Text",
            placeholder = "Teks ini akan hilang saat rotasi layar"
        )
        
        CounterDisplay(
            count = rememberCounter,
            onIncrement = { rememberCounter++ },
            onReset = { rememberCounter = 0 }
        )
        
        Divider()
        
        // SECTION 2: REMEMBER SAVEABLE (State Lokal - Persisten)
        SectionHeader(
            title = "2. RememberSaveable (State Persisten)",
            description = "State bertahan saat rotasi layar atau configuration change"
        )
        
        StatelessTextField(
            value = saveableText,
            onValueChange = { saveableText = it },
            label = "RememberSaveable Text",
            placeholder = "Teks ini bertahan saat rotasi layar"
        )
        
        CounterDisplay(
            count = saveableCounter,
            onIncrement = { saveableCounter++ },
            onReset = { saveableCounter = 0 }
        )
        
        Divider()
        
        // SECTION 3: VIEWMODEL (State Terpusat)
        SectionHeader(
            title = "3. ViewModel (State Terpusat)",
            description = "State dikelola secara terpusat dan dapat dibagikan antar Composables"
        )
        
        // Greeting menggunakan state dari ViewModel
        GreetingDisplay(
            name = userName,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Input untuk mengubah nama
        StatelessTextField(
            value = userName,
            onValueChange = { viewModel.updateUserName(it) },
            label = "Edit Name",
            placeholder = "Masukkan nama baru"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Counter menggunakan ViewModel
        CounterDisplay(
            count = viewModelCounter,
            onIncrement = { viewModel.incrementCounter() },
            onReset = { viewModel.resetCounter() }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // SECTION 4: STATE HOISTING dengan List Management
        SectionHeader(
            title = "4. State Hoisting + List Management",
            description = "Mengelola collection data dengan state hoisting"
        )
        
        // Input untuk item baru
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatelessTextField(
                value = newItemText,
                onValueChange = { newItemText = it },
                label = "New Item",
                placeholder = "Masukkan item baru",
                modifier = Modifier.weight(1f)
            )
            
            Button(
                onClick = {
                    if (newItemText.isNotBlank()) {
                        viewModel.addItem(newItemText)
                        newItemText = ""
                    }
                },
                modifier = Modifier.height(56.dp)
            ) {
                Text("Add")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Menampilkan list items
        ItemsList(
            items = items,
            onRemoveItem = { index ->
                viewModel.removeItem(index)
            }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Penjelasan konsep
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Konsep Penting:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = """
                    • remember: State lokal, hilang saat rotasi layar
                    • rememberSaveable: State lokal, bertahan saat rotasi
                    • ViewModel: State terpusat, bertahan seumur hidup Activity
                    • State Hoisting: Memindahkan state ke parent untuk reusability
                    • Recomposition: UI diperbarui otomatis saat state berubah
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StateManagementExamplesPreview() {
    StateExampleTheme {
        StateManagementExamples()
    }
}