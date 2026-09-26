package com.example.coopwidget.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.appwidget.updateAll
import com.example.coopwidget.data.datastore.PartyDataStore
import com.example.coopwidget.data.datastore.WidgetBackgroundStyle
import com.example.coopwidget.data.datastore.WidgetConfigStore
import com.example.coopwidget.domain.model.DefaultAccentColors
import com.example.coopwidget.domain.model.DefaultRoles
import com.example.coopwidget.domain.model.PartyData
import com.example.coopwidget.domain.model.Player
import com.example.coopwidget.domain.usecase.AgeCalculator
import com.example.coopwidget.theme.CoOpWidgetTheme
import kotlinx.coroutines.launch

class WidgetConfigActivity : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setResult(Activity.RESULT_CANCELED)

        val intentExtras = intent.extras
        if (intentExtras != null) {
            appWidgetId = intentExtras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        val dataStore = PartyDataStore(applicationContext)
        val configStore = WidgetConfigStore(applicationContext)

        setContent {
            CoOpWidgetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF090D16)
                ) {
                    val partyData by dataStore.partyDataFlow.collectAsState(initial = PartyData())
                    val scope = rememberCoroutineScope()

                    WidgetConfigScreen(
                        partyData = partyData,
                        appWidgetId = appWidgetId,
                        onSaveWidgetConfig = { selectedPlayer, bgStyle, showTagline ->
                            scope.launch {
                                configStore.saveWidgetConfig(
                                    appWidgetId = appWidgetId,
                                    playerId = selectedPlayer.id,
                                    bgStyle = bgStyle,
                                    showTagline = showTagline
                                )
                                SinglePlayerWidget().updateAll(applicationContext)

                                val resultValue = Intent().apply {
                                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                                }
                                setResult(Activity.RESULT_OK, resultValue)
                                finish()
                            }
                        },
                        onCancel = {
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WidgetConfigScreen(
    partyData: PartyData,
    appWidgetId: Int,
    onSaveWidgetConfig: (Player, WidgetBackgroundStyle, Boolean) -> Unit,
    onCancel: () -> Unit
) {
    var selectedPlayerIndex by remember { mutableStateOf(0) }
    var selectedBgStyle by remember { mutableStateOf(WidgetBackgroundStyle.TRANSLUCENT) }
    var showTagline by remember { mutableStateOf(true) }

    val selectedPlayer = partyData.players.getOrNull(selectedPlayerIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "CONFIGURE SLIM WIDGET",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF5F7FA)
                        )
                        Text(
                            text = "Widget ID: #$appWidgetId",
                            fontSize = 11.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color(0xFF9CA3AF))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF090D16))
            )
        },
        bottomBar = {
            if (selectedPlayer != null) {
                Surface(
                    color = Color(0xFF111827),
                    tonalElevation = 8.dp
                ) {
                    Button(
                        onClick = {
                            onSaveWidgetConfig(selectedPlayer, selectedBgStyle, showTagline)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("APPLY WIDGET CONFIGURATION", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                    }
                }
            }
        },
        containerColor = Color(0xFF090D16)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            if (partyData.players.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No players found in party.\nPlease open app and create players first!",
                        color = Color(0xFF9CA3AF),
                        fontSize = 14.sp
                    )
                }
            } else {
                // Section 1: Select Player
                Text("1. SELECT PLAYER", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    partyData.players.forEachIndexed { index, player ->
                        val isSelected = index == selectedPlayerIndex
                        val pTag = "P${index + 1}"
                        val accent = parseColor(player.accentColorHex, DefaultAccentColors.getColorForIndex(index))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF2196F3) else Color(0xFF334155),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedPlayerIndex = index },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF111827))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(accent)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(pTag, fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Black)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "${player.name.uppercase()} - ${player.role.uppercase()}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF5F7FA)
                                    )
                                }

                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF2196F3))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Background Style
                Text("2. WIDGET BACKGROUND STYLE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                Spacer(modifier = Modifier.height(6.dp))

                val styles = listOf(
                    WidgetBackgroundStyle.TRANSLUCENT to "🌙 Translucent HUD (Glass)",
                    WidgetBackgroundStyle.TRANSPARENT to "🌌 Transparent (Wallpaper Overlay)",
                    WidgetBackgroundStyle.SOLID_DARK to "⬛ Solid Dark Navy",
                    WidgetBackgroundStyle.ACCENT_TINT to "🎨 Player Accent Tint"
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    styles.forEach { (styleOption, label) ->
                        val isSelected = selectedBgStyle == styleOption
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF2196F3) else Color(0xFF334155),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedBgStyle = styleOption },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(label, fontSize = 13.sp, color = Color(0xFFF5F7FA), fontWeight = FontWeight.Medium)
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedBgStyle = styleOption },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2196F3))
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Show Tagline Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Show Tagline Footer", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF5F7FA))
                        Text("Display tagline text at bottom of widget", fontSize = 11.sp, color = Color(0xFF9CA3AF))
                    }
                    Switch(
                        checked = showTagline,
                        onCheckedChange = { showTagline = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF2196F3))
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 4: Live Preview
                Text("3. LIVE PREVIEW", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF9CA3AF))
                Spacer(modifier = Modifier.height(6.dp))

                if (selectedPlayer != null) {
                    val pTag = "P${selectedPlayerIndex + 1}"
                    val accent = parseColor(selectedPlayer.accentColorHex, DefaultAccentColors.getColorForIndex(selectedPlayerIndex))
                    val level = AgeCalculator.calculateLevel(selectedPlayer.birthDate)

                    val previewBg = when (selectedBgStyle) {
                        WidgetBackgroundStyle.TRANSPARENT -> Color(0x11000000)
                        WidgetBackgroundStyle.TRANSLUCENT -> Color(0xEE090D16)
                        WidgetBackgroundStyle.SOLID_DARK -> Color(0xFF090D16)
                        WidgetBackgroundStyle.ACCENT_TINT -> {
                            val accentInt = parseColorInt(selectedPlayer.accentColorHex)
                            Color((accentInt and 0x00FFFFFF) or 0x33000000)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(previewBg)
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(pTag, fontSize = 24.sp, fontWeight = FontWeight.Black, color = accent)
                            Text("${selectedPlayer.name.uppercase()} - ${selectedPlayer.role.uppercase()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF5F7FA))
                            Text("LV. $level", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFCBD5E1))

                            Spacer(modifier = Modifier.height(6.dp))

                            selectedPlayer.stats.take(5).forEach { stat ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 1.dp)
                                ) {
                                    Text(stat.name.take(3).uppercase(), fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFFCBD5E1), modifier = Modifier.width(22.dp))
                                    LinearProgressIndicator(
                                        progress = { (stat.value / 100f).coerceIn(0f, 1f) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(5.dp)
                                            .clip(RoundedCornerShape(2.dp)),
                                        color = accent,
                                        trackColor = Color(0xFF1E293B)
                                    )
                                }
                            }

                            if (showTagline) {
                                Spacer(modifier = Modifier.height(6.dp))
                                HorizontalDivider(color = Color(0xFF475569))
                                Spacer(modifier = Modifier.height(4.dp))

                                val taglineText = if (selectedPlayer.tagline.isNotBlank()) {
                                    selectedPlayer.tagline.replace("•", "\n").replace("-", "\n")
                                } else {
                                    DefaultRoles.getDefaultTaglineForRole(selectedPlayer.role).replace("•", "\n").replace("-", "\n")
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

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

private fun parseColor(hex: String, defaultHex: String = "#2196F3"): Color {
    return Color(parseColorInt(hex, defaultHex))
}

private fun parseColorInt(hex: String, defaultHex: String = "#2196F3"): Int {
    return try {
        val clean = if (hex.startsWith("#")) hex.substring(1) else hex
        val colorInt = clean.toLong(16).toInt()
        if (clean.length == 6) colorInt or 0xFF000000.toInt() else colorInt
    } catch (e: Exception) {
        try {
            val cleanDefault = if (defaultHex.startsWith("#")) defaultHex.substring(1) else defaultHex
            cleanDefault.toLong(16).toInt() or 0xFF000000.toInt()
        } catch (e2: Exception) {
            0xFF2196F3.toInt()
        }
    }
}
