package com.example.ui

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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
import java.util.UUID

enum class MemoryOp {
    CLEAR, RECALL, ADD, SUBTRACT, STORE
}

enum class CalculatorAppMode {
    STANDARD, TIMBER_CFT
}

enum class TimberType {
    SAWN_TIMBER, // সাইজ কাঠ (L ft * W in * T in / 144)
    ROUND_LOG    // গোল কাঠ / গুঁড়ি ((Girth in)^2 * L ft / 2304)
}

enum class ActiveTimberField {
    LENGTH, WIDTH, THICKNESS, GIRTH, QUANTITY, PRICE
}

data class TimberBatchItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val details: String,
    val quantity: Int,
    val cft: Double,
    val unitPrice: Double,
    val totalPrice: Double
)

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

    // --- App Settings & Navigation State ---
    private val _currentScreen = MutableStateFlow(CalculatorAppMode.STANDARD)
    val currentScreen: StateFlow<CalculatorAppMode> = _currentScreen.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isRoundButtons = MutableStateFlow(false)
    val isRoundButtons: StateFlow<Boolean> = _isRoundButtons.asStateFlow()

    private val _isSoundEnabled = MutableStateFlow(true)
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private val _showFeetInchesInDisplay = MutableStateFlow(true)
    val showFeetInchesInDisplay: StateFlow<Boolean> = _showFeetInchesInDisplay.asStateFlow()

    private val _isFeetInchesPrimary = MutableStateFlow(false)
    val isFeetInchesPrimary: StateFlow<Boolean> = _isFeetInchesPrimary.asStateFlow()

    private val _showHistorySheet = MutableStateFlow(false)
    val showHistorySheet: StateFlow<Boolean> = _showHistorySheet.asStateFlow()

    // --- Standard Calculator State ---
    private val _expressionValue = MutableStateFlow(TextFieldValue(""))
    val expressionValue: StateFlow<TextFieldValue> = _expressionValue.asStateFlow()

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

    private var isEvaluated = false

    // --- Timber CFT Calculator State ---
    private val _timberType = MutableStateFlow(TimberType.SAWN_TIMBER)
    val timberType: StateFlow<TimberType> = _timberType.asStateFlow()

    private val _activeTimberField = MutableStateFlow(ActiveTimberField.LENGTH)
    val activeTimberField: StateFlow<ActiveTimberField> = _activeTimberField.asStateFlow()

    private val _timberLength = MutableStateFlow("")
    val timberLength: StateFlow<String> = _timberLength.asStateFlow()

    private val _timberWidth = MutableStateFlow("")
    val timberWidth: StateFlow<String> = _timberWidth.asStateFlow()

    private val _timberThickness = MutableStateFlow("")
    val timberThickness: StateFlow<String> = _timberThickness.asStateFlow()

    private val _timberGirth = MutableStateFlow("")
    val timberGirth: StateFlow<String> = _timberGirth.asStateFlow()

    private val _timberQuantity = MutableStateFlow("1")
    val timberQuantity: StateFlow<String> = _timberQuantity.asStateFlow()

    private val _timberUnitPrice = MutableStateFlow("")
    val timberUnitPrice: StateFlow<String> = _timberUnitPrice.asStateFlow()

    private val _timberBatch = MutableStateFlow<List<TimberBatchItem>>(emptyList())
    val timberBatch: StateFlow<List<TimberBatchItem>> = _timberBatch.asStateFlow()

    // --- Settings / Navigation Actions ---
    fun selectAppMode(mode: CalculatorAppMode) {
        _currentScreen.value = mode
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun toggleButtonShape() {
        _isRoundButtons.value = !_isRoundButtons.value
    }

    fun toggleSoundEnabled() {
        _isSoundEnabled.value = !_isSoundEnabled.value
    }

    fun toggleFeetInchesInDisplay() {
        _showFeetInchesInDisplay.value = !_showFeetInchesInDisplay.value
    }

    fun toggleFeetInchesPrimary() {
        _isFeetInchesPrimary.value = !_isFeetInchesPrimary.value
    }

    fun toggleHistorySheet(show: Boolean? = null) {
        _showHistorySheet.value = show ?: !_showHistorySheet.value
    }

    fun onExpressionValueChange(newValue: TextFieldValue) {
        _expressionValue.value = newValue
        _expression.value = newValue.text
        updateLiveResult()
    }

    private fun setExpressionWithCursor(text: String, cursorIndex: Int) {
        val clampedCursor = cursorIndex.coerceIn(0, text.length)
        _expressionValue.value = TextFieldValue(text, selection = TextRange(clampedCursor))
        _expression.value = text
        updateLiveResult()
    }

    fun moveCursorLeft() {
        val current = _expressionValue.value
        val newCursor = (current.selection.start - 1).coerceAtLeast(0)
        _expressionValue.value = current.copy(selection = TextRange(newCursor))
    }

    fun moveCursorRight() {
        val current = _expressionValue.value
        val newCursor = (current.selection.start + 1).coerceAtMost(current.text.length)
        _expressionValue.value = current.copy(selection = TextRange(newCursor))
    }

    // --- Standard Calculator Logic ---
    fun onDigitInput(digit: String) {
        if (isEvaluated) {
            _expressionValue.value = TextFieldValue("")
            _expression.value = ""
            isEvaluated = false
        }
        val tfv = _expressionValue.value
        val currentText = tfv.text
        val cursorStart = tfv.selection.start.coerceIn(0, currentText.length)
        val cursorEnd = tfv.selection.end.coerceIn(0, currentText.length)

        if (digit == ".") {
            val textBeforeCursor = currentText.substring(0, cursorStart)
            val lastNumSegment = textBeforeCursor.takeLastWhile { it.isDigit() || it == '.' }
            if (lastNumSegment.contains(".")) return
            if (cursorStart == 0 || textBeforeCursor.last().let { it == '+' || it == '-' || it == '×' || it == '÷' || it == '(' || it == ' ' }) {
                val newText = currentText.substring(0, cursorStart) + "0." + currentText.substring(cursorEnd)
                setExpressionWithCursor(newText, cursorStart + 2)
                return
            }
        }

        val newText = currentText.substring(0, cursorStart) + digit + currentText.substring(cursorEnd)
        setExpressionWithCursor(newText, cursorStart + digit.length)
    }

    fun onOperatorInput(operator: String) {
        isEvaluated = false
        val tfv = _expressionValue.value
        val currentText = tfv.text
        val cursorStart = tfv.selection.start.coerceIn(0, currentText.length)
        val cursorEnd = tfv.selection.end.coerceIn(0, currentText.length)

        if (currentText.isEmpty()) {
            if (operator == "-") {
                setExpressionWithCursor("-", 1)
            } else if (_liveResult.value.isNotEmpty() && _liveResult.value != "Error") {
                val newText = _liveResult.value + " $operator "
                setExpressionWithCursor(newText, newText.length)
            }
            return
        }

        val opString = " $operator "
        val newText = currentText.substring(0, cursorStart) + opString + currentText.substring(cursorEnd)
        setExpressionWithCursor(newText, cursorStart + opString.length)
    }

    fun onBracketInput(bracket: String = "AUTO") {
        if (isEvaluated) {
            _expressionValue.value = TextFieldValue("")
            _expression.value = ""
            isEvaluated = false
        }
        val tfv = _expressionValue.value
        val currentText = tfv.text
        val cursorStart = tfv.selection.start.coerceIn(0, currentText.length)
        val cursorEnd = tfv.selection.end.coerceIn(0, currentText.length)

        val openCount = currentText.count { it == '(' }
        val closeCount = currentText.count { it == ')' }

        val targetChar = if (bracket == "(" || bracket == ")") {
            bracket
        } else {
            if (openCount > closeCount && cursorStart > 0 && (currentText[cursorStart - 1].isDigit() || currentText[cursorStart - 1] == ')')) {
                ")"
            } else {
                "("
            }
        }

        val insertText = if (targetChar == "(") {
            if (cursorStart > 0 && currentText[cursorStart - 1].isDigit()) " × (" else "("
        } else {
            ")"
        }

        val newText = currentText.substring(0, cursorStart) + insertText + currentText.substring(cursorEnd)
        setExpressionWithCursor(newText, cursorStart + insertText.length)
    }

    fun onBackspace() {
        isEvaluated = false
        val tfv = _expressionValue.value
        val currentText = tfv.text
        val cursorStart = tfv.selection.start.coerceIn(0, currentText.length)
        val cursorEnd = tfv.selection.end.coerceIn(0, currentText.length)

        if (cursorStart != cursorEnd) {
            val newText = currentText.removeRange(cursorStart, cursorEnd)
            setExpressionWithCursor(newText, cursorStart)
            return
        }

        if (cursorStart > 0) {
            val textBefore = currentText.substring(0, cursorStart)
            val dropCount = if (textBefore.endsWith(" + ") || textBefore.endsWith(" - ") || textBefore.endsWith(" × ") || textBefore.endsWith(" ÷ ")) {
                3
            } else {
                1
            }
            val newText = currentText.substring(0, cursorStart - dropCount) + currentText.substring(cursorStart)
            setExpressionWithCursor(newText, cursorStart - dropCount)
        }
    }

    fun onClear() {
        isEvaluated = false
        _expressionValue.value = TextFieldValue("")
        _expression.value = ""
        _liveResult.value = ""
        _liveFeetInches.value = ""
    }

    fun onEquals() {
        val expr = _expressionValue.value.text.trim()
        if (expr.isEmpty()) return

        try {
            val resultValue = ExpressionEvaluator.evaluate(expr)
            val formattedResult = ExpressionEvaluator.formatDecimal(resultValue)
            val feetInchesStr = ExpressionEvaluator.formatFeetInches(resultValue)

            _liveResult.value = ""
            _liveFeetInches.value = feetInchesStr
            _expressionValue.value = TextFieldValue(formattedResult, selection = TextRange(formattedResult.length))
            _expression.value = formattedResult
            isEvaluated = true

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
                if (_currentScreen.value == CalculatorAppMode.TIMBER_CFT) {
                    getActiveFieldFlow().value = memStr
                } else {
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
            }
            MemoryOp.ADD -> {
                _memoryValue.value += currentVal
                _hasMemory.value = _memoryValue.value != 0.0
                if (_currentScreen.value == CalculatorAppMode.TIMBER_CFT) {
                    addTimberToHistoryOnMemory(currentVal, isAdd = true)
                }
            }
            MemoryOp.SUBTRACT -> {
                _memoryValue.value -= currentVal
                _hasMemory.value = _memoryValue.value != 0.0
                if (_currentScreen.value == CalculatorAppMode.TIMBER_CFT) {
                    addTimberToHistoryOnMemory(currentVal, isAdd = false)
                }
            }
            MemoryOp.STORE -> {
                _memoryValue.value = currentVal
                _hasMemory.value = _memoryValue.value != 0.0
                if (_currentScreen.value == CalculatorAppMode.TIMBER_CFT) {
                    addTimberToHistoryOnMemory(currentVal, isAdd = true)
                }
            }
        }
    }

    private fun addTimberToHistoryOnMemory(cftValue: Double, isAdd: Boolean) {
        val isSawn = _timberType.value == TimberType.SAWN_TIMBER
        val qty = _timberQuantity.value.toIntOrNull() ?: 1
        val pricePerCft = _timberUnitPrice.value.toDoubleOrNull() ?: 0.0
        val totalPrice = cftValue * pricePerCft

        val title = if (isSawn) {
            "সাইজ কাঠ (${_timberLength.value}' × ${_timberWidth.value}\" × ${_timberThickness.value}\")"
        } else {
            "গোল কাঠ (${_timberLength.value}', বেড়: ${_timberGirth.value}\")"
        }

        val opTag = if (isAdd) "M+" else "M-"
        val formattedCft = ExpressionEvaluator.formatTimberValue(cftValue)
        val formattedPrice = if (totalPrice > 0) " | ৳${ExpressionEvaluator.formatTimberValue(totalPrice)}" else ""

        viewModelScope.launch {
            repository.insert(
                expression = "[$opTag] $title [$qty টি]",
                result = "$formattedCft CFT",
                feetInchesResult = formattedPrice
            )
        }
    }

    private fun parseCurrentOrLiveValue(): Double {
        if (_currentScreen.value == CalculatorAppMode.TIMBER_CFT) {
            val timberCft = calculateSingleTimberCft()
            if (timberCft > 0.0) {
                return timberCft
            }
            val activeVal = getActiveFieldFlow().value.toDoubleOrNull() ?: 0.0
            if (activeVal > 0.0) {
                return activeVal
            }
        }
        val expr = _expression.value.trim()
        if (expr.isNotEmpty()) {
            try {
                return ExpressionEvaluator.evaluate(expr)
            } catch (e: Exception) {
                // fallback
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
            _liveResult.value = ""
            _liveFeetInches.value = ""
        }
    }

    fun selectHistoryItem(item: CalculationHistoryEntity, useResultOnly: Boolean) {
        val targetText = if (useResultOnly) item.result else item.expression
        _expressionValue.value = TextFieldValue(targetText, selection = TextRange(targetText.length))
        _expression.value = targetText
        isEvaluated = false
        updateLiveResult()
        _showHistorySheet.value = false
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch { repository.deleteById(id) }
    }

    fun clearAllHistory() {
        viewModelScope.launch { repository.clearAll() }
    }

    // --- Timber CFT Calculator Logic ---
    fun setTimberType(type: TimberType) {
        _timberType.value = type
        _activeTimberField.value = ActiveTimberField.LENGTH
    }

    fun setActiveTimberField(field: ActiveTimberField) {
        _activeTimberField.value = field
    }

    fun onTimberKeyInput(key: String) {
        val currentFlow = getActiveFieldFlow()
        val currentVal = currentFlow.value

        when (key) {
            "C", "CLEAR" -> currentFlow.value = ""
            "⌫", "X", "x", "DEL" -> if (currentVal.isNotEmpty()) currentFlow.value = currentVal.dropLast(1)
            "." -> if (!currentVal.contains(".")) currentFlow.value = if (currentVal.isEmpty()) "0." else "$currentVal."
            "NEXT" -> advanceToNextField()
            else -> {
                // Digit 0-9
                if (currentVal == "0" && key != "0") {
                    currentFlow.value = key
                } else if (_activeTimberField.value == ActiveTimberField.QUANTITY && currentVal == "1" && key != "0") {
                    currentFlow.value = key
                } else {
                    currentFlow.value = currentVal + key
                }
            }
        }
    }

    private fun getActiveFieldFlow(): MutableStateFlow<String> {
        return when (_activeTimberField.value) {
            ActiveTimberField.LENGTH -> _timberLength
            ActiveTimberField.WIDTH -> _timberWidth
            ActiveTimberField.THICKNESS -> _timberThickness
            ActiveTimberField.GIRTH -> _timberGirth
            ActiveTimberField.QUANTITY -> _timberQuantity
            ActiveTimberField.PRICE -> _timberUnitPrice
        }
    }

    private fun advanceToNextField() {
        val fields = if (_timberType.value == TimberType.SAWN_TIMBER) {
            listOf(ActiveTimberField.LENGTH, ActiveTimberField.WIDTH, ActiveTimberField.THICKNESS, ActiveTimberField.QUANTITY, ActiveTimberField.PRICE)
        } else {
            listOf(ActiveTimberField.LENGTH, ActiveTimberField.GIRTH, ActiveTimberField.QUANTITY, ActiveTimberField.PRICE)
        }
        val currentIndex = fields.indexOf(_activeTimberField.value)
        if (currentIndex in 0 until fields.size - 1) {
            _activeTimberField.value = fields[currentIndex + 1]
        } else {
            // Loop back to length or add to batch
            _activeTimberField.value = fields[0]
        }
    }

    fun calculateSingleTimberCft(): Double {
        val len = _timberLength.value.toDoubleOrNull() ?: 0.0
        val qty = _timberQuantity.value.toIntOrNull() ?: 1

        return if (_timberType.value == TimberType.SAWN_TIMBER) {
            val width = _timberWidth.value.toDoubleOrNull() ?: 0.0
            val thickness = _timberThickness.value.toDoubleOrNull() ?: 0.0
            if (len > 0 && width > 0 && thickness > 0) {
                (len * width * thickness) / 144.0 * qty
            } else 0.0
        } else {
            val girth = _timberGirth.value.toDoubleOrNull() ?: 0.0
            if (len > 0 && girth > 0) {
                (girth * girth * len) / 2304.0 * qty
            } else 0.0
        }
    }

    fun addCurrentToTimberBatch() {
        val singleCft = calculateSingleTimberCft()
        if (singleCft <= 0.0) return

        val qty = _timberQuantity.value.toIntOrNull() ?: 1
        val pricePerCft = _timberUnitPrice.value.toDoubleOrNull() ?: 0.0
        val totalPrice = singleCft * pricePerCft

        val isSawn = _timberType.value == TimberType.SAWN_TIMBER
        val title = if (isSawn) {
            "সাইজ কাঠ (${_timberLength.value}' × ${_timberWidth.value}\" × ${_timberThickness.value}\")"
        } else {
            "গোল কাঠ/লগ (দৈর্ঘ্য: ${_timberLength.value}', বেড়: ${_timberGirth.value}\")"
        }

        val details = "${qty} টি | ${String.format("%.2f", singleCft)} CFT" +
                if (pricePerCft > 0) " @ ৳${pricePerCft}/CFT" else ""

        val item = TimberBatchItem(
            title = title,
            details = details,
            quantity = qty,
            cft = singleCft,
            unitPrice = pricePerCft,
            totalPrice = totalPrice
        )

        _timberBatch.value = _timberBatch.value + item

        // Save entry to history DB as well
        viewModelScope.launch {
            repository.insert(
                expression = "$title [$qty টি]",
                result = "${String.format("%.2f", singleCft)} CFT",
                feetInchesResult = if (totalPrice > 0) "৳${String.format("%.2f", totalPrice)}" else ""
            )
        }

        // Reset inputs except Price
        _timberLength.value = ""
        _timberWidth.value = ""
        _timberThickness.value = ""
        _timberGirth.value = ""
        _timberQuantity.value = "1"
        _activeTimberField.value = ActiveTimberField.LENGTH
    }

    fun removeTimberBatchItem(id: String) {
        _timberBatch.value = _timberBatch.value.filter { it.id != id }
    }

    fun clearTimberBatch() {
        _timberBatch.value = emptyList()
    }
}
