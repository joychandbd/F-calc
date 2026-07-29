package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.Canvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentOlive
import com.example.ui.theme.AccentOrange

@Composable
fun DisplayPanel(
    expressionValue: TextFieldValue,
    onExpressionValueChange: (TextFieldValue) -> Unit,
    liveResult: String,
    hasMemory: Boolean,
    memoryValue: Double,
    modifier: Modifier = Modifier
) {
    val exprScrollState = rememberScrollState()

    // Scroll to end when text length changes
    LaunchedEffect(expressionValue.text) {
        exprScrollState.animateScrollTo(exprScrollState.maxValue)
    }

    // Blinking cursor transition
    val infiniteTransition = rememberInfiniteTransition(label = "cursor_blink")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        SelectionContainer {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Memory Badge Row
                if (hasMemory) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.onSurface)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "M = ${if (memoryValue % 1.0 == 0.0) memoryValue.toLong() else memoryValue}",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Expression Input Display with BasicTextField for text select, copy, paste, and blinking black cursor
                val rawText = expressionValue.text

                val exprFontSize = when {
                    rawText.length > 30 -> 22.sp
                    rawText.length > 18 -> 26.sp
                    else -> 34.sp
                }
                val exprLineHeight = when {
                    rawText.length > 30 -> 28.sp
                    rawText.length > 18 -> 32.sp
                    else -> 42.sp
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 60.dp, max = 130.dp)
                        .verticalScroll(exprScrollState),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    BasicTextField(
                        value = expressionValue,
                        onValueChange = onExpressionValueChange,
                        readOnly = true,
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = exprFontSize,
                            lineHeight = exprLineHeight,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.End
                        ),
                        cursorBrush = SolidColor(Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("expression_text"),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                if (rawText.isEmpty()) {
                                    Text(
                                        text = "0",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = exprFontSize,
                                        lineHeight = exprLineHeight,
                                        fontWeight = FontWeight.Normal,
                                        textAlign = TextAlign.End,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                if (liveResult.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // Result Display (Adaptive font size, STRICT single line, NO '=' sign)
                    val resultFontSize = when {
                        liveResult.length > 20 -> 18.sp
                        liveResult.length > 15 -> 22.sp
                        liveResult.length > 10 -> 26.sp
                        else -> 32.sp
                    }

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = liveResult,
                            color = Color.Black,
                            fontSize = resultFontSize,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.testTag("primary_result_text")
                        )
                    }
                }
            }
        }
    }
}
