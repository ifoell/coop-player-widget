package com.example.coopwidget.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.widgetConfigDataStore: DataStore<Preferences> by preferencesDataStore(name = "widget_config_prefs")

enum class WidgetBackgroundStyle {
    TRANSLUCENT,
    TRANSPARENT,
    SOLID_DARK,
    ACCENT_TINT
}

data class SingleWidgetConfig(
    val playerId: String? = null,
    val bgStyle: WidgetBackgroundStyle = WidgetBackgroundStyle.TRANSLUCENT,
    val showTagline: Boolean = true
)

class WidgetConfigStore(private val context: Context) {

    fun getWidgetConfigFlow(appWidgetId: Int): Flow<SingleWidgetConfig> {
        val playerKey = stringPreferencesKey("widget_player_$appWidgetId")
        val bgKey = stringPreferencesKey("widget_bg_$appWidgetId")
        val taglineKey = booleanPreferencesKey("widget_tagline_$appWidgetId")

        return context.widgetConfigDataStore.data.map { prefs ->
            val playerId = prefs[playerKey]
            val bgStyleName = prefs[bgKey] ?: WidgetBackgroundStyle.TRANSLUCENT.name
            val bgStyle = try {
                WidgetBackgroundStyle.valueOf(bgStyleName)
            } catch (e: Exception) {
                WidgetBackgroundStyle.TRANSLUCENT
            }
            val showTagline = prefs[taglineKey] ?: true

            SingleWidgetConfig(
                playerId = playerId,
                bgStyle = bgStyle,
                showTagline = showTagline
            )
        }
    }

    suspend fun saveWidgetConfig(
        appWidgetId: Int,
        playerId: String,
        bgStyle: WidgetBackgroundStyle = WidgetBackgroundStyle.TRANSLUCENT,
        showTagline: Boolean = true
    ) {
        val playerKey = stringPreferencesKey("widget_player_$appWidgetId")
        val bgKey = stringPreferencesKey("widget_bg_$appWidgetId")
        val taglineKey = booleanPreferencesKey("widget_tagline_$appWidgetId")

        context.widgetConfigDataStore.edit { prefs ->
            prefs[playerKey] = playerId
            prefs[bgKey] = bgStyle.name
            prefs[taglineKey] = showTagline
        }
    }

    suspend fun removeWidgetConfig(appWidgetId: Int) {
        val playerKey = stringPreferencesKey("widget_player_$appWidgetId")
        val bgKey = stringPreferencesKey("widget_bg_$appWidgetId")
        val taglineKey = booleanPreferencesKey("widget_tagline_$appWidgetId")

        context.widgetConfigDataStore.edit { prefs ->
            prefs.remove(playerKey)
            prefs.remove(bgKey)
            prefs.remove(taglineKey)
        }
    }
}
