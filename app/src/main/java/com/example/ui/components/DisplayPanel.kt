package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SlateCard
import com.example.ui.theme.TimberAmber
import com.example.ui.theme.TimberGold
import com.example.ui.theme.TimberWood

@Composable
fun DisplayPanel(
    expression: String,
    liveResult: String,
    liveFeetInches: String,
    hasMemory: Boolean,
    memoryValue: Double,
    isFeetInchesPrimary: Boolean,
    onToggleFeetInches: () -> Unit,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val exprScrollState = rememberScrollState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Info Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Memory Badge
                AnimatedVisibility(
                    visible = hasMemory,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        color = TimberAmber.copy(alpha = 0.2f),
                        shape = CircleShape,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(TimberAmber)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "M = ${if (memoryValue % 1.0 == 0.0) memoryValue.toLong() else memoryValue}",
                                color = TimberGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (!hasMemory) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SquareFoot,
                            contentDescription = "Carpenter Tool",
                            tint = TimberWood,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Carpenter Calc",
                            color = TimberWood,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Feet-Inches Toggle Pill
                    Surface(
                        color = if (isFeetInchesPrimary) TimberAmber else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onToggleFeetInches)
                            .testTag("feet_inches_toggle_button")
                    ) {
                        Text(
                            text = if (isFeetInchesPrimary) "FT - IN" else "DECIMAL",
                            color = if (isFeetInchesPrimary) MaterialTheme.colorScheme.onPrimary else TimberWood,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // History Button
                    IconButton(
                        onClick = onOpenHistory,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Calculation History",
                            tint = TimberGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Expression Input Display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(exprScrollState),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = if (expression.isEmpty()) "0" else expression,
                    color = if (expression.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    fontSize = if (expression.length > 18) 24.sp else 32.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.End,
                    modifier = Modifier.testTag("expression_text")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Result Display (Primary & Secondary)
            if (liveResult.isNotEmpty()) {
                val primaryText = if (isFeetInchesPrimary && liveFeetInches.isNotEmpty()) liveFeetInches else liveResult
                val secondaryText = if (isFeetInchesPrimary) liveResult else liveFeetInches

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "= $primaryText",
                        color = TimberGold,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End,
                        modifier = Modifier.testTag("primary_result_text")
                    )

                    if (secondaryText.isNotEmpty() && secondaryText != primaryText) {
                        Text(
                            text = "($secondaryText)",
                            color = TimberWood.copy(alpha = 0.85f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.End,
                            modifier = Modifier.testTag("secondary_result_text")
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}
