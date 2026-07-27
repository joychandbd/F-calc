package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CalculatorAppMode
import com.example.ui.TimberType
import com.example.ui.theme.AccentOrange

@Composable
fun AppDrawerContent(
    currentMode: CalculatorAppMode,
    currentTimberType: TimberType,
    onSelectMode: (CalculatorAppMode) -> Unit,
    onSelectTimberType: (TimberType) -> Unit,
    onOpenHistory: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    var cftInputValue by remember { mutableStateOf("") }
    val cftVal = cftInputValue.toDoubleOrNull() ?: 0.0
    val cbmVal = cftVal / 35.315
    val tonVal = cftVal / 50.0

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerContentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.width(310.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // App Header
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SquareFoot,
                        contentDescription = "Tool",
                        tint = AccentOrange,
                        modifier = Modifier.size(38.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "প্রদীপ ক্যালকুলেটর",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "কাঠ ও কার্পেন্টার হিসাবের সহযোগী",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section 1: Main Modes
            DrawerSectionHeader("ক্যালকুলেটর মোড (Main Menu)")

            // Standard Calculator
            NavigationDrawerItem(
                label = { Text("সাধারণ ক্যালকুলেটর", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface) },
                icon = { Icon(Icons.Default.Calculate, contentDescription = null, tint = AccentOrange) },
                selected = currentMode == CalculatorAppMode.STANDARD,
                onClick = {
                    onSelectMode(CalculatorAppMode.STANDARD)
                    onCloseDrawer()
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.testTag("drawer_item_standard")
            )

            // Sawn Timber CFT
            NavigationDrawerItem(
                label = { Text("সাইজ কাঠ CFT", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface) },
                icon = { Icon(Icons.Default.SquareFoot, contentDescription = null, tint = AccentOrange) },
                selected = currentMode == CalculatorAppMode.TIMBER_CFT && currentTimberType == TimberType.SAWN_TIMBER,
                onClick = {
                    onSelectTimberType(TimberType.SAWN_TIMBER)
                    onSelectMode(CalculatorAppMode.TIMBER_CFT)
                    onCloseDrawer()
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.testTag("drawer_item_sawn_timber")
            )

            // Round Log CFT
            NavigationDrawerItem(
                label = { Text("গোল কাঠ / গুঁড়ি CFT", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface) },
                icon = { Icon(Icons.Default.Forest, contentDescription = null, tint = AccentOrange) },
                selected = currentMode == CalculatorAppMode.TIMBER_CFT && currentTimberType == TimberType.ROUND_LOG,
                onClick = {
                    onSelectTimberType(TimberType.ROUND_LOG)
                    onSelectMode(CalculatorAppMode.TIMBER_CFT)
                    onCloseDrawer()
                },
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.testTag("drawer_item_round_log")
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

            // Section 2: Wood Unit Converters
            DrawerSectionHeader("কাঠের একক রূপান্তর (Wood Converters)")

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "CFT ➔ CBM ও টন কনভার্টার", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = cftInputValue,
                        onValueChange = { cftInputValue = it },
                        label = { Text("CFT সংখ্যা লিখুন") },
                        placeholder = { Text("যেমন: 100") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentOrange,
                            focusedLabelColor = AccentOrange
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (cftVal > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "ঘনমিটার (CBM): ${String.format("%.3f", cbmVal)} m³",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentOrange
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "কাঠের টন (Tons): ${String.format("%.2f", tonVal)} Ton",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Reference standard units
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "• ১ CBM = ৩৫.৩১৫ CFT", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "• ১ টন কাঠ = ৫০ CFT", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "• ১ ফুট = ১২ ইঞ্চি", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "• ১ ইঞ্চি = ৮ সুতা", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))

            // Section 3: History
            DrawerSectionHeader("রেকর্ড ও ইতিহাস")

            NavigationDrawerItem(
                label = { Text("পূর্বের ইতিহাস দেখুন", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface) },
                icon = { Icon(Icons.Default.History, contentDescription = null, tint = AccentOrange) },
                selected = false,
                onClick = {
                    onCloseDrawer()
                    onOpenHistory()
                },
                modifier = Modifier.testTag("drawer_item_history")
            )
        }
    }
}

@Composable
fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
    )
}
