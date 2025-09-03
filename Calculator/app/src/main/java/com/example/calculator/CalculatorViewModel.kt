package com.example.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import net.objecthunter.exp4j.ExpressionBuilder

data class LastOperation(
    val number: String,
    val operation: CalculatorAction.Operation
)

data class CalculatorState(
    val expression: String = "",
    val display: String = "0",
    val history: String = "",
    val lastOperation: LastOperation? = null,
    val showAboutDialog: Boolean = false
)

sealed class CalculatorAction {
    data class Number(val value: String) : CalculatorAction()
    data class Operation(val operation: String) : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object Decimal : CalculatorAction()
    object Calculate : CalculatorAction()
    object Percentage : CalculatorAction()
    object ShowAboutDialog : CalculatorAction()
    object HideAboutDialog : CalculatorAction()
}

class CalculatorViewModel : ViewModel() {

    var state by mutableStateOf(CalculatorState())
        private set

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.value)
            is CalculatorAction.Operation -> enterOperation(action.operation)
            is CalculatorAction.Decimal -> enterDecimal()
            CalculatorAction.Clear -> state = CalculatorState()
            CalculatorAction.Delete -> performDeletion()
            CalculatorAction.Calculate -> performCalculation()
            CalculatorAction.Percentage -> handlePercentage()
            CalculatorAction.ShowAboutDialog -> {
                state = state.copy(showAboutDialog = true)
            }
            CalculatorAction.HideAboutDialog -> {
                state = state.copy(showAboutDialog = false)
            }
        }
    }

    private fun enterNumber(number: String) {
        val newExpression = state.expression + number
        state = state.copy(
            expression = newExpression,
            display = newExpression,
            lastOperation = null
        )
    }

    private fun enterOperation(operation: String) {
        var currentExpression = state.expression

        if (currentExpression.isBlank() && state.display != "0" && state.display != "Error") {
            currentExpression = state.display
        }

        val newExpression = if (currentExpression.isNotEmpty() && currentExpression.last() in listOf('+', '-', '×', '÷')) {
            currentExpression.dropLast(1) + operation
        } else if (currentExpression.isEmpty()) {
            operation
        } else {
            currentExpression + operation
        }

        if (newExpression == "×" || newExpression == "÷") {
            return
        }
        state = state.copy(expression = newExpression, display = newExpression)
    }


    private fun enterDecimal() {
        val lastOperatorIndex = state.expression.lastIndexOfAny(charArrayOf('+', '-', '×', '÷'))
        val numberSegment = if (lastOperatorIndex == -1) state.expression else state.expression.substring(lastOperatorIndex + 1)
        if (numberSegment.contains(".").not() && numberSegment.isNotBlank()) {
            val newExpression = state.expression + "."
            state = state.copy(expression = newExpression, display = newExpression)
        }
    }

    private fun handlePercentage() {
        val expression = state.expression
        if (expression.isBlank()) {
            return
        }

        val lastOperatorIndex = expression.lastIndexOfAny(charArrayOf('+', '-'))
        val lastNumberSegment = if (lastOperatorIndex == -1) {
            expression
        } else {
            expression.substring(lastOperatorIndex + 1)
        }

        try {
            val number = lastNumberSegment.toDouble()
            val percentageValue = number / 100.0

            val newExpression = if (lastOperatorIndex == -1) {
                percentageValue.toString()
            } else {
                val baseExpression = expression.substring(0, lastOperatorIndex)
                val baseValue = evaluate(baseExpression).toDouble()
                val finalPart = (baseValue * percentageValue).toString()
                baseExpression + expression[lastOperatorIndex] + finalPart
            }

            state = state.copy(
                expression = newExpression,
                display = newExpression
            )

        } catch (e: NumberFormatException) {
            "Error : $e"
        }
    }

    private fun performDeletion() {
        val newExpression = state.expression.dropLast(1)
        state = state.copy(
            expression = newExpression,
            display = if (newExpression.isBlank()) "0" else newExpression
        )
    }

    private fun performCalculation() {
        val expressionToEvaluate = state.lastOperation?.let {
            state.display + it.operation.operation + it.number
        } ?: state.expression

        if (expressionToEvaluate.isBlank()) return

        val result = evaluate(expressionToEvaluate)
        if (result != "Error") {
            val lastOperatorIndex = expressionToEvaluate.lastIndexOfAny(charArrayOf('+', '-', '×', '÷'))
            val newLastOperation = if (state.lastOperation == null && lastOperatorIndex != -1) {
                val lastNumber = expressionToEvaluate.substring(lastOperatorIndex + 1)
                val lastOpChar = expressionToEvaluate[lastOperatorIndex].toString()
                LastOperation(lastNumber, CalculatorAction.Operation(lastOpChar))
            } else {
                state.lastOperation
            }

            state = state.copy(
                expression = result,
                display = result,
                history = expressionToEvaluate,
                lastOperation = newLastOperation
            )
        } else {
            state = state.copy(display = "Error")
        }
    }

    private fun evaluate(expression: String): String {
        return try {
            val expressionToParse = expression.replace('×', '*').replace('÷', '/')
            val exp = ExpressionBuilder(expressionToParse).build()
            val result = exp.evaluate()
            if (result % 1 == 0.0) result.toLong().toString() else result.toString()
        } catch (e: Exception) {
            "Error : $e"
        }
    }
}
