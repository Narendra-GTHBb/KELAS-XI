package com.apk.calculatorapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.apk.calculatorapp.ui.theme.CalculatorAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalculatorApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorApp(modifier: Modifier = Modifier) {
    // State untuk menyimpan input dari TextField
    var firstNumber by remember { mutableStateOf(TextFieldValue("")) }
    var secondNumber by remember { mutableStateOf(TextFieldValue("")) }
    
    val context = LocalContext.current
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // TextField pertama
        TextField(
            value = firstNumber,
            onValueChange = { firstNumber = it },
            label = { Text("First Number") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        
        // TextField kedua
        TextField(
            value = secondNumber,
            onValueChange = { secondNumber = it },
            label = { Text("Second Number") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Row untuk tombol-tombol operasi
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Tombol Add
            Button(
                onClick = {
                    try {
                        val num1 = firstNumber.text.toInt()
                        val num2 = secondNumber.text.toInt()
                        val result = num1 + num2
                        Toast.makeText(context, "Result: $result", Toast.LENGTH_SHORT).show()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(context, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.padding(4.dp)
            ) {
                Text("Add")
            }
            
            // Tombol Sub
            Button(
                onClick = {
                    try {
                        val num1 = firstNumber.text.toInt()
                        val num2 = secondNumber.text.toInt()
                        val result = num1 - num2
                        Toast.makeText(context, "Result: $result", Toast.LENGTH_SHORT).show()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(context, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.padding(4.dp)
            ) {
                Text("Sub")
            }
            
            // Tombol Mul
            Button(
                onClick = {
                    try {
                        val num1 = firstNumber.text.toInt()
                        val num2 = secondNumber.text.toInt()
                        val result = num1 * num2
                        Toast.makeText(context, "Result: $result", Toast.LENGTH_SHORT).show()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(context, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.padding(4.dp)
            ) {
                Text("Mul")
            }
            
            // Tombol Div
            Button(
                onClick = {
                    try {
                        val num1 = firstNumber.text.toInt()
                        val num2 = secondNumber.text.toInt()
                        if (num2 != 0) {
                            val result = num1 / num2
                            Toast.makeText(context, "Result: $result", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Cannot divide by zero", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: NumberFormatException) {
                        Toast.makeText(context, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.padding(4.dp)
            ) {
                Text("Div")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorAppPreview() {
    CalculatorAppTheme {
        CalculatorApp()
    }
}