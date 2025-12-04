package com.example.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable

class CalculatorViewModel : ViewModel() {
    
    private val _equation = MutableLiveData("")
    val equation: LiveData<String> = _equation
    
    private val _result = MutableLiveData("")
    val result: LiveData<String> = _result
    
    fun onButtonClick(button: String) {
        when (button) {
            "AC" -> {
                // Clear all
                _equation.value = ""
                _result.value = ""
            }
            "C" -> {
                // Clear last character
                val current = _equation.value ?: ""
                if (current.isNotEmpty()) {
                    _equation.value = current.dropLast(1)
                    calculateResult()
                }
            }
            "=" -> {
                // Replace equation with result
                val resultValue = _result.value ?: ""
                if (resultValue.isNotEmpty()) {
                    _equation.value = resultValue
                    _result.value = ""
                }
            }
            else -> {
                // Add number or operator
                _equation.value = (_equation.value ?: "") + button
                calculateResult()
            }
        }
    }
    
    private fun calculateResult() {
        try {
            val equation = _equation.value ?: ""
            if (equation.isEmpty()) {
                _result.value = ""
                return
            }
            
            // Replace × with * and ÷ with /
            val processedEquation = equation
                .replace("×", "*")
                .replace("÷", "/")
            
            // Evaluate expression using Rhino
            val rhino = Context.enter()
            rhino.optimizationLevel = -1
            
            val scope: Scriptable = rhino.initStandardObjects()
            val result = rhino.evaluateString(scope, processedEquation, "JavaScript", 1, null)
            
            // Format result
            val resultValue = Context.toString(result)
            _result.value = if (resultValue.endsWith(".0")) {
                resultValue.dropLast(2)
            } else {
                resultValue
            }
            
            Context.exit()
        } catch (e: Exception) {
            // Don't show error, just keep calculating
            _result.value = ""
        }
    }
}
