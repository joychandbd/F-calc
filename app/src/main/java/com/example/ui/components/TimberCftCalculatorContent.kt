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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ActiveTimberField
import com.example.ui.TimberBatchItem
import com.example.ui.TimberType
import com.example.ui.theme.AccentOrange

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
    onSelectType: (TimberType) -> Unit,
    onSelectField: (ActiveTimberField) -> Unit,
    onKeyInput: (String) -> Unit,
    onAddBatchItem: () -> Unit,
    onRemoveBatchItem: (String) -> Unit,
    onClearBatch: () -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 4.dp)
    ) {
        // Header Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier.testTag("timber_drawer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "প্রদীপ ক্যালকুলেটর",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Timber Type Selector Tabs
        TabRow(
            selectedTabIndex = if (timberType == TimberType.SAWN_TIMBER) 0 else 1,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = AccentOrange
        ) {
            Tab(
                selected = timberType == TimberType.SAWN_TIMBER,
                onClick = { onSelectType(TimberType.SAWN_TIMBER) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SquareFoot, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("সাইজ কাঠ", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            )
            Tab(
                selected = timberType == TimberType.ROUND_LOG,
                onClick = { onSelectType(TimberType.ROUND_LOG) },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Forest, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("গোল কাঠ / গুঁড়ি", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Input Fields Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                if (timberType == TimberType.SAWN_TIMBER) {
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

                Spacer(modifier = Modifier.height(8.dp))

                // Single Item Result & Add Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CFT: ${String.format("%.2f", calculatedCft)}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentOrange
                        )
                        val unitP = unitPrice.toDoubleOrNull() ?: 0.0
                        if (unitP > 0) {
                            Text(
                                text = "দাম: ৳${String.format("%.2f", calculatedCft * unitP)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Button(
                        onClick = onAddBatchItem,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("add_timber_batch_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ যোগ করুন", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Batch List Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val totalCft = batchList.sumOf { it.cft }
                    val totalPrice = batchList.sumOf { it.totalPrice }

                    Text(
                        text = "মোট কাঠ: ${batchList.size} টি",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (batchList.isNotEmpty()) {
                        Text(
                            text = "মোট: ${String.format("%.2f", totalCft)} CFT" +
                                    if (totalPrice > 0) " (৳${String.format("%.2f", totalPrice)})" else "",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentOrange
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

                if (batchList.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "উপরের তথ্য দিয়ে '+ যোগ করুন' বাটনে চাপ দিন",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(batchList, key = { it.id }) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Text(text = item.details, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(
                                    onClick = { onRemoveBatchItem(item.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "সব মুছুন",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable(onClick = onClearBatch)
                                .padding(2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Single Non-Duplicated Keypad for Timber CFT
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
    val borderColor = if (isActive) AccentOrange else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    val bgColor = if (isActive) AccentOrange.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant

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
                color = if (isActive) AccentOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (value.isEmpty()) "0" else value,
                fontSize = 18.sp,
                color = if (isActive) AccentOrange else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CustomizedTimberKeypad(
    onKeyInput: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyHeight = 50.dp

    val numberBg = MaterialTheme.colorScheme.surfaceVariant
    val numberText = MaterialTheme.colorScheme.onSurface

    val actionBg = MaterialTheme.colorScheme.secondaryContainer
    val actionText = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Row 1: 7, 8, 9, C
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            TimberKeyButton("7", { onKeyInput("7") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
            TimberKeyButton("8", { onKeyInput("8") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
            TimberKeyButton("9", { onKeyInput("9") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
            TimberKeyButton("C", { onKeyInput("C") }, Modifier.weight(1f).height(keyHeight), bg = actionBg, textCol = MaterialTheme.colorScheme.error)
        }
        // Row 2: 4, 5, 6, ⌫
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            TimberKeyButton("4", { onKeyInput("4") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
            TimberKeyButton("5", { onKeyInput("5") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
            TimberKeyButton("6", { onKeyInput("6") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
            TimberKeyButton("⌫", { onKeyInput("⌫") }, Modifier.weight(1f).height(keyHeight), bg = actionBg, textCol = actionText)
        }
        // Row 3: 1, 2, 3, .
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            TimberKeyButton("1", { onKeyInput("1") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
            TimberKeyButton("2", { onKeyInput("2") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
            TimberKeyButton("3", { onKeyInput("3") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
            TimberKeyButton(".", { onKeyInput(".") }, Modifier.weight(1f).height(keyHeight), bg = numberBg, textCol = numberText)
        }
        // Row 4: 0, NEXT (Single NEXT button only!)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
            TimberKeyButton("0", { onKeyInput("0") }, Modifier.weight(2f).height(keyHeight), bg = numberBg, textCol = numberText)
            TimberKeyButton("পরের ঘর ➔", { onKeyInput("NEXT") }, Modifier.weight(2f).height(keyHeight), bg = AccentOrange, textCol = Color.White, fontSp = 15.sp)
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
    fontSp: androidx.compose.ui.unit.TextUnit = 20.sp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = fontSp, fontWeight = FontWeight.Bold, color = textCol)
    }
}
