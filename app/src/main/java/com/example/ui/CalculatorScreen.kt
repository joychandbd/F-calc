package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.BottomModeOption
import com.example.ui.components.DisplayPanel
import com.example.ui.components.HistoryBottomSheet
import com.example.ui.components.KeypadGrid
import com.example.ui.components.MemoryBar
import com.example.ui.components.TimberCftCalculatorContent
import com.example.ui.components.TopPillHeaderBar

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isRoundButtons by viewModel.isRoundButtons.collectAsStateWithLifecycle()

    // Standard Calculator state
    val expressionValue by viewModel.expressionValue.collectAsStateWithLifecycle()
    val liveResult by viewModel.liveResult.collectAsStateWithLifecycle()
    val hasMemory by viewModel.hasMemory.collectAsStateWithLifecycle()
    val memoryValue by viewModel.memoryValue.collectAsStateWithLifecycle()
    val showHistorySheet by viewModel.showHistorySheet.collectAsStateWithLifecycle()
    val historyList by viewModel.historyList.collectAsStateWithLifecycle()

    // Timber CFT Calculator state
    val timberType by viewModel.timberType.collectAsStateWithLifecycle()
    val activeTimberField by viewModel.activeTimberField.collectAsStateWithLifecycle()
    val timberLength by viewModel.timberLength.collectAsStateWithLifecycle()
    val timberWidth by viewModel.timberWidth.collectAsStateWithLifecycle()
    val timberThickness by viewModel.timberThickness.collectAsStateWithLifecycle()
    val timberGirth by viewModel.timberGirth.collectAsStateWithLifecycle()
    val timberQuantity by viewModel.timberQuantity.collectAsStateWithLifecycle()
    val timberUnitPrice by viewModel.timberUnitPrice.collectAsStateWithLifecycle()
    val timberBatch by viewModel.timberBatch.collectAsStateWithLifecycle()

    val buttonShapeRadius = if (isRoundButtons) 30.dp else 14.dp

    val selectedBottomOption = if (currentScreen == CalculatorAppMode.STANDARD) {
        BottomModeOption.CALCULATOR
    } else {
        BottomModeOption.WOODEN
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .navigationBarsPadding()
        ) {
            // Top Pill Header Bar (Mode selector 85% width + History button 15% width at VERY top)
            TopPillHeaderBar(
                selectedOption = selectedBottomOption,
                isHistoryOpen = showHistorySheet,
                onOptionSelected = { option ->
                    when (option) {
                        BottomModeOption.CALCULATOR -> {
                            viewModel.selectAppMode(CalculatorAppMode.STANDARD)
                        }
                        BottomModeOption.WOODEN -> {
                            viewModel.selectAppMode(CalculatorAppMode.TIMBER_CFT)
                        }
                    }
                },
                onOpenHistory = { viewModel.toggleHistorySheet() }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (currentScreen == CalculatorAppMode.STANDARD) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Display Panel
                        DisplayPanel(
                            expressionValue = expressionValue,
                            onExpressionValueChange = viewModel::onExpressionValueChange,
                            liveResult = liveResult,
                            hasMemory = hasMemory,
                            memoryValue = memoryValue,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Memory Key Row
                        MemoryBar(
                            hasMemory = hasMemory,
                            onMemoryOp = viewModel::onMemoryOperation,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Keypad
                        KeypadGrid(
                            onDigitClick = viewModel::onDigitInput,
                            onOperatorClick = viewModel::onOperatorInput,
                            onBracketClick = viewModel::onBracketInput,
                            onBackspaceClick = viewModel::onBackspace,
                            onClearClick = viewModel::onClear,
                            onEqualsClick = viewModel::onEquals,
                            shapeRadius = buttonShapeRadius,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    TimberCftCalculatorContent(
                        timberType = timberType,
                        activeField = activeTimberField,
                        length = timberLength,
                        width = timberWidth,
                        thickness = timberThickness,
                        girth = timberGirth,
                        quantity = timberQuantity,
                        unitPrice = timberUnitPrice,
                        calculatedCft = viewModel.calculateSingleTimberCft(),
                        batchList = timberBatch,
                        hasMemory = hasMemory,
                        memoryValue = memoryValue,
                        onMemoryOp = viewModel::onMemoryOperation,
                        onSelectType = viewModel::setTimberType,
                        onSelectField = viewModel::setActiveTimberField,
                        onKeyInput = viewModel::onTimberKeyInput,
                        onAddBatchItem = viewModel::addCurrentToTimberBatch,
                        onRemoveBatchItem = viewModel::removeTimberBatchItem,
                        onClearBatch = viewModel::clearTimberBatch,
                        onOpenHistory = { viewModel.toggleHistorySheet(false) }
                    )
                }

                // Stored Calculation History Sheet
                if (showHistorySheet) {
                    HistoryBottomSheet(
                        historyList = historyList,
                        onSelectHistoryItem = viewModel::selectHistoryItem,
                        onDeleteHistoryItem = viewModel::deleteHistoryItem,
                        onClearAllHistory = viewModel::clearAllHistory,
                        onDismiss = { viewModel.toggleHistorySheet(false) }
                    )
                }
            }
        }
    }
}

