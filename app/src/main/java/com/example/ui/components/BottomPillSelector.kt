package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class BottomModeOption {
    CALCULATOR,
    WOODEN
}

@Composable
fun TopPillHeaderBar(
    selectedOption: BottomModeOption,
    onOptionSelected: (BottomModeOption) -> Unit,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp, top = 8.dp, end = 12.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mode Switcher Pill (85% Width)
        Row(
            modifier = Modifier
                .weight(0.85f)
                .height(44.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFFE8E8ED))
                .padding(horizontal = 3.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val options = listOf(
                BottomModeOption.CALCULATOR to "🗓️ Calculator",
                BottomModeOption.WOODEN to "🪵 Wooden"
            )

            options.forEach { (option, label) ->
                val isSelected = selectedOption == option

                val pillBg by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Color.Transparent,
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                    label = "pillBg"
                )

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color.Black else Color(0xFF555555),
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                    label = "textColor"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .then(
                            if (isSelected) Modifier.shadow(2.dp, RoundedCornerShape(22.dp)) else Modifier
                        )
                        .background(pillBg)
                        .clickable { onOptionSelected(option) }
                        .testTag("top_pill_${option.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = textColor,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // History Icon Button (10-15% Width)
        IconButton(
            onClick = onOpenHistory,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFFE8E8ED))
                .testTag("top_history_button")
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "History",
                tint = Color.Black
            )
        }
    }
}

// Backward compatibility alias if needed
@Composable
fun BottomPillSelector(
    selectedOption: BottomModeOption,
    onOptionSelected: (BottomModeOption) -> Unit,
    modifier: Modifier = Modifier
) {
    TopPillHeaderBar(
        selectedOption = selectedOption,
        onOptionSelected = onOptionSelected,
        onOpenHistory = {},
        modifier = modifier
    )
}
