package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AppDrawerContent
import com.example.ui.components.DisplayPanel
import com.example.ui.components.HistoryBottomSheet
import com.example.ui.components.KeypadGrid
import com.example.ui.components.MemoryBar
import com.example.ui.components.TimberCftCalculatorContent
import kotlinx.coroutines.launch

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val isRoundButtons by viewModel.isRoundButtons.collectAsStateWithLifecycle()
    val showFeetInches by viewModel.showFeetInchesInDisplay.collectAsStateWithLifecycle()

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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawerContent(
                currentMode = currentScreen,
                currentTimberType = timberType,
                onSelectMode = viewModel::selectAppMode,
                onSelectTimberType = viewModel::setTimberType,
                onOpenHistory = { viewModel.toggleHistorySheet(true) },
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                if (currentScreen == CalculatorAppMode.STANDARD) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 6.dp)
                    ) {
                        // Display Panel
                        DisplayPanel(
                            expressionValue = expressionValue,
                            onExpressionValueChange = viewModel::onExpressionValueChange,
                            liveResult = liveResult,
                            hasMemory = hasMemory,
                            memoryValue = memoryValue,
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onOpenHistory = { viewModel.toggleHistorySheet(true) },
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Memory Key Row
                        MemoryBar(
                            hasMemory = hasMemory,
                            onMemoryOp = viewModel::onMemoryOperation,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(4.dp))

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
                        onSelectType = viewModel::setTimberType,
                        onSelectField = viewModel::setActiveTimberField,
                        onKeyInput = viewModel::onTimberKeyInput,
                        onAddBatchItem = viewModel::addCurrentToTimberBatch,
                        onRemoveBatchItem = viewModel::removeTimberBatchItem,
                        onClearBatch = viewModel::clearTimberBatch,
                        onOpenDrawer = { scope.launch { drawerState.open() } }
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
