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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.DisplayPanel
import com.example.ui.components.HistoryBottomSheet
import com.example.ui.components.KeypadGrid
import com.example.ui.components.MemoryBar

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val expression by viewModel.expression.collectAsStateWithLifecycle()
    val liveResult by viewModel.liveResult.collectAsStateWithLifecycle()
    val liveFeetInches by viewModel.liveFeetInches.collectAsStateWithLifecycle()
    val hasMemory by viewModel.hasMemory.collectAsStateWithLifecycle()
    val memoryValue by viewModel.memoryValue.collectAsStateWithLifecycle()
    val isFeetInchesPrimary by viewModel.isFeetInchesPrimary.collectAsStateWithLifecycle()
    val showQuickFractions by viewModel.showQuickFractions.collectAsStateWithLifecycle()
    val showHistorySheet by viewModel.showHistorySheet.collectAsStateWithLifecycle()
    val historyList by viewModel.historyList.collectAsStateWithLifecycle()

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp)
            ) {
                // Top Display Panel
                DisplayPanel(
                    expression = expression,
                    liveResult = liveResult,
                    liveFeetInches = liveFeetInches,
                    hasMemory = hasMemory,
                    memoryValue = memoryValue,
                    isFeetInchesPrimary = isFeetInchesPrimary,
                    onToggleFeetInches = viewModel::toggleFeetInchesPrimary,
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
                    onFractionClick = viewModel::onFractionInput,
                    onBackspaceClick = viewModel::onBackspace,
                    onClearClick = viewModel::onClear,
                    onEqualsClick = viewModel::onEquals,
                    showQuickFractions = showQuickFractions,
                    modifier = Modifier.fillMaxWidth()
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
