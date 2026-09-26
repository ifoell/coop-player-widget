package com.example.coopwidget.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.widgetConfigDataStore: DataStore<Preferences> by preferencesDataStore(name = "widget_config_prefs")

class WidgetConfigStore(private val context: Context) {

    fun getPlayerIdForWidgetFlow(appWidgetId: Int): Flow<String?> {
        val key = stringPreferencesKey("widget_player_$appWidgetId")
        return context.widgetConfigDataStore.data.map { prefs ->
            prefs[key]
        }
    }

    suspend fun savePlayerIdForWidget(appWidgetId: Int, playerId: String) {
        val key = stringPreferencesKey("widget_player_$appWidgetId")
        context.widgetConfigDataStore.edit { prefs ->
            prefs[key] = playerId
        }
    }

    suspend fun removeWidgetConfig(appWidgetId: Int) {
        val key = stringPreferencesKey("widget_player_$appWidgetId")
        context.widgetConfigDataStore.edit { prefs ->
            prefs.remove(key)
        }
    }
}
