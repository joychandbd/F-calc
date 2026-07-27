package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoogleActionBg
import com.example.ui.theme.GoogleActionText
import com.example.ui.theme.GoogleEqualsBg
import com.example.ui.theme.GoogleEqualsText
import com.example.ui.theme.GoogleNumBg
import com.example.ui.theme.GoogleNumText

@Composable
fun KeypadGrid(
    onDigitClick: (String) -> Unit,
    onOperatorClick: (String) -> Unit,
    onBracketClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onClearClick: () -> Unit,
    onEqualsClick: () -> Unit,
    shapeRadius: Dp = 50.dp,
    modifier: Modifier = Modifier
) {
    val buttonHeight = 68.dp

    val numberBg = GoogleNumBg
    val numberText = GoogleNumText

    val actionBg = GoogleActionBg
    val actionText = GoogleActionText

    val equalBg = GoogleEqualsBg
    val equalText = GoogleEqualsText

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: AC, ( ), %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(
                text = "AC",
                onClick = onClearClick,
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = actionBg,
                contentColor = actionText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                shapeRadius = shapeRadius,
                testTag = "clear_button"
            )
            CalcButton(
                text = "( )",
                onClick = { onBracketClick("AUTO") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = actionBg,
                contentColor = actionText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                shapeRadius = shapeRadius,
                testTag = "bracket_button"
            )
            CalcButton(
                text = "%",
                onClick = { onOperatorClick("%") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = actionBg,
                contentColor = actionText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                shapeRadius = shapeRadius,
                testTag = "percent_button"
            )
            CalcButton(
                text = "÷",
                onClick = { onOperatorClick("÷") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = actionBg,
                contentColor = actionText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                shapeRadius = shapeRadius,
                testTag = "divide_button"
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(
                text = "7",
                onClick = { onDigitClick("7") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                shapeRadius = shapeRadius,
                testTag = "num_7_button"
            )
            CalcButton(
                text = "8",
                onClick = { onDigitClick("8") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                shapeRadius = shapeRadius,
                testTag = "num_8_button"
            )
            CalcButton(
                text = "9",
                onClick = { onDigitClick("9") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                shapeRadius = shapeRadius,
                testTag = "num_9_button"
            )
            CalcButton(
                text = "×",
                onClick = { onOperatorClick("×") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = actionBg,
                contentColor = actionText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                shapeRadius = shapeRadius,
                testTag = "multiply_button"
            )
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(
                text = "4",
                onClick = { onDigitClick("4") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                shapeRadius = shapeRadius,
                testTag = "num_4_button"
            )
            CalcButton(
                text = "5",
                onClick = { onDigitClick("5") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                shapeRadius = shapeRadius,
                testTag = "num_5_button"
            )
            CalcButton(
                text = "6",
                onClick = { onDigitClick("6") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                shapeRadius = shapeRadius,
                testTag = "num_6_button"
            )
            CalcButton(
                text = "−",
                onClick = { onOperatorClick("-") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = actionBg,
                contentColor = actionText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                shapeRadius = shapeRadius,
                testTag = "minus_button"
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(
                text = "1",
                onClick = { onDigitClick("1") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                shapeRadius = shapeRadius,
                testTag = "num_1_button"
            )
            CalcButton(
                text = "2",
                onClick = { onDigitClick("2") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                shapeRadius = shapeRadius,
                testTag = "num_2_button"
            )
            CalcButton(
                text = "3",
                onClick = { onDigitClick("3") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                shapeRadius = shapeRadius,
                testTag = "num_3_button"
            )
            CalcButton(
                text = "+",
                onClick = { onOperatorClick("+") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = actionBg,
                contentColor = actionText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                shapeRadius = shapeRadius,
                testTag = "plus_button"
            )
        }

        // Row 5: 0, ., ⌫, =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(
                text = "0",
                onClick = { onDigitClick("0") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                fontSize = 30.sp,
                fontWeight = FontWeight.SemiBold,
                shapeRadius = shapeRadius,
                testTag = "num_0_button"
            )
            CalcButton(
                text = ".",
                onClick = { onDigitClick(".") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                shapeRadius = shapeRadius,
                testTag = "decimal_button"
            )
            CalcButton(
                text = "⌫",
                onClick = onBackspaceClick,
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                shapeRadius = shapeRadius,
                testTag = "backspace_button"
            )
            CalcButton(
                text = "=",
                onClick = onEqualsClick,
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = equalBg,
                contentColor = equalText,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                shapeRadius = shapeRadius,
                testTag = "equals_button"
            )
        }
    }
}
