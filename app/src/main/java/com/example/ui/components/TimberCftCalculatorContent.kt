package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ActiveTimberField
import com.example.ui.MemoryOp
import com.example.ui.TimberBatchItem
import com.example.ui.TimberType
import com.example.ui.theme.GoogleActionBg
import com.example.ui.theme.GoogleActionText
import com.example.ui.theme.GoogleEqualsBg
import com.example.ui.theme.GoogleEqualsText
import com.example.ui.theme.GoogleNumBg
import com.example.ui.theme.GoogleNumText

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect

@Composable
fun TimberCftCalculatorContent(
    timberType: TimberType,
    activeField: ActiveTimberField,
    length: String,
    width: String,
    thickness: String,
    girth: String,
    quantity: String,
    unitPrice: String,
    calculatedCft: Double,
    batchList: List<TimberBatchItem>,
    hasMemory: Boolean = false,
    memoryValue: Double = 0.0,
    onMemoryOp: (MemoryOp) -> Unit = {},
    onSelectType: (TimberType) -> Unit,
    onSelectField: (ActiveTimberField) -> Unit,
    onKeyInput: (String) -> Unit,
    onAddBatchItem: () -> Unit = {},
    onRemoveBatchItem: (String) -> Unit = {},
    onClearBatch: () -> Unit = {},
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = if (timberType == TimberType.SAWN_TIMBER) 0 else 1,
        pageCount = { 2 }
    )

    LaunchedEffect(pagerState.currentPage) {
        val targetType = if (pagerState.currentPage == 0) TimberType.SAWN_TIMBER else TimberType.ROUND_LOG
        if (timberType != targetType) {
            onSelectType(targetType)
        }
    }

    LaunchedEffect(timberType) {
        val targetPage = if (timberType == TimberType.SAWN_TIMBER) 0 else 1
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 4.dp)
    ) {
        // Top Header Summary Card (Two lines: total CFT & total price + reset button)
        val displayCft = if (hasMemory) memoryValue else batchList.sumOf { it.cft }
        val uPrice = unitPrice.toDoubleOrNull() ?: 0.0
        val displayPrice = if (hasMemory) {
            if (uPrice > 0.0) memoryValue * uPrice else batchList.sumOf { it.totalPrice }
        } else {
            batchList.sumOf { it.totalPrice }
        }

        val hasBatch = batchList.isNotEmpty() || (hasMemory && memoryValue > 0.0)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .alpha(if (hasBatch) 1f else 0f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "মোট কাঠ: ${String.format("%.2f", displayCft)} cft",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "মোট দাম: ৳${String.format("%.2f", displayPrice)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (hasBatch) {
                    IconButton(
                        onClick = onClearBatch,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("timber_clear_batch_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Clear Batch",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Card containing Headline at TOP + Input Fields + CFT/Price calculation at BOTTOM
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val isSawnPage = page == 0

                val lenVal = length.toDoubleOrNull() ?: 0.0
                val qtyVal = quantity.toIntOrNull() ?: 1
                val unitPVal = unitPrice.toDoubleOrNull() ?: 0.0

                val sawnCftVal = if (isSawnPage) {
                    val wVal = width.toDoubleOrNull() ?: 0.0
                    val thVal = thickness.toDoubleOrNull() ?: 0.0
                    if (lenVal > 0 && wVal > 0 && thVal > 0) (lenVal * wVal * thVal / 144.0) * qtyVal else 0.0
                } else 0.0

                val roundCftVal = if (!isSawnPage) {
                    val gVal = girth.toDoubleOrNull() ?: 0.0
                    if (lenVal > 0 && gVal > 0) (gVal * gVal * lenVal / 2304.0) * qtyVal else 0.0
                } else 0.0

                val pageCft = if (isSawnPage) sawnCftVal else roundCftVal
                val pagePrice = pageCft * unitPVal

                val formattedCftStr = com.example.util.ExpressionEvaluator.formatTimberValue(pageCft)
                val formattedPriceStr = com.example.util.ExpressionEvaluator.formatTimberValue(pagePrice)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // Headline Tag with Icon + Background ABOVE input boxes
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSawnPage) Icons.Default.CropSquare else Icons.Default.Circle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSawnPage) "সাইজ কাঠ" else "গোল কাঠ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (isSawnPage) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                TimberFieldBox(
                                    label = "দৈর্ঘ্য (ফুট)",
                                    value = length,
                                    isActive = activeField == ActiveTimberField.LENGTH,
                                    onClick = { onSelectField(ActiveTimberField.LENGTH) },
                                    modifier = Modifier.weight(1f)
                                )
                                TimberFieldBox(
                                    label = "প্রস্থ (ইঞ্চি)",
                                    value = width,
                                    isActive = activeField == ActiveTimberField.WIDTH,
                                    onClick = { onSelectField(ActiveTimberField.WIDTH) },
                                    modifier = Modifier.weight(1f)
                                )
                                TimberFieldBox(
                                    label = "পুরুত্ব (ইঞ্চি)",
                                    value = thickness,
                                    isActive = activeField == ActiveTimberField.THICKNESS,
                                    onClick = { onSelectField(ActiveTimberField.THICKNESS) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                TimberFieldBox(
                                    label = "দৈর্ঘ্য (ফুট)",
                                    value = length,
                                    isActive = activeField == ActiveTimberField.LENGTH,
                                    onClick = { onSelectField(ActiveTimberField.LENGTH) },
                                    modifier = Modifier.weight(1f)
                                )
                                TimberFieldBox(
                                    label = "বেড় (ইঞ্চি)",
                                    value = girth,
                                    isActive = activeField == ActiveTimberField.GIRTH,
                                    onClick = { onSelectField(ActiveTimberField.GIRTH) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            TimberFieldBox(
                                label = "পরিমাণ (টি)",
                                value = quantity,
                                isActive = activeField == ActiveTimberField.QUANTITY,
                                onClick = { onSelectField(ActiveTimberField.QUANTITY) },
                                modifier = Modifier.weight(1f)
                            )
                            TimberFieldBox(
                                label = "দর / CFT (৳)",
                                value = unitPrice,
                                isActive = activeField == ActiveTimberField.PRICE,
                                onClick = { onSelectField(ActiveTimberField.PRICE) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Results lines: CFT: X and দাম: ৳Y at bottom
                        Text(
                            text = "CFT: $formattedCftStr",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "দাম: ৳$formattedPriceStr",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Indicator dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(2) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .size(if (isSelected) 8.dp else 6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) Color.Black else Color.LightGray)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Keypad anchored at the very bottom
        CustomizedTimberKeypad(
            onKeyInput = onKeyInput,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
        )
    }
}

@Composable
fun TimberFieldBox(
    label: String,
    value: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isActive) Color.Black else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    val bgColor = if (isActive) Color(0xFFE2E2E6) else MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(2.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = if (isActive) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (value.isEmpty()) "0" else value,
                fontSize = 18.sp,
                color = if (isActive) Color.Black else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CustomizedTimberKeypad(
    onKeyInput: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyHeight = 48.dp

    val numberBg = GoogleNumBg
    val numberText = GoogleNumText

    val actionBg = Color(0xFFD6D6DC)
    val actionText = Color.Black

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Row 1: AC, Add, Next (Matching user image drawing)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            TimberKeyButton("AC", { onKeyInput("AC") }, Modifier.weight(1f).height(keyHeight), bg = actionBg, textCol = actionText, testTag = "timber_ac_button")
            TimberKeyButton("Add", { onKeyInput("ADD") }, Modifier.weight(1f).height(keyHeight), bg = actionBg, textCol = actionText, testTag = "timber_add_button")
            TimberKeyButton("Next", { onKeyInput("NEXT") }, Modifier.weight(1f).height(keyHeight), bg = actionBg, textCol = actionText, testTag = "timber_next_button")
        }
        // Row 2: 1, 2, 3
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            TimberKeyButton("1", { onKeyInput("1") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
            TimberKeyButton("2", { onKeyInput("2") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
            TimberKeyButton("3", { onKeyInput("3") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
        }
        // Row 3: 4, 5, 6
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            TimberKeyButton("4", { onKeyInput("4") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
            TimberKeyButton("5", { onKeyInput("5") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
            TimberKeyButton("6", { onKeyInput("6") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
        }
        // Row 4: 7, 8, 9
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            TimberKeyButton("7", { onKeyInput("7") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
            TimberKeyButton("8", { onKeyInput("8") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
            TimberKeyButton("9", { onKeyInput("9") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
        }
        // Row 5: 0, ., X (Backspace matching user image drawing)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            TimberKeyButton("0", { onKeyInput("0") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
            TimberKeyButton(".", { onKeyInput(".") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
            TimberKeyButton("⌫", { onKeyInput("⌫") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText, testTag = "timber_x_button")
        }
    }
}

@Composable
fun TimberKeyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bg: Color = MaterialTheme.colorScheme.surfaceVariant,
    textCol: Color = MaterialTheme.colorScheme.onSurface,
    fontSp: androidx.compose.ui.unit.TextUnit = 22.sp,
    testTag: String? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = fontSp, fontWeight = FontWeight.Bold, color = textCol)
    }
}
