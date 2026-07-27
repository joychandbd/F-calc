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
    onOpenDrawer: () -> Unit,
    onOpenHistory: () -> Unit,
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
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("hamburger_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Drawer Menu",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "প্রদীপ ক্যালকুলেটর",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Memory Badge
                    AnimatedVisibility(
                        visible = hasMemory,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            color = AccentOrange.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(AccentOrange)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "M = ${if (memoryValue % 1.0 == 0.0) memoryValue.toLong() else memoryValue}",
                                    color = AccentOrange,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // History Button
                    IconButton(
                        onClick = onOpenHistory,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Expression Input Display with Blinking Overlay Cursor and Touch/Drag Water Drop Handle
            val exprFontSize = when {
                expressionValue.text.length > 30 -> 24.sp
                expressionValue.text.length > 18 -> 28.sp
                else -> 36.sp
            }
            val exprLineHeight = when {
                expressionValue.text.length > 30 -> 30.sp
                expressionValue.text.length > 18 -> 34.sp
                else -> 44.sp
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 60.dp, max = 110.dp)
                    .verticalScroll(exprScrollState),
                contentAlignment = Alignment.BottomEnd
            ) {
                var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
                var isTouching by remember { mutableStateOf(false) }
                val text = expressionValue.text
                val displayText = if (text.isEmpty()) "0" else text
                val cursorPos = expressionValue.selection.start.coerceIn(0, text.length)

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {
                    Text(
                        text = displayText,
                        color = if (text.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        fontSize = exprFontSize,
                        lineHeight = exprLineHeight,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.End,
                        onTextLayout = { layout ->
                            textLayoutResult = layout
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(text) {
                                awaitEachGesture {
                                    val down = awaitFirstDown(requireUnconsumed = false)
                                    isTouching = true
                                    textLayoutResult?.let { layout ->
                                        try {
                                            val newOffset = layout.getOffsetForPosition(down.position).coerceIn(0, text.length)
                                            onExpressionValueChange(expressionValue.copy(selection = TextRange(newOffset)))
                                        } catch (_: Exception) {}
                                    }

                                    while (true) {
                                        val event = awaitPointerEvent()
                                        val change = event.changes.firstOrNull() ?: break
                                        if (change.pressed) {
                                            textLayoutResult?.let { layout ->
                                                try {
                                                    val newOffset = layout.getOffsetForPosition(change.position).coerceIn(0, text.length)
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
                            val cursorHeightDp = with(density) { cursorRect.height.toDp().coerceIn(22.dp, 44.dp) }

                            // Vertical Cursor Line (Overlay - does NOT shift characters)
                            Box(
                                modifier = Modifier
                                    .offset(x = cursorLeftDp, y = cursorTopDp)
                                    .width(2.5.dp)
                                    .height(cursorHeightDp)
                                    .alpha(if (isTouching) 1f else cursorAlpha)
                                    .background(AccentOrange, RoundedCornerShape(1.dp))
                            )

                            // Water drop 💧 handle (shown only when user is touching/holding text)
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
                                    drawPath(dropPath, color = AccentOrange)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Result Display (Google Calc Olive Accent Color with explicit lineHeight and dynamic sizing to prevent overlap)
            val resultFontSize = when {
                liveResult.length > 25 -> 22.sp
                liveResult.length > 15 -> 28.sp
                else -> 34.sp
            }
            val resultLineHeight = when {
                liveResult.length > 25 -> 28.sp
                liveResult.length > 15 -> 34.sp
                else -> 40.sp
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (liveResult.isNotEmpty()) {
                    Text(
                        text = "= $liveResult",
                        color = AccentOlive,
                        fontSize = resultFontSize,
                        lineHeight = resultLineHeight,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        modifier = Modifier.testTag("primary_result_text")
                    )
                }
            }
        }
    }
}
