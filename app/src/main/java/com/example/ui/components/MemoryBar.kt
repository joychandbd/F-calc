package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MemoryOp
import com.example.ui.theme.AccentOrange

@Composable
fun MemoryBar(
    hasMemory: Boolean,
    onMemoryOp: (MemoryOp) -> Unit,
    modifier: Modifier = Modifier
) {
    val memKeys = listOf(
        "MC" to MemoryOp.CLEAR,
        "MR" to MemoryOp.RECALL,
        "M+" to MemoryOp.ADD,
        "M-" to MemoryOp.SUBTRACT,
        "MS" to MemoryOp.STORE
    )
    val buttonBg = MaterialTheme.colorScheme.secondaryContainer
    val buttonText = MaterialTheme.colorScheme.onSurface
    val activeText = AccentOrange

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        memKeys.forEach { (label, op) ->
            val isEnabled = when (op) {
                MemoryOp.CLEAR, MemoryOp.RECALL -> hasMemory
                else -> true
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isEnabled) buttonBg else buttonBg.copy(alpha = 0.4f))
                    .clickable(enabled = isEnabled) {
                        onMemoryOp(op)
                    }
                    .testTag("memory_key_$label"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (hasMemory && (op == MemoryOp.RECALL || op == MemoryOp.CLEAR)) activeText else if (isEnabled) buttonText else buttonText.copy(alpha = 0.4f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
