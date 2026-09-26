package com.example.coopwidget.widget

import android.content.Context
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
import androidx.glance.appwidget.GlanceAppWidgetManager
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
import com.example.coopwidget.data.datastore.SingleWidgetConfig
import com.example.coopwidget.data.datastore.WidgetBackgroundStyle
import com.example.coopwidget.data.datastore.WidgetConfigStore
import com.example.coopwidget.domain.model.DefaultAccentColors
import com.example.coopwidget.domain.model.DefaultRoles
import com.example.coopwidget.domain.model.PartyData
import com.example.coopwidget.domain.model.Player
import com.example.coopwidget.domain.model.Stat
import com.example.coopwidget.domain.usecase.AgeCalculator

class SinglePlayerWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val dataStore = PartyDataStore(context)
        val configStore = WidgetConfigStore(context)
        val appWidgetId = try {
            GlanceAppWidgetManager(context).getAppWidgetId(id)
        } catch (e: Exception) {
            -1
        }

        provideContent {
            val partyData by dataStore.partyDataFlow.collectAsState(initial = PartyData())
            val widgetConfig by configStore.getWidgetConfigFlow(appWidgetId).collectAsState(initial = SingleWidgetConfig())

            val playerIndexInList = if (widgetConfig.playerId != null) {
                partyData.players.indexOfFirst { it.id == widgetConfig.playerId }
            } else -1

            val actualIndex = if (playerIndexInList != -1) playerIndexInList else 0
            val targetPlayer = if (playerIndexInList != -1) {
                partyData.players[playerIndexInList]
            } else {
                partyData.players.getOrNull(0)
            }

            SinglePlayerWidgetContent(
                player = targetPlayer,
                playerIndex = actualIndex,
                bgStyle = widgetConfig.bgStyle,
                showTagline = widgetConfig.showTagline
            )
        }
    }
}

class SinglePlayerWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SinglePlayerWidget()
}

@Composable
fun SinglePlayerWidgetContent(
    player: Player?,
    playerIndex: Int,
    bgStyle: WidgetBackgroundStyle = WidgetBackgroundStyle.TRANSLUCENT,
    showTagline: Boolean = true
) {
    val pTag = "P${playerIndex + 1}"
    val defaultAccentHex = DefaultAccentColors.getColorForIndex(playerIndex)
    val accentColor = parseColor(player?.accentColorHex ?: defaultAccentHex, defaultAccentHex)

    val bgColor = when (bgStyle) {
        WidgetBackgroundStyle.TRANSPARENT -> Color(0x11000000)
        WidgetBackgroundStyle.TRANSLUCENT -> Color(0xEE090D16)
        WidgetBackgroundStyle.SOLID_DARK -> Color(0xFF090D16)
        WidgetBackgroundStyle.ACCENT_TINT -> {
            val accentInt = player?.accentColorHex?.let { parseColorInt(it) } ?: parseColorInt(defaultAccentHex)
            Color((accentInt and 0x00FFFFFF) or 0x33000000)
        }
    }

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(bgColor)
            .padding(10.dp)
            .clickable(actionStartActivity<MainActivity>())
    ) {
        if (player == null) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pTag,
                    style = TextStyle(
                        color = androidx.glance.unit.ColorProvider(accentColor),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = "NO PLAYER",
                    style = TextStyle(
                        color = androidx.glance.unit.ColorProvider(Color(0xFF9CA3AF)),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = GlanceModifier.height(2.dp))
                Text(
                    text = "TAP TO CONFIGURE",
                    style = TextStyle(
                        color = androidx.glance.unit.ColorProvider(Color(0xFF6B7280)),
                        fontSize = 9.sp
                    )
                )
            }
        } else {
            val level = AgeCalculator.calculateLevel(player.birthDate)

            Column(
                modifier = GlanceModifier.fillMaxSize()
            ) {
                // Header: P1
                Text(
                    text = pTag,
                    style = TextStyle(
                        color = androidx.glance.unit.ColorProvider(accentColor),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                // Subheader: <player_name> - <role>  (e.g., Syaiful - DAD)
                val displayNameRole = "${player.name.uppercase()} - ${player.role.uppercase()}"
                Text(
                    text = displayNameRole,
                    style = TextStyle(
                        color = androidx.glance.unit.ColorProvider(Color(0xFFF5F7FA)),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                // Level
                Text(
                    text = "LV. $level",
                    style = TextStyle(
                        color = androidx.glance.unit.ColorProvider(Color(0xFFCBD5E1)),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = GlanceModifier.height(8.dp))

                // Stats List
                if (player.stats.isNotEmpty()) {
                    player.stats.take(5).forEach { stat ->
                        SlimStatRow(stat = stat, accentColor = accentColor)
                        Spacer(modifier = GlanceModifier.height(3.dp))
                    }
                }

                if (showTagline) {
                    Spacer(modifier = GlanceModifier.height(6.dp))

                    // Separator Line
                    Box(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF475569))
                    ) {}

                    Spacer(modifier = GlanceModifier.height(6.dp))

                    // Tagline Footer
                    val taglineText = if (player.tagline.isNotBlank()) {
                        player.tagline.replace("•", "\n").replace("-", "\n")
                    } else {
                        DefaultRoles.getDefaultTaglineForRole(player.role).replace("•", "\n").replace("-", "\n")
                    }

                    Text(
                        text = taglineText.uppercase(),
                        style = TextStyle(
                            color = androidx.glance.unit.ColorProvider(Color(0xFF94A3B8)),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SlimStatRow(stat: Stat, accentColor: Color) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stat.name.take(4).uppercase(),
            style = TextStyle(
                color = androidx.glance.unit.ColorProvider(Color(0xFFCBD5E1)),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = GlanceModifier.width(32.dp)
        )
        Spacer(modifier = GlanceModifier.width(4.dp))

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
