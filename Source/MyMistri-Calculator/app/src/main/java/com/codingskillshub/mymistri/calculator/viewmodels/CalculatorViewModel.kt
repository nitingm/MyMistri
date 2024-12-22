package com.codingskillshub.mymistri.calculator.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.collections.ArrayDeque
import kotlin.math.abs

sealed class Symbol {
    data class Number(var value: Double) : Symbol() {
        operator fun plus(other: Number) : Number {
            return Number(this.value + other.value)
        }
        operator fun minus(other: Number) : Number {
            return Number(this.value - other.value)
        }

        operator fun times(other: Number) : Number {
            return Number(this.value * other.value)
        }

        operator fun div(other: Number) : Number {
            return Number(this.value / other.value)
        }
        operator fun times(other: Length) : Length {
            val inches = other.feet * 12 + other.inch
            val result = inches * (this.value).toInt()
            return Length(result / 12, result % 12)
        }
        override fun toString(): String {
            return "$value"
        }
    }
    data class Length(var feet: Int, var inch: Int) : Symbol() {
        operator fun plus(other: Length) : Length {
            val incha = this.feet * 12 + this.inch
            val inchb = other.feet * 12 + other.inch
            val result = incha + inchb
            return Length(result / 12, result % 12)
        }

        operator fun minus(other: Length) : Length {
            val incha = this.feet * 12 + this.inch
            val inchb = other.feet * 12 + other.inch
            val result = incha - inchb
            return Length(result / 12, result % 12)
        }

        operator fun times(other: Length) : Number {
            val incha = this.feet * 12 + this.inch
            val inchb = other.feet * 12 + other.inch
            val result = incha * inchb
            return Number((result / (12*12)).toDouble())
        }

        operator fun div(other: Length) : Number {
            val incha = this.feet * 12 + this.inch
            val inchb = other.feet * 12 + other.inch
            val result = incha / inchb
            return Number((result / (12*12)).toDouble())
        }

        operator fun times(other: Number) : Length {
            val inches = this.feet * 12 + this.inch
            val result = inches * (other.value).toInt()
            return Length(result / 12, result % 12)
        }
        operator fun div(other: Number) : Length {
            val inches = this.feet * 12 + this.inch
            val result = inches / (other.value).toInt()
            return Length(result / 12, result % 12)
        }
        override fun toString(): String {
            return "$feet'$inch"
        }
    }
    data class Operator(val operatorSymbol: Char) : Symbol() {
        override fun toString(): String {
            return "$operatorSymbol"
        }
    }
    override fun toString(): String {
        when (this) {
            is Symbol.Length -> {
                return (this as Symbol.Length).toString()
            }

            is Symbol.Number -> {
                return (this as Symbol.Number).toString()
            }

            is Symbol.Operator -> {
                return (this as Symbol.Operator).toString()
            }
        }
    }
}

enum class Mode {
    LENGTH,
    AREA
}

class CalculatorViewModel: ViewModel() {
    var inputExpression by mutableStateOf("")
    var outputValue by mutableStateOf("")
    var isExpressionValid by mutableStateOf(true)
    var mode = Mode.LENGTH
    var modeName by mutableStateOf("Length")
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    private fun showSnackbar(message: String) {
        viewModelScope.launch {
            _snackbarEvent.emit(message)
        }
    }

    fun updateInputExpression(expression: String) {
        inputExpression = expression
    }

    fun toggleMode() {
        mode = if (mode == Mode.LENGTH) Mode.AREA else Mode.LENGTH
        modeName = when(mode) {
            Mode.LENGTH -> "Length"
            Mode.AREA -> "Area"
        }
        Log.i("CalculatorViewModel", "Mode changed to $modeName")
    }

    fun removeLastChar() {
        isExpressionValid = true
        inputExpression = inputExpression.dropLast(1)
    }

    fun addOperand(operand: Char) {
        isExpressionValid = true
        if (Regex(pattern = ".*[)]$").matches(inputExpression)) { // case : (12'5+3'4)3'4 a ')' should be followed by a operator
            return
        }
        if(mode == Mode.LENGTH) {
            if(operand == '\'') {
                // case : 12'3'4
                if(Regex(pattern = "(.*\\'\\d+$)|(.*\\d\\'$)").matches(inputExpression)) {
                    return
                }
                // case: expression does not end with a number
                // example : 12'3+
                // solution: preppend 0 before ' so 12'3+0'
                if(!Regex(pattern = ".*\\d+$").matches(inputExpression)) {
                    inputExpression += "0"
                }
                inputExpression += operand
            } else if(operand.isDigit()) {
                if(Regex(pattern = "(.*\\'\\d+$)").matches(inputExpression)) {
                    if(Regex(pattern = ".*\\'\\d{1}$").matches(inputExpression)) { // case : inches greater than 11 ex: 0'42
                        if ((getLastNumberFromExpression(inputExpression) * 10 + operand.digitToInt()) < 12) {
                            inputExpression += operand
                        }
                    }
                } else {
                    inputExpression += operand
                }
            }
        } else if(mode == Mode.AREA) {
            if(operand == '\'') {
                // case : 12'3'4
                if(Regex(pattern = "(.*\\'\\d+$)|(.*\\d\\'$)").matches(inputExpression)) {
                    return
                }
                // case: expression does not end with a number
                // example : 12'3+
                // solution: preppend 0 before ' so 12'3+0'
                if(!Regex(pattern = ".*\\d+$").matches(inputExpression)) {
                    inputExpression += "0"
                }
                inputExpression += operand
            } else if(operand.isDigit()) {
                if(Regex(pattern = "(.*\\'\\d+$)").matches(inputExpression)) {
                    if(Regex(pattern = ".*\\'\\d{1}$").matches(inputExpression)) { // case : inches greater than 11 ex: 0'42
                        if ((getLastNumberFromExpression(inputExpression) * 10 + operand.digitToInt()) < 12) {
                            inputExpression += operand
                        }
                    }
                } else {
                    inputExpression += operand
                }
            }
        }
    }

    fun addOperator(operator: Char) {
        isExpressionValid = true
        if(operator == '%') return
        if(mode == Mode.LENGTH) {
            if(Regex(pattern = ".*\\d\\'$").matches(inputExpression)) {
                // case: 12'+ -> it is necessary to add 0 before the operator to get 12'0+
                inputExpression += "0"
            } else if (Regex(pattern = "(.*[+-]\\d+$)|(\\d+)").matches(inputExpression)) {
                // case: 12'4+5+ && 12+ -> it is necessary to add '0 before the operator to get 12'4+5'0+ && 12'0+
                inputExpression += "'0"
            }
            if (operator == '(') {
                if (Regex(pattern = ".*\\([^)]*$").matches(inputExpression)) {
                    if (!Regex(pattern = ".*[+-/*%(]$").matches(inputExpression)) {
                        inputExpression += ')'
                    }
                } else {
                    if (Regex(pattern = ".*[+-/*%]$").matches(inputExpression) || inputExpression.isEmpty() ) {
                        inputExpression +=  operator
                    }
                }
                return
            }
            if (Regex(pattern = ".*[+-/*%]$").matches(inputExpression)) {
                inputExpression = inputExpression.dropLast(1) + operator
                return
            }
            inputExpression += operator
        } else if(mode == Mode.AREA) {
            if (Regex(pattern = ".*\\d\\'$").matches(inputExpression)) {
                // case: 12'+ -> it is necessary to add 0 before the operator to get 12'0+
                inputExpression += "0"
            }
            if (operator == '(') {
                if (Regex(pattern = ".*\\([^)]*\$").matches(inputExpression)) {
                    if (!Regex(pattern = ".*[+-/*%(]$").matches(inputExpression)) {
                        inputExpression += ')'
                    }
                } else {
                    if (Regex(pattern = ".*[+-/*%]$").matches(inputExpression) || inputExpression.isEmpty()) {
                        inputExpression += operator
                    }
                }
                return
            }
            if (Regex(pattern = ".*[+-/*%]$").matches(inputExpression)) {
                inputExpression = inputExpression.dropLast(1) + operator
                return
            }
            inputExpression += operator
        }
    }

    fun calculateResult() {
        if(inputExpression.isNullOrEmpty()) return
        try {
            val inputExpressionArray = parseInputExpression()
            val postfixExpressionArray = infixToPostfix(inputExpressionArray)
            if(isExpressionValid) {
                if(mode == Mode.LENGTH) {
                    val result: Symbol.Length = calculateTotalLength(postfixExpressionArray)
                    if(result.feet < 0 || result.inch < 0) {
                        val feet = abs(result.feet)
                        val inch = abs(result.inch)
                        outputValue = "-${feet}'${inch}"
                    } else {
                        outputValue = "${result.feet}'${result.inch}"
                    }
                } else if(mode == Mode.AREA) {
                    val result: Symbol.Number = calculateTotalArea(postfixExpressionArray)
                    outputValue = "${result.value} sq.feet"
                }
            }
        } catch (e: Exception) {
            // Code for handling the exception
            Log.e("CalculatorViewModel", "Error calculating result", e)
        }
    }

    fun parseInputExpression() : Array<Symbol> {
        var result: Array<Symbol> = arrayOf()
        val splitExpressionArray: List<String> = Regex("(\\d+[.']?\\d*)|([+\\-*/()])")
            .findAll(inputExpression)
            .map { it.value }
            .toList()
        for (item in splitExpressionArray) {
            if (item.matches(Regex(pattern = "\\d+(\\'\\d+)"))) {
                val (feet,inch) = item.split("'").map { it.toInt() }
                result += Symbol.Length(feet,inch.toInt())
            } else if(item.matches(Regex(pattern = "\\d+(\\.\\d+)"))) {
                val value = item.toDouble()
                result += Symbol.Number(value)
            } else if(item.matches(Regex(pattern = "\\d+"))) {
                result += Symbol.Number(item.toDouble())
            } else if(item.matches(Regex(pattern = "[+\\-*/()]"))) {
                result += Symbol.Operator(item[0])
            }
        }
        if(result.last() is Symbol.Operator && (result.last() as Symbol.Operator).operatorSymbol !in "()") {
            isExpressionValid = false
        }
        return result
    }

    fun infixToPostfix(inputExpressionArray: Array<Symbol>) : Array<Symbol> {
        var result: Array<Symbol> = arrayOf()
        var operatorStack = ArrayDeque<Symbol.Operator>()
        var operatorPriority = mapOf('+' to 1, '-' to 1, '*' to 2, '/' to 2 , '(' to 0, ')' to 0)
        for(item in inputExpressionArray) {
            if (item is Symbol.Length || item is Symbol.Number) {
                result += item
            } else if (item is Symbol.Operator) {
                if(item.operatorSymbol == '(') {
                    operatorStack.addLast(item)
                } else if(item.operatorSymbol == ')') {
                    while (operatorStack.last().operatorSymbol != '(') {
                        result += operatorStack.last()
                        operatorStack.removeLast()
                    }
                    operatorStack.removeLast()
                } else {
                    while(operatorStack.isNotEmpty() &&
                        operatorPriority[operatorStack.last().operatorSymbol]!! >= operatorPriority[item.operatorSymbol]!!) {
                            result += operatorStack.last()
                            operatorStack.removeLast()
                    }
                    operatorStack.addLast(item)
                }
            }
        }
        while (operatorStack.isNotEmpty()) {
            result += operatorStack.last()
            operatorStack.removeLast()
        }
        return result
    }

    fun calculateTotalArea(postfixExpressionArray: Array<Symbol>) : Symbol.Number {
        var result: Symbol.Number = Symbol.Number(0.0)
        val operandStack: ArrayDeque<Symbol> = ArrayDeque()
        for(item in postfixExpressionArray) {
            if (item is Symbol.Length) {
                operandStack.addLast(item)
            } else if(item is Symbol.Number) {
                operandStack.addLast(item)
            } else if (item is Symbol.Operator) {
                val b = operandStack.removeLast()
                val a = operandStack.removeLast()
                when(item.operatorSymbol) {
                    '+' -> {
                        if(a is Symbol.Length && b is Symbol.Length) {
                            operandStack.addLast(a + b )
                        } else if(a is Symbol.Length && b is Symbol.Number
                            || a is Symbol.Number && b is Symbol.Length) {
                            showSnackbar("Invalid expression")
                            Log.d("CalculatorViewModel","Invalid expression: ${a}+${b}")
                            isExpressionValid = false
                            break
                        } else if(a is Symbol.Number && b is Symbol.Number) {
                            operandStack.addLast(a + b)
                        } else {
                            Log.d("CalculatorViewModel","Invalid expression")
                        }
                    }
                    '-' -> {
                        if(a is Symbol.Length && b is Symbol.Length) {
                            operandStack.addLast(a - b )
                        } else if(a is Symbol.Length && b is Symbol.Number
                            || a is Symbol.Number && b is Symbol.Length) {
                            showSnackbar("Invalid expression")
                            Log.d("CalculatorViewModel","Invalid expression: ${a}-${b}")
                            isExpressionValid = false
                            break
                        } else if(a is Symbol.Number && b is Symbol.Number) {
                            operandStack.addLast(a - b)
                        } else {
                            Log.d("CalculatorViewModel","Invalid expression")
                        }
                    }
                    '*' -> {
                        if(a is Symbol.Length && b is Symbol.Length) {
                            operandStack.addLast(a * b)
                        } else if(a is Symbol.Length && b is Symbol.Number) {
                            operandStack.addLast(a * b)
                        } else if(a is Symbol.Number && b is Symbol.Length) {
                            operandStack.addLast(a * b)
                        } else if(a is Symbol.Number && b is Symbol.Number) {
                            operandStack.addLast(a * b)
                        } else {
                            Log.d("CalculatorViewModel","Invalid expression")
                        }
                    }
                    '/' -> {
                        if(a is Symbol.Length && b is Symbol.Length) {
                            operandStack.addLast(a / b)
                        } else if(a is Symbol.Length && b is Symbol.Number) {
                            operandStack.addLast(a / b)
                        } else if(a is Symbol.Number && b is Symbol.Length) {
                            showSnackbar("Invalid expression")
                            Log.d("CalculatorViewModel","Invalid expression: ${a}/${b}")
                            isExpressionValid = false
                            break
                        } else if(a is Symbol.Number && b is Symbol.Number) {
                            operandStack.addLast(a / b)
                        } else {
                            Log.d("CalculatorViewModel","Invalid expression")
                        }
                    }
                    else -> {
                        Log.d("CalculatorViewModel","Invalid operator")
                    }
                }
            }
        }
        if(operandStack.size == 1) {
            if(operandStack.last() is Symbol.Number) {
                result = operandStack.last() as Symbol.Number
                isExpressionValid = true
            } else {
                showSnackbar("Invalid expression")
                Log.d("CalculatorViewModel","Invalid expression: Area mode output type mismatch")
            }
        }
        return result
    }

    fun calculateTotalLength(postfixExpressionArray: Array<Symbol>) : Symbol.Length {
        var result: Symbol.Length = Symbol.Length(0,0)
        val operandStack: ArrayDeque<Symbol> = ArrayDeque()
        for(item in postfixExpressionArray) {
            if (item is Symbol.Length) {
                operandStack.addLast(item)
            } else if(item is Symbol.Number) {
                operandStack.addLast(item)
            } else if (item is Symbol.Operator) {
                val b = operandStack.removeLast()
                val a = operandStack.removeLast()
                when(item.operatorSymbol) {
                    '+' -> {
                        if(a is Symbol.Length && b is Symbol.Length) {
                            operandStack.addLast(a + b )
                        } else if(a is Symbol.Length && b is Symbol.Number
                            || a is Symbol.Number && b is Symbol.Length) {
                            showSnackbar("Invalid expression")
                            Log.d("CalculatorViewModel","Invalid expression: ${a}+${b}")
                            isExpressionValid = false
                            break
                        } else if(a is Symbol.Number && b is Symbol.Number) {
                            operandStack.addLast(a + b)
                        } else {
                            Log.d("CalculatorViewModel","Invalid expression")
                        }
                    }
                    '-' -> {
                        if(a is Symbol.Length && b is Symbol.Length) {
                            operandStack.addLast(a - b )
                        } else if(a is Symbol.Length && b is Symbol.Number
                            || a is Symbol.Number && b is Symbol.Length) {
                            showSnackbar("Invalid expression")
                            Log.d("CalculatorViewModel","Invalid expression: ${a}-${b}")
                            isExpressionValid = false
                            break
                        } else if(a is Symbol.Number && b is Symbol.Number) {
                            operandStack.addLast(a - b)
                        } else {
                            Log.d("CalculatorViewModel","Invalid expression")
                        }
                    }
                    '*' -> {
                        if(a is Symbol.Length && b is Symbol.Length) {
                            showSnackbar("Length product not allowed in Length mode")
                            isExpressionValid = false
                            break
                        } else if(a is Symbol.Length && b is Symbol.Number) {
                            operandStack.addLast(a * b)
                        } else if(a is Symbol.Number && b is Symbol.Length) {
                            operandStack.addLast(a * b)
                        } else if(a is Symbol.Number && b is Symbol.Number) {
                            operandStack.addLast(a * b)
                        } else {
                            Log.d("CalculatorViewModel","Invalid expression")
                        }
                    }
                    '/' -> {
                        if(a is Symbol.Length && b is Symbol.Length) {
                            operandStack.addLast(a / b)
                        } else if(a is Symbol.Length && b is Symbol.Number) {
                            operandStack.addLast(a / b)
                        } else if(a is Symbol.Number && b is Symbol.Length) {
                            showSnackbar("Invalid expression")
                            Log.d("CalculatorViewModel","Invalid expression: ${a}/${b}")
                            isExpressionValid = false
                            break
                        } else if(a is Symbol.Number && b is Symbol.Number) {
                            operandStack.addLast(a / b)
                        } else {
                            Log.d("CalculatorViewModel","Invalid expression")
                        }
                    }
                    else -> {
                        Log.d("CalculatorViewModel","Invalid operator")
                    }
                }
            }
        }
        if(operandStack.size == 1) {
            if(operandStack.last() is Symbol.Length) {
                result = operandStack.last() as Symbol.Length
                isExpressionValid = true
            } else {
                showSnackbar("Invalid expression")
                Log.d("CalculatorViewModel","Invalid expression: Length mode output type mismatch")
            }
        }
        return result
    }

    private fun getLastNumberFromExpression(input: String): Int {
        val retStr = input.takeLastWhile { it.isDigit() }
        return if(!retStr.isNullOrEmpty()) retStr.toInt() else 0
    }
}