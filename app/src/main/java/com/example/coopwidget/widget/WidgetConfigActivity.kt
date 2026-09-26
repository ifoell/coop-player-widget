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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.coopwidget.data.datastore.WidgetConfigStore
import com.example.coopwidget.domain.model.DefaultAccentColors
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

        // Set result to CANCELED by default so if user backs out, Android cancels widget placement
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
                        onSelectPlayer = { selectedPlayer ->
                            scope.launch {
                                configStore.savePlayerIdForWidget(appWidgetId, selectedPlayer.id)
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
    onSelectPlayer: (Player) -> Unit,
    onCancel: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "SELECT PLAYER FOR WIDGET",
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
        containerColor = Color(0xFF090D16)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Choose which family player status to display on this slim widget:",
                fontSize = 13.sp,
                color = Color(0xFF9CA3AF)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (partyData.players.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No players found in party.\nOpen main app to add players!",
                        color = Color(0xFF9CA3AF),
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(
                        items = partyData.players,
                        key = { _, player -> player.id }
                    ) { index, player ->
                        val pTag = "P${index + 1}"
                        val accentColor = parseColor(player.accentColorHex, DefaultAccentColors.getColorForIndex(index))
                        val level = AgeCalculator.calculateLevel(player.birthDate)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                                .clickable { onSelectPlayer(player) },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF111827))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(accentColor)
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = pTag,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.Black
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = player.name.uppercase(),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFF5F7FA)
                                        )
                                        Text(
                                            text = "${player.role} • LV. $level",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = accentColor
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Select",
                                    tint = accentColor
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
