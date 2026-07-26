package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
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
import com.example.ui.MemoryOp
import com.example.ui.theme.MemoryButtonBg
import com.example.ui.theme.TimberGold
import com.example.ui.theme.TimberWood

@Composable
fun MemoryBar(
    hasMemory: Boolean,
    onMemoryOp: (MemoryOp) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val memoryKeys = listOf(
            "MC" to MemoryOp.CLEAR,
            "MR" to MemoryOp.RECALL,
            "M+" to MemoryOp.ADD,
            "M-" to MemoryOp.SUBTRACT,
            "MS" to MemoryOp.STORE
        )

        val memoryActiveColor = TimberGold
        val memoryInactiveColor = TimberWood.copy(alpha = 0.7f)

        memoryKeys.forEach { (label, op) ->
            CalcButton(
                text = label,
                onClick = { onMemoryOp(op) },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                backgroundColor = MemoryButtonBg,
                contentColor = if (hasMemory) memoryActiveColor else memoryInactiveColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                shapeRadius = 12.dp,
                testTag = "memory_${label.lowercase()}_button"
            )
        }
    }
}
