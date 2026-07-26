package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CalculationHistoryEntity
import com.example.data.HistoryRepository
import com.example.util.ExpressionEvaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MemoryOp {
    CLEAR, RECALL, ADD, SUBTRACT, STORE
}

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository

    init {
        val dao = AppDatabase.getDatabase(application).calculationHistoryDao()
        repository = HistoryRepository(dao)
    }

    val historyList: StateFlow<List<CalculationHistoryEntity>> = repository.allHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _liveResult = MutableStateFlow("")
    val liveResult: StateFlow<String> = _liveResult.asStateFlow()

    private val _liveFeetInches = MutableStateFlow("")
    val liveFeetInches: StateFlow<String> = _liveFeetInches.asStateFlow()

    private val _memoryValue = MutableStateFlow(0.0)
    val memoryValue: StateFlow<Double> = _memoryValue.asStateFlow()

    private val _hasMemory = MutableStateFlow(false)
    val hasMemory: StateFlow<Boolean> = _hasMemory.asStateFlow()

    private val _showHistorySheet = MutableStateFlow(false)
    val showHistorySheet: StateFlow<Boolean> = _showHistorySheet.asStateFlow()

    private val _isFeetInchesPrimary = MutableStateFlow(false)
    val isFeetInchesPrimary: StateFlow<Boolean> = _isFeetInchesPrimary.asStateFlow()

    private val _showQuickFractions = MutableStateFlow(true)
    val showQuickFractions: StateFlow<Boolean> = _showQuickFractions.asStateFlow()

    private var isEvaluated = false

    fun onDigitInput(digit: String) {
        if (isEvaluated) {
            _expression.value = ""
            isEvaluated = false
        }
        val current = _expression.value
        // Avoid multiple leading zeroes or double decimal points in current number segment
        if (digit == ".") {
            val lastNumSegment = current.takeLastWhile { it.isDigit() || it == '.' }
            if (lastNumSegment.contains(".")) return
            if (current.isEmpty() || current.last().let { it == '+' || it == '-' || it == '×' || it == '÷' || it == '(' || it == ' ' }) {
                _expression.value = current + "0."
                updateLiveResult()
                return
            }
        }
        _expression.value = current + digit
        updateLiveResult()
    }

    fun onOperatorInput(operator: String) {
        isEvaluated = false
        val current = _expression.value
        if (current.isEmpty()) {
            if (operator == "-") {
                _expression.value = "-"
            } else if (_liveResult.value.isNotEmpty() && _liveResult.value != "Error") {
                _expression.value = _liveResult.value + " $operator "
            }
            return
        }

        val trimmed = current.trim()
        val lastChar = trimmed.last()

        if (lastChar == '+' || lastChar == '-' || lastChar == '×' || lastChar == '÷') {
            // Replace trailing operator
            val withoutLast = trimmed.dropLast(1).trimEnd()
            _expression.value = "$withoutLast $operator "
        } else {
            _expression.value = "$trimmed $operator "
        }
        updateLiveResult()
    }

    fun onBracketInput(bracket: String) {
        if (isEvaluated) {
            if (bracket == "(") _expression.value = ""
            isEvaluated = false
        }
        val current = _expression.value
        if (bracket == "(") {
            if (current.isNotEmpty() && current.last().isDigit()) {
                _expression.value = "$current × ("
            } else {
                _expression.value = "$current("
            }
        } else {
            _expression.value = "$current)"
        }
        updateLiveResult()
    }

    fun onFractionInput(fractionStr: String) {
        // e.g. "1/16", "1/8", "1/4", "1/2", "3/4"
        if (isEvaluated) {
            _expression.value = ""
            isEvaluated = false
        }
        val current = _expression.value
        if (current.isEmpty()) {
            _expression.value = fractionStr
        } else {
            val lastChar = current.trim().last()
            if (lastChar.isDigit() || lastChar == ')') {
                _expression.value = "$current + $fractionStr"
            } else {
                _expression.value = "$current $fractionStr"
            }
        }
        updateLiveResult()
    }

    fun onBackspace() {
        isEvaluated = false
        val current = _expression.value
        if (current.isNotEmpty()) {
            val updated = if (current.endsWith(" ")) {
                current.dropLast(2)
            } else {
                current.dropLast(1)
            }
            _expression.value = updated
            updateLiveResult()
        }
    }

    fun onClear() {
        isEvaluated = false
        _expression.value = ""
        _liveResult.value = ""
        _liveFeetInches.value = ""
    }

    fun onEquals() {
        val expr = _expression.value.trim()
        if (expr.isEmpty()) return

        try {
            val resultValue = ExpressionEvaluator.evaluate(expr)
            val formattedResult = ExpressionEvaluator.formatDecimal(resultValue)
            val feetInchesStr = ExpressionEvaluator.formatFeetInches(resultValue)

            _liveResult.value = formattedResult
            _liveFeetInches.value = feetInchesStr
            _expression.value = formattedResult
            isEvaluated = true

            // Store in Room DB history
            viewModelScope.launch {
                repository.insert(
                    expression = expr,
                    result = formattedResult,
                    feetInchesResult = feetInchesStr
                )
            }
        } catch (e: Exception) {
            _liveResult.value = "Error"
            _liveFeetInches.value = ""
        }
    }

    fun onMemoryOperation(op: MemoryOp) {
        val currentVal = parseCurrentOrLiveValue()

        when (op) {
            MemoryOp.CLEAR -> {
                _memoryValue.value = 0.0
                _hasMemory.value = false
            }
            MemoryOp.RECALL -> {
                val memStr = ExpressionEvaluator.formatDecimal(_memoryValue.value)
                if (isEvaluated) {
                    _expression.value = memStr
                    isEvaluated = false
                } else {
                    val current = _expression.value
                    if (current.isEmpty()) {
                        _expression.value = memStr
                    } else if (current.trim().last().let { it == '+' || it == '-' || it == '×' || it == '÷' || it == '(' }) {
                        _expression.value = "$current $memStr"
                    } else {
                        _expression.value = "$current × $memStr"
                    }
                }
                updateLiveResult()
            }
            MemoryOp.ADD -> {
                _memoryValue.value += currentVal
                _hasMemory.value = _memoryValue.value != 0.0
            }
            MemoryOp.SUBTRACT -> {
                _memoryValue.value -= currentVal
                _hasMemory.value = _memoryValue.value != 0.0
            }
            MemoryOp.STORE -> {
                _memoryValue.value = currentVal
                _hasMemory.value = _memoryValue.value != 0.0
            }
        }
    }

    private fun parseCurrentOrLiveValue(): Double {
        val expr = _expression.value.trim()
        if (expr.isNotEmpty()) {
            try {
                return ExpressionEvaluator.evaluate(expr)
            } catch (e: Exception) {
                // fallback to liveResult
            }
        }
        return _liveResult.value.toDoubleOrNull() ?: 0.0
    }

    private fun updateLiveResult() {
        val expr = _expression.value.trim()
        if (expr.isEmpty()) {
            _liveResult.value = ""
            _liveFeetInches.value = ""
            return
        }
        try {
            val value = ExpressionEvaluator.evaluate(expr)
            _liveResult.value = ExpressionEvaluator.formatDecimal(value)
            _liveFeetInches.value = ExpressionEvaluator.formatFeetInches(value)
        } catch (e: Exception) {
            // Keep previous or clear if syntax incomplete
            _liveResult.value = ""
            _liveFeetInches.value = ""
        }
    }

    fun toggleFeetInchesPrimary() {
        _isFeetInchesPrimary.value = !_isFeetInchesPrimary.value
    }

    fun toggleQuickFractions() {
        _showQuickFractions.value = !_showQuickFractions.value
    }

    fun toggleHistorySheet(show: Boolean? = null) {
        _showHistorySheet.value = show ?: !_showHistorySheet.value
    }

    fun selectHistoryItem(item: CalculationHistoryEntity, useResultOnly: Boolean) {
        if (useResultOnly) {
            _expression.value = item.result
        } else {
            _expression.value = item.expression
        }
        isEvaluated = false
        updateLiveResult()
        _showHistorySheet.value = false
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}
