package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NumberButtonBg
import com.example.ui.theme.OperatorButtonBg
import com.example.ui.theme.TimberAmber
import com.example.ui.theme.TimberGold
import com.example.ui.theme.TimberWood

@Composable
fun KeypadGrid(
    onDigitClick: (String) -> Unit,
    onOperatorClick: (String) -> Unit,
    onBracketClick: (String) -> Unit,
    onFractionClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onClearClick: () -> Unit,
    onEqualsClick: () -> Unit,
    showQuickFractions: Boolean,
    modifier: Modifier = Modifier
) {
    val buttonHeight = 62.dp

    val numberBg = NumberButtonBg
    val numberText = MaterialTheme.colorScheme.onSurface

    val opBg = OperatorButtonBg
    val opText = TimberGold

    val actionBg = OperatorButtonBg
    val actionText = TimberWood

    val equalBg = TimberAmber
    val equalText = MaterialTheme.colorScheme.onPrimary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        // Quick Fraction Tape Row
        AnimatedVisibility(
            visible = showQuickFractions,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val fractions = listOf("1/16", "1/8", "1/4", "1/2", "3/4")
                fractions.forEach { frac ->
                    CalcButton(
                        text = frac,
                        onClick = { onFractionClick(frac) },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        backgroundColor = OperatorButtonBg,
                        contentColor = TimberWood,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        shapeRadius = 10.dp,
                        testTag = "fraction_${frac.replace("/", "_")}_button"
                    )
                }
            }
        }

        // Row 1: C, (, ), ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalcButton(
                text = "C",
                onClick = onClearClick,
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = actionBg,
                contentColor = MaterialTheme.colorScheme.error,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                testTag = "clear_button"
            )
            CalcButton(
                text = "(",
                onClick = { onBracketClick("(") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = actionBg,
                contentColor = actionText,
                fontSize = 20.sp,
                testTag = "left_bracket_button"
            )
            CalcButton(
                text = ")",
                onClick = { onBracketClick(")") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = actionBg,
                contentColor = actionText,
                fontSize = 20.sp,
                testTag = "right_bracket_button"
            )
            CalcButton(
                text = "÷",
                onClick = { onOperatorClick("÷") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = opBg,
                contentColor = opText,
                fontSize = 26.sp,
                testTag = "divide_button"
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalcButton(
                text = "7",
                onClick = { onDigitClick("7") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                testTag = "num_7_button"
            )
            CalcButton(
                text = "8",
                onClick = { onDigitClick("8") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                testTag = "num_8_button"
            )
            CalcButton(
                text = "9",
                onClick = { onDigitClick("9") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                testTag = "num_9_button"
            )
            CalcButton(
                text = "×",
                onClick = { onOperatorClick("×") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = opBg,
                contentColor = opText,
                fontSize = 26.sp,
                testTag = "multiply_button"
            )
        }

        // Row 3: 4, 5, 6, -
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalcButton(
                text = "4",
                onClick = { onDigitClick("4") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                testTag = "num_4_button"
            )
            CalcButton(
                text = "5",
                onClick = { onDigitClick("5") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                testTag = "num_5_button"
            )
            CalcButton(
                text = "6",
                onClick = { onDigitClick("6") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                testTag = "num_6_button"
            )
            CalcButton(
                text = "−",
                onClick = { onOperatorClick("-") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = opBg,
                contentColor = opText,
                fontSize = 26.sp,
                testTag = "minus_button"
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalcButton(
                text = "1",
                onClick = { onDigitClick("1") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                testTag = "num_1_button"
            )
            CalcButton(
                text = "2",
                onClick = { onDigitClick("2") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                testTag = "num_2_button"
            )
            CalcButton(
                text = "3",
                onClick = { onDigitClick("3") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                testTag = "num_3_button"
            )
            CalcButton(
                text = "+",
                onClick = { onOperatorClick("+") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = opBg,
                contentColor = opText,
                fontSize = 26.sp,
                testTag = "plus_button"
            )
        }

        // Row 5: 0, ., ⌫, =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalcButton(
                text = "0",
                onClick = { onDigitClick("0") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                testTag = "num_0_button"
            )
            CalcButton(
                text = ".",
                onClick = { onDigitClick(".") },
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = numberBg,
                contentColor = numberText,
                fontSize = 24.sp,
                testTag = "decimal_button"
            )
            CalcButton(
                text = "⌫",
                onClick = onBackspaceClick,
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = actionBg,
                contentColor = actionText,
                fontSize = 20.sp,
                testTag = "backspace_button"
            )
            CalcButton(
                text = "=",
                onClick = onEqualsClick,
                modifier = Modifier.weight(1f).height(buttonHeight),
                backgroundColor = equalBg,
                contentColor = equalText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                testTag = "equals_button"
            )
        }
    }
}
