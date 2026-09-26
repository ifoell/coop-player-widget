package com.example.coopwidget.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coopwidget.domain.model.DefaultAccentColors
import com.example.coopwidget.domain.model.PartyData
import com.example.coopwidget.domain.model.Player
import com.example.coopwidget.domain.model.Stat
import com.example.coopwidget.domain.usecase.AgeCalculator
import com.example.coopwidget.ui.player.AddEditPlayerDialog
import com.example.coopwidget.ui.settings.PartySettingsDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyHomeScreen(
    partyData: PartyData,
    onAddPlayer: (Player) -> Unit,
    onUpdatePlayer: (Player) -> Unit,
    onDeletePlayer: (String) -> Unit,
    onReorderPlayers: (List<Player>) -> Unit,
    onUpdateSettings: (familyName: String, subTitle: String, footerText: String, showFooter: Boolean, showStats: Boolean) -> Unit
) {
    var showAddPlayerDialog by remember { mutableStateOf(false) }
    var playerToEdit by remember { mutableStateOf<Player?>(null) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showWidgetPreviewDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(20.dp)
                                .background(Color(0xFF2196F3))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = partyData.familyName.uppercase(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF5F7FA)
                            )
                            Text(
                                text = partyData.subTitle,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showWidgetPreviewDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Preview,
                            contentDescription = "Widget Preview",
                            tint = Color(0xFF2196F3)
                        )
                    }
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFF9CA3AF)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF090D16))
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddPlayerDialog = true },
                containerColor = Color(0xFF2196F3),
                contentColor = Color.Black,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("ADD PLAYER", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = Color(0xFF090D16)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            if (partyData.players.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color(0xFF334155)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "NO PLAYERS IN PARTY",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9CA3AF)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap + ADD PLAYER below to create your CO-OP family party!",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    itemsIndexed(
                        items = partyData.players,
                        key = { _, player -> player.id }
                    ) { index, player ->
                        PlayerCardItem(
                            player = player,
                            pNumber = "P${index + 1}",
                            defaultAccent = DefaultAccentColors.getColorForIndex(index),
                            isFirst = index == 0,
                            isLast = index == partyData.players.size - 1,
                            onEdit = { playerToEdit = player },
                            onDelete = { onDeletePlayer(player.id) },
                            onMoveUp = {
                                if (index > 0) {
                                    val mutable = partyData.players.toMutableList()
                                    val item = mutable.removeAt(index)
                                    mutable.add(index - 1, item)
                                    onReorderPlayers(mutable)
                                }
                            },
                            onMoveDown = {
                                if (index < partyData.players.size - 1) {
                                    val mutable = partyData.players.toMutableList()
                                    val item = mutable.removeAt(index)
                                    mutable.add(index + 1, item)
                                    onReorderPlayers(mutable)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialogs
    if (showAddPlayerDialog) {
        AddEditPlayerDialog(
            playerToEdit = null,
            onDismiss = { showAddPlayerDialog = false },
            onSave = { newPlayer ->
                onAddPlayer(newPlayer)
                showAddPlayerDialog = false
            }
        )
    }

    if (playerToEdit != null) {
        AddEditPlayerDialog(
            playerToEdit = playerToEdit,
            onDismiss = { playerToEdit = null },
            onSave = { updatedPlayer ->
                onUpdatePlayer(updatedPlayer)
                playerToEdit = null
            }
        )
    }

    if (showSettingsDialog) {
        PartySettingsDialog(
            currentData = partyData,
            onDismiss = { showSettingsDialog = false },
            onSave = { familyName, subTitle, footerText, showFooter, showStats ->
                onUpdateSettings(familyName, subTitle, footerText, showFooter, showStats)
                showSettingsDialog = false
            }
        )
    }

    if (showWidgetPreviewDialog) {
        WidgetPreviewDialog(
            partyData = partyData,
            onDismiss = { showWidgetPreviewDialog = false }
        )
    }
}

@Composable
private fun PlayerCardItem(
    player: Player,
    pNumber: String,
    defaultAccent: String,
    isFirst: Boolean,
    isLast: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    val accentColor = parseColor(player.accentColorHex, defaultAccent)
    val computedLevel = AgeCalculator.calculateLevel(player.birthDate)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Player Title Line
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // P Tag Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentColor)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = pNumber,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${player.name.uppercase()} - ${player.role.uppercase()}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF5F7FA)
                        )
                    }
                    Text(
                        text = "Born: ${player.birthDate}",
                        fontSize = 11.sp,
                        color = Color(0xFF6B7280)
                    )
                }

                // LV Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF1E293B))
                        .border(1.dp, accentColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "LV. $computedLevel",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stat bars
            if (player.stats.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    player.stats.forEach { stat ->
                        StatBarItem(stat = stat, accentColor = accentColor)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            HorizontalDivider(color = Color(0xFF1E293B))

            // Action Buttons Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reorder controls
                Row {
                    IconButton(
                        onClick = onMoveUp,
                        enabled = !isFirst,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Move Up",
                            tint = if (!isFirst) Color(0xFF9CA3AF) else Color(0xFF334155)
                        )
                    }
                    IconButton(
                        onClick = onMoveDown,
                        enabled = !isLast,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Move Down",
                            tint = if (!isLast) Color(0xFF9CA3AF) else Color(0xFF334155)
                        )
                    }
                }

                // Edit / Delete buttons
                Row {
                    TextButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF2196F3))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("EDIT", fontSize = 12.sp, color = Color(0xFF2196F3))
                    }
                    TextButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFFF4F7B))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("DELETE", fontSize = 12.sp, color = Color(0xFFFF4F7B))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBarItem(stat: Stat, accentColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stat.name.take(6).uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9CA3AF),
            modifier = Modifier.width(50.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        LinearProgressIndicator(
            progress = { (stat.value / 100f).coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = accentColor,
            trackColor = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${stat.value}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF5F7FA),
            modifier = Modifier.width(28.dp)
        )
    }
}

@Composable
private fun WidgetPreviewDialog(partyData: PartyData, onDismiss: () -> Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF090D16)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WIDGET PREVIEW",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF5F7FA)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF9CA3AF))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Row of slim vertical player widgets (P1, P2, P3...)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val previewCount = partyData.players.size.coerceAtLeast(3)
                    for (idx in 0 until previewCount.coerceAtMost(3)) {
                        val p = partyData.players.getOrNull(idx)
                        val accent = parseColor(p?.accentColorHex ?: DefaultAccentColors.getColorForIndex(idx))
                        val pLevel = if (p != null) AgeCalculator.calculateLevel(p.birthDate) else 0

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xEE090D16))
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "P${idx + 1}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = accent
                                )
                                Text(
                                    text = p?.role?.uppercase() ?: "NO PLAYER",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF5F7FA)
                                )
                                Text(
                                    text = "LV. $pLevel",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFCBD5E1)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                if (p != null && p.stats.isNotEmpty()) {
                                    p.stats.take(5).forEach { stat ->
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = stat.name.take(3).uppercase(),
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFCBD5E1),
                                                modifier = Modifier.width(22.dp)
                                            )
                                            LinearProgressIndicator(
                                                progress = { (stat.value / 100f).coerceIn(0f, 1f) },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(4.dp)
                                                    .clip(RoundedCornerShape(2.dp)),
                                                color = accent,
                                                trackColor = Color(0xFF1E293B)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                HorizontalDivider(color = Color(0xFF475569))
                                Spacer(modifier = Modifier.height(4.dp))

                                val taglineText = if (p != null && p.tagline.isNotBlank()) {
                                    p.tagline.replace("•", "\n").replace("-", "\n")
                                } else if (p != null) {
                                    com.example.coopwidget.domain.model.DefaultRoles.getDefaultTaglineForRole(p.role).replace("•", "\n").replace("-", "\n")
                                } else {
                                    "TAP TO\nCONFIGURE"
                                }

                                Text(
                                    text = taglineText.uppercase(),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8),
                                    lineHeight = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun parseColor(hex: String, defaultHex: String = "#2196F3"): Color {
    return try {
        val clean = if (hex.startsWith("#")) hex.substring(1) else hex
        val colorInt = clean.toLong(16).toInt()
        val full = if (clean.length == 6) colorInt or 0xFF000000.toInt() else colorInt
        Color(full)
    } catch (e: Exception) {
        try {
            val cleanDefault = if (defaultHex.startsWith("#")) defaultHex.substring(1) else defaultHex
            Color(cleanDefault.toLong(16).toInt() or 0xFF000000.toInt())
        } catch (e2: Exception) {
            Color(0xFF2196F3)
        }
    }
}
