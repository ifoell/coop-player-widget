package com.example.coopwidget.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.coopwidget.MainActivity
import com.example.coopwidget.data.datastore.PartyDataStore
import com.example.coopwidget.domain.model.DefaultAccentColors
import com.example.coopwidget.domain.model.PartyData
import com.example.coopwidget.domain.model.Player
import com.example.coopwidget.domain.model.Stat
import com.example.coopwidget.domain.usecase.AgeCalculator

class CoOpWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val dataStore = PartyDataStore(context)

        provideContent {
            val partyData by dataStore.partyDataFlow.collectAsState(initial = PartyData())
            val size = LocalSize.current

            val isSmall = size.width < 180.dp || size.height < 150.dp
            val isMedium = !isSmall && size.height < 220.dp

            WidgetContent(
                partyData = partyData,
                isSmall = isSmall,
                isMedium = isMedium,
                context = context
            )
        }
    }
}

@Composable
private fun WidgetContent(
    partyData: PartyData,
    isSmall: Boolean,
    isMedium: Boolean,
    context: Context
) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .padding(8.dp)
            .clickable(actionStartActivity<MainActivity>())
    ) {
        // Outer Translucent Frame
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xEE111827))
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Widget Header
            WidgetHeader(partyData = partyData, isSmall = isSmall)

            Spacer(modifier = GlanceModifier.height(4.dp))

            // Player Cards Layout
            val players = partyData.players
            if (players.isEmpty()) {
                Box(
                    modifier = GlanceModifier.defaultWeight().fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO PLAYERS IN PARTY\nTAP TO ADD",
                        style = TextStyle(
                            color = androidx.glance.unit.ColorProvider(Color(0xFF9CA3AF)),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            } else {
                val maxVisible = when {
                    isSmall -> 1
                    isMedium -> 2
                    else -> 4
                }

                val visiblePlayers = players.take(maxVisible)
                val overflowCount = players.size - visiblePlayers.size

                Column(modifier = GlanceModifier.defaultWeight().fillMaxWidth()) {
                    if (visiblePlayers.size <= 2 || isSmall || isMedium) {
                        // Vertical list for 1 or 2 players
                        visiblePlayers.forEachIndexed { index, player ->
                            PlayerCard(
                                player = player,
                                pNumber = "P${index + 1}",
                                showStats = partyData.showStats && !isSmall,
                                isCompact = isSmall || isMedium,
                                defaultAccent = DefaultAccentColors.getColorForIndex(index)
                            )
                            if (index < visiblePlayers.size - 1) {
                                Spacer(modifier = GlanceModifier.height(4.dp))
                            }
                        }
                    } else {
                        // 2x2 Grid for 3 or 4 players on large widget
                        val row1 = visiblePlayers.take(2)
                        val row2 = visiblePlayers.drop(2).take(2)

                        Row(modifier = GlanceModifier.defaultWeight().fillMaxWidth()) {
                            row1.forEachIndexed { idx, player ->
                                Box(modifier = GlanceModifier.defaultWeight()) {
                                    PlayerCard(
                                        player = player,
                                        pNumber = "P${idx + 1}",
                                        showStats = partyData.showStats,
                                        isCompact = true,
                                        defaultAccent = DefaultAccentColors.getColorForIndex(idx)
                                    )
                                }
                                if (idx < row1.size - 1) Spacer(modifier = GlanceModifier.width(4.dp))
                            }
                        }
                        if (row2.isNotEmpty()) {
                            Spacer(modifier = GlanceModifier.height(4.dp))
                            Row(modifier = GlanceModifier.defaultWeight().fillMaxWidth()) {
                                row2.forEachIndexed { idx, player ->
                                    Box(modifier = GlanceModifier.defaultWeight()) {
                                        PlayerCard(
                                            player = player,
                                            pNumber = "P${idx + 3}",
                                            showStats = partyData.showStats,
                                            isCompact = true,
                                            defaultAccent = DefaultAccentColors.getColorForIndex(idx + 2)
                                        )
                                    }
                                    if (idx < row2.size - 1) Spacer(modifier = GlanceModifier.width(4.dp))
                                }
                            }
                        }
                    }

                    if (overflowCount > 0 && !isSmall) {
                        Spacer(modifier = GlanceModifier.height(2.dp))
                        Text(
                            text = "+$overflowCount MORE PLAYERS",
                            style = TextStyle(
                                color = androidx.glance.unit.ColorProvider(Color(0xFF9CA3AF)),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Widget Footer
            if (partyData.showFooter && !isSmall && !isMedium) {
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = partyData.footerText,
                    style = TextStyle(
                        color = androidx.glance.unit.ColorProvider(Color(0xFF6B7280)),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
private fun WidgetHeader(partyData: PartyData, isSmall: Boolean) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = GlanceModifier
                .width(4.dp)
                .height(14.dp)
                .background(Color(0xFF2196F3))
        ) {}
        Spacer(modifier = GlanceModifier.width(6.dp))
        Text(
            text = partyData.familyName.uppercase(),
            style = TextStyle(
                color = androidx.glance.unit.ColorProvider(Color(0xFFF5F7FA)),
                fontSize = if (isSmall) 11.sp else 13.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = GlanceModifier.defaultWeight())
        Text(
            text = "CO-OP",
            style = TextStyle(
                color = androidx.glance.unit.ColorProvider(Color(0xFF2196F3)),
                fontSize = if (isSmall) 9.sp else 11.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun PlayerCard(
    player: Player,
    pNumber: String,
    showStats: Boolean,
    isCompact: Boolean,
    defaultAccent: String
) {
    val accentColor = parseHexColor(player.accentColorHex, defaultAccent)
    val level = AgeCalculator.calculateLevel(player.birthDate)

    Column(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(Color(0xFF1E293B))
            .padding(6.dp)
    ) {
        // Player Header Row: P1 | NAME | ROLE | LV. 35
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // P Tag
            Box(
                modifier = GlanceModifier
                    .background(accentColor)
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = pNumber,
                    style = TextStyle(
                        color = androidx.glance.unit.ColorProvider(Color.Black),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = GlanceModifier.width(6.dp))
            Text(
                text = player.name.uppercase(),
                style = TextStyle(
                    color = androidx.glance.unit.ColorProvider(Color(0xFFF5F7FA)),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = GlanceModifier.width(4.dp))
            Text(
                text = "• ${player.role}",
                style = TextStyle(
                    color = androidx.glance.unit.ColorProvider(Color(0xFF9CA3AF)),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = GlanceModifier.defaultWeight())
            Text(
                text = "LV. $level",
                style = TextStyle(
                    color = androidx.glance.unit.ColorProvider(accentColor),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        // Stats Progress Bars (if enabled)
        if (showStats && player.stats.isNotEmpty()) {
            Spacer(modifier = GlanceModifier.height(4.dp))
            val displayedStats = if (isCompact) player.stats.take(2) else player.stats.take(4)
            displayedStats.forEach { stat ->
                StatProgressBar(stat = stat, accentColor = accentColor)
                Spacer(modifier = GlanceModifier.height(2.dp))
            }
        }
    }
}

@Composable
private fun StatProgressBar(stat: Stat, accentColor: Color) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stat.name.take(4).uppercase(),
            style = TextStyle(
                color = androidx.glance.unit.ColorProvider(Color(0xFF9CA3AF)),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = GlanceModifier.width(4.dp))

        // Custom Bar Layout using Weights
        val filledWeight = (stat.value.coerceIn(0, 100) / 100f).coerceAtLeast(0.05f)
        val emptyWeight = (1f - filledWeight).coerceAtLeast(0.01f)

        Row(
            modifier = GlanceModifier
                .defaultWeight()
                .height(6.dp)
                .background(Color(0xFF0F172A))
        ) {
            Box(
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxHeight()
                    .background(accentColor)
            ) {}
            if (emptyWeight > 0.05f) {
                Box(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .fillMaxHeight()
                        .background(Color(0xFF334155))
                ) {}
            }
        }

        Spacer(modifier = GlanceModifier.width(4.dp))
        Text(
            text = "${stat.value}",
            style = TextStyle(
                color = androidx.glance.unit.ColorProvider(Color(0xFFE2E8F0)),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

class CoOpWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CoOpWidget()
}

private fun parseHexColor(hex: String, defaultHex: String): Color {
    return try {
        val cleanHex = if (hex.startsWith("#")) hex.substring(1) else hex
        val colorInt = cleanHex.toLong(16).toInt()
        val fullColorInt = if (cleanHex.length == 6) colorInt or 0xFF000000.toInt() else colorInt
        Color(fullColorInt)
    } catch (e: Exception) {
        try {
            val cleanDefault = if (defaultHex.startsWith("#")) defaultHex.substring(1) else defaultHex
            Color(cleanDefault.toLong(16).toInt() or 0xFF000000.toInt())
        } catch (e2: Exception) {
            Color(0xFF2196F3)
        }
    }
}
