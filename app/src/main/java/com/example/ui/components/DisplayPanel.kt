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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.selection.SelectionContainer
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

                // Expression Input Display with Blinking Overlay Cursor and Touch/Drag Water Drop Handle
                val rawText = expressionValue.text
                val formattedDisplayText = if (rawText.isEmpty()) "0" else rawText

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
                    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
                    var isTouching by remember { mutableStateOf(false) }
                    val cursorPos = expressionValue.selection.start.coerceIn(0, rawText.length)

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {
                        Text(
                            text = formattedDisplayText,
                            color = if (rawText.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                            fontSize = exprFontSize,
                            lineHeight = exprLineHeight,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.End,
                            onTextLayout = { layout ->
                                textLayoutResult = layout
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .pointerInput(rawText) {
                                    awaitEachGesture {
                                        val down = awaitFirstDown(requireUnconsumed = false)
                                        isTouching = true
                                        textLayoutResult?.let { layout ->
                                            try {
                                                val newOffset = layout.getOffsetForPosition(down.position).coerceIn(0, rawText.length)
                                                onExpressionValueChange(expressionValue.copy(selection = TextRange(newOffset)))
                                            } catch (_: Exception) {}
                                        }

                                        while (true) {
                                            val event = awaitPointerEvent()
                                            val change = event.changes.firstOrNull() ?: break
                                            if (change.pressed) {
                                                textLayoutResult?.let { layout ->
                                                    try {
                                                        val newOffset = layout.getOffsetForPosition(change.position).coerceIn(0, rawText.length)
                                                        onExpressionValueChange(expressionValue.copy(selection = TextRange(newOffset)))
                                                    } catch (_: Exception) {}
                                                }
                                            } else {
                                                break
                                            }
                                        }
                                        isTouching = false
                                    }
                                }
                                .testTag("expression_text")
                        )

                        // Overlay Blinking Cursor Line + Water Drop 💧 Handle
                        textLayoutResult?.let { layout ->
                            val cursorRect = try {
                                layout.getCursorRect(cursorPos)
                            } catch (_: Exception) { null }

                            if (cursorRect != null) {
                                val density = LocalDensity.current
                                val cursorLeftDp = with(density) { cursorRect.left.toDp() }
                                val cursorTopDp = with(density) { cursorRect.top.toDp() }
                                val cursorHeightDp = with(density) { cursorRect.height.toDp().coerceIn(20.dp, 40.dp) }

                                // Vertical Cursor Line
                                Box(
                                    modifier = Modifier
                                        .offset(x = cursorLeftDp, y = cursorTopDp)
                                        .width(2.5.dp)
                                        .height(cursorHeightDp)
                                        .alpha(if (isTouching) 1f else cursorAlpha)
                                        .background(Color.Black, RoundedCornerShape(1.dp))
                                )

                                // Water drop 💧 handle
                                if (isTouching) {
                                    Canvas(
                                        modifier = Modifier
                                            .offset(
                                                x = cursorLeftDp - 7.dp,
                                                y = cursorTopDp + cursorHeightDp - 2.dp
                                            )
                                            .size(16.dp, 20.dp)
                                    ) {
                                        val dropPath = Path().apply {
                                            moveTo(size.width / 2f, 0f)
                                            cubicTo(
                                                size.width * 0.95f, size.height * 0.45f,
                                                size.width, size.height,
                                                size.width / 2f, size.height
                                            )
                                            cubicTo(
                                                0f, size.height,
                                                0f, size.height * 0.45f,
                                                size.width / 2f, 0f
                                            )
                                            close()
                                        }
                                        drawPath(dropPath, color = Color.Black)
                                    }
                                }
                            }
                        }
                    }
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
