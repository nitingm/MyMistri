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

sealed class Symbol {
    data class Number(val value: Double) : Symbol()
    data class Length(var feet: Int, var inch: Byte) : Symbol()
    data class Operator(val operator: Char) : Symbol()
}

enum class Mode {
    LENGTH,
    AREA
}

class CalculatorViewModel: ViewModel() {
    var inputExpression by mutableStateOf("")
    var outputValue by mutableStateOf("")
    private var isBracketOpen = false
    var mode = Mode.LENGTH
    var modeName by mutableStateOf("Length")
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()


    private var isFtinAllowed = true

    fun showSnackbar(message: String) {
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
        inputExpression = inputExpression.dropLast(1)
    }

    fun addOperand(operand: Char) {
        if (Regex(pattern = ".*[)]$").matches(inputExpression)) { // case : (12'5+3'4)3'4
            return
        }
        if(operand == '\'') {
            if(mode == Mode.LENGTH && !isFtinAllowed) {
                showSnackbar("Product of lengths not allowed in Length mode")
                return
            }
            if(Regex(pattern = "(.*\\'\\d+$)|(.*\\d\\'$)").matches(inputExpression)) { // case : 12'3'4
                return
            }
            if(!Regex(pattern = ".*\\d+$").matches(inputExpression)) { // case not 10 add 0 before '
                inputExpression += "0"
            }
            isFtinAllowed = false
        }
        inputExpression += operand
    }

    fun addOperator(operator: Char) {
        if(Regex(pattern = ".*\\'\\d+$").matches(inputExpression)) {
//            println("Invalid input") // do nothing
        } else if(Regex(pattern = ".*\\d\\'$").matches(inputExpression)) {
            inputExpression += "0"
        } else if (Regex(pattern = "(.*[+-]\\d+$)|(\\d+)").matches(inputExpression)) {
            inputExpression += "'0"
        }
        if (operator == '(') {
            if (isBracketOpen) {
                if (!Regex(pattern = ".*[+-/*%(]$").matches(inputExpression)) {
                    isBracketOpen = false
                    inputExpression += ')'
                }
            } else {
                if (Regex(pattern = ".*[+-/*%]$").matches(inputExpression) || inputExpression.isEmpty() ) {
                    inputExpression +=  operator
                    isBracketOpen = true
                }
            }
            return
        }
        if (Regex(pattern = ".*[+-/*%]$").matches(inputExpression)) {
            inputExpression = inputExpression.dropLast(1) + operator
            return
        }
        if(operator == '+' || operator == '-') {
            isFtinAllowed = true
        }
        inputExpression += operator
    }

    fun calculateResult() {
        var inputExpressionArray = parseInputExpression()
        var postfixExpressionArray = infixToPostfix(inputExpressionArray)
        var result: Symbol.Length = calculatePostfix(postfixExpressionArray)
        outputValue = "${result.feet}'${result.inch}"
    }

    fun parseInputExpression() : Array<Symbol> {
        var result: Array<Symbol> = arrayOf()
        val splitExpressionArray = Regex(pattern = ("(\\d+(\\'\\d+))|\\+|\\-|\\*|\\/")).findAll(inputExpression)
        for (item in splitExpressionArray) {
            if (item.value.matches(Regex(pattern = ("\\d+(\\'\\d+)")))) {
                val (feet,inch) = item.value.split("'").map { it.toInt() }
                result += Symbol.Length(feet,inch.toByte())
            } else {
                result += Symbol.Operator(item.value[0])
            }
        }
        return result
    }

    fun infixToPostfix(inputExpressionArray: Array<Symbol>) : Array<Symbol> {
        var result: Array<Symbol> = arrayOf()
        var operatorStack = ArrayDeque<Symbol.Operator>()
        var operatorPriority = mapOf('+' to 1, '-' to 1, '*' to 2, '/' to 2 , '(' to 0, ')' to 0)
        for(item in inputExpressionArray) {
            if (item is Symbol.Length) {
                result += item
            } else if (item is Symbol.Operator) {
                if(item.operator == ')') {
                    while (operatorStack.last().operator != '(') {
                        result += operatorStack.last()
                        operatorStack.removeLast()
                    }
                    operatorStack.removeLast()
                } else {
                    while(operatorStack.isNotEmpty()) {
                        val lastOperator = operatorStack.last()?.operator ?: break
                        if(operatorPriority[lastOperator]!! >= operatorPriority[item.operator]!!) {
                            result += operatorStack.last()
                            operatorStack.removeLast()
                        } else {
                            break
                        }
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

    fun calculatePostfix(postfixExpressionArray: Array<Symbol>) : Symbol.Length {
        var tempExpressionArray = postfixExpressionArray
        while(tempExpressionArray.size > 1) {
            var a : Symbol.Length = tempExpressionArray[0] as Symbol.Length
            var b : Symbol.Length = tempExpressionArray[1] as Symbol.Length
            var incha = a.feet * 12 + a.inch
            var inchb = b.feet * 12 + b.inch
            Log.d("CalculatorViewModel", "a.feet = ${a.feet}, a.inch = ${a.inch}, b.feet = ${b.feet}, b.inch = ${b.inch}")
            var operator = tempExpressionArray[2] as Symbol.Operator
            var tempResult : Symbol.Length = Symbol.Length(0,0)
            when(operator.operator) {
                '+' -> {
                    tempResult = Symbol.Length( (incha + inchb) / 12, ((incha + inchb) % 12).toByte() )
                }
                '-' -> {
                    tempResult = Symbol.Length( (incha - inchb) / 12, ((incha - inchb) % 12).toByte() )
                }
                '*' -> {
                    tempResult = Symbol.Length( (incha * inchb) / (12*12), ((incha * inchb) % 12).toByte() )
                    Log.d("CalculatorViewModel", "incha = $incha, inchb = $inchb, result = ${tempResult.feet}'${tempResult.inch}")
                }
                '/' -> {
                    tempResult = Symbol.Length( (incha / inchb) / 12, ((incha / inchb) % 12).toByte() )
                }
                else -> {
                    println("Invalid operator")

                }
            }
            tempExpressionArray = arrayOf<Symbol>()+ tempResult + tempExpressionArray.sliceArray(3 until tempExpressionArray.size)
        }
        return tempExpressionArray[0] as Symbol.Length
    }
}